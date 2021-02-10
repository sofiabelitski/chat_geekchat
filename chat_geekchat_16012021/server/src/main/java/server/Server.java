package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 9050;
    private List<ClientHandler> clients;
   private AuthService authservice;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authservice=new SimpleAuthService();
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
               new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AuthService getAuthservice() {
        return authservice;
    }

    public void broadcastMsg(ClientHandler ch,String msg){
        String str=String.format("[ %s ]: %s",ch.getNickName(),msg);
        for (ClientHandler c : clients) {
            c.sendMsg(str);
        }
    }

    public void privateMsg(ClientHandler sender,String recipientNick,String msg ){
        sender.sendMsg(String.format("[ %s ]: %s",sender.getNickName(),msg));
       for (ClientHandler c : clients) {
           if(c.getNickName().equals(recipientNick)){
               c.sendMsg(String.format("[ %s ]: %s",sender.getNickName(),msg));
           }
       }

    }
    void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }
    void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }
}
