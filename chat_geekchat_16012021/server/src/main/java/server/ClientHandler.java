package server;

import commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickName;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл для аутентификации
                    while (true) {

                        String str = in.readUTF();
                        if (str.startsWith(Command.AUTH)) {
                            String[] token = str.split("\\s");
                            String newNick = server.getAuthservice().getNickByLogAndPass(token[1], token[2]);
                            if (newNick != null) {
                                nickName = newNick;
                                sendMsg(Command.AUTH_OK+" " + nickName);
                                server.subscribe(this);
                                System.out.println("client " + nickName + " connected" + socket.getRemoteSocketAddress());
                                break;
                            } else {
                                sendMsg("неверный логин / пароль");
                            }
                        }
                        if (str.equals(Command.END)) {
                            sendMsg(Command.END);
                            System.out.println("client disconnected");
                            break;
                        }

                    }
                    //цикл для работы
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals(Command.END)) {
                            sendMsg(Command.END);
                            System.out.println("client disconnected");
                            break;
                        }
                        if (str.startsWith(Command.PRIVATE_MSG)){
                            String[] token=str.split("\\s",3);
                            server.privateMsg(this,token[1],token[2]);

                        }else {
                            server.broadcastMsg(this, str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickName() {
        return nickName;
    }
}
