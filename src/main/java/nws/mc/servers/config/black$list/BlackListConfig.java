package nws.mc.servers.config.black$list;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

public class BlackListConfig extends _JsonConfig<BlackListData> {
    private static final String filePath = Servers.ConfigDir_BlackList + "blackList.json";
    public static final BlackListConfig instance = new BlackListConfig();
    public BlackListConfig() {
        super(filePath, """
                {
                    "enable": true,
                    "allowedMode": false,
                    "msg": "blacklist.deny",
                    "list": []
                }
                """, new TypeToken<>() {});
    }
}
