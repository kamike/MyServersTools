package nws.mc.servers.config.login;

import java.util.HashMap;

public class LoginData {
    public boolean enable;
    public String success;
    public String fail;
    public int time;
    public int minPasswordLength;
    public int maxPasswordLength;
    private HashMap<String, String> passwords;

    public HashMap<String, String> getPasswords() {
        return passwords;
    }

    public void setPasswords(HashMap<String, String> passwords) {
        this.passwords = passwords;
    }
}
