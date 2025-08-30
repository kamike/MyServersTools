package nws.mc.servers.config.login;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

import java.util.HashMap;

public class LoginConfig extends _JsonConfig<LoginData> {
    public static final String file = Servers.ConfigDir_Login + "Login.json";
    public static final LoginConfig INSTANCE = new LoginConfig();
    public LoginConfig() {
        super(file, """
                {
                    "enable": true,
                    "time": 1200,
                    "success": "login.success",
                    "fail": "login.failed",
                    "minPasswordLength": 6,
                    "maxPasswordLength": 9,
                    "password": {}
                }
                """, new TypeToken<>(){});
    }
    public HashMap<String, String> getPasswords() {
        if (getDatas().getPasswords() == null)  getDatas().setPasswords(new HashMap<>());
        return getDatas().getPasswords();
    }
}
