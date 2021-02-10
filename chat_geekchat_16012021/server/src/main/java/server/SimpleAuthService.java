package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{
    private class UserData{
        String login;
        String nickName;
        String password;

        public UserData(String login, String password,String nickName) {
            this.nickName = nickName;
            this.password = password;
            this.login=login;
        }
    }

   private List<UserData> users;

    public SimpleAuthService() {
        users = new ArrayList<>();
        users.add(new UserData("qwe","qwe","qwe"));
        users.add(new UserData("sof","sof","sof"));
        users.add(new UserData("ida","ida","ida"));
        for (int i = 0; i < 10; i++) {

            users.add(new UserData("user"+i,"user"+i,"user"+i));
        }
    }

    @Override
    public String getNickByLogAndPass(String log, String password) {
        for (UserData ud:users) {
            if(ud.login.equals(log)&&ud.password.equals(password)){
                return ud.nickName;

            }
        }
        return null;
    }
}
