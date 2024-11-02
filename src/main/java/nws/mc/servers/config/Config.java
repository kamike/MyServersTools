package nws.mc.servers.config;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

public class Config extends _JsonConfig<ConfigData> {
    private static final String file = Servers.ConfigDir + "config.json";
    public static final Config I = new Config();
    public Config() {
        super(file, """
                {
                    "clearAnomalousEntity": true,
                    "lang": "auto"
                }
                """, new TypeToken<>(){});
    }
}
