package kz.timka.server;

public interface AuthenticationProvider {
    void init();
    String getNicknameByLoginAndPassword(String login, String password);
    void changeNick(String oldNick, String newNick);

    boolean icNickBusy(String nickname);

    void shutdown();
}
