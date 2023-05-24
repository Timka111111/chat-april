package kz.timka.server;

public interface AuthenticationProvider {
    String getNicknameByLoginAndPassword(String login, String password);
    void changeNick(String oldNick, String newNick);
}
