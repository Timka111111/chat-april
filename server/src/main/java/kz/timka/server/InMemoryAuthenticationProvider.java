package kz.timka.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider{
    private class User {
        private String login;
        private String password;
        private String username;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<User> users;

    public InMemoryAuthenticationProvider() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("Bob@gmail.com", "100", "Bob"),
                new User("Jack@gmail.com", "100", "Jack"),
                new User("Ben@gmail.com", "100", "Ben")
        ));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for(User u : users) {
            if(u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void changeNick(String oldNick, String newNick) {
        for(User u : users) {
            if(u.username.equals(oldNick)) {
                u.username = newNick;
                return;
            }
        }
    }
}
