package nws.mc.servers.config.command;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

public class CommandConfig extends _JsonConfig<CommandData> {
    public static final String filePath = Servers.ConfigDir + "Commands.json";
    public static final CommandConfig I = new CommandConfig();
    public CommandConfig() {
        super(filePath, """
                {
                    "home": true,
                    "setHome": true,
                    "back": true,
                    "backMaxCount": 5,
                    "tpa": true,
                    "tpaAccept": true,
                    "tpaDeny": true,
                    "reward": true,
                    "trash": true
                }
                """, new TypeToken<>() {});
    }
}
