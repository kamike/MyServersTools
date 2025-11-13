package nws.mc.servers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import nws.dev.core.pack._Pack;
import nws.dev.core.system._File;
import nws.mc.servers.listen.ListenHandleRegister;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.io.File;

@Mod(Servers.MOD_ID)
public class Servers {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "neko_server";
    public static final String ConfigDir = java.nio.file.Paths.get("", "NekoServers").toAbsolutePath().toString() + File.separator;
    public static final String ConfigDir_Language = ConfigDir + "Lang" + File.separator;
    public static final String ConfigDir_Msg = ConfigDir + "Msg" + File.separator;
    public static final String ConfigDir_Clear = ConfigDir + "Clear" + File.separator;
    public static final String ConfigDir_PlayerGroup = ConfigDir + "PlayerGroup" + File.separator;
    public static final String ConfigDir_BlackList = ConfigDir + "BlackList" + File.separator;
    public static final String ConfigDir_JavaScript = ConfigDir + "JavaScript" + File.separator;
    public static final String ConfigDir_Login = ConfigDir + "Login" + File.separator;
    public static final String ConfigDir_Data = ConfigDir + "Data" + File.separator;
    public static final String ConfigDir_PlayerData = ConfigDir_Data + "Player" + File.separator;
    public static final String ConfigDir_ListenHandle = ConfigDir + "ListenHandle" + File.separator;
    private void init() {
        try {
            _File.checkAndCreateDir(ConfigDir);
            _File.checkAndCreateDir(ConfigDir_Language);
            _File.checkAndCreateDir(ConfigDir_BlackList);
            _File.checkAndCreateDir(ConfigDir_Login);
            _Pack.writeFiles("assets/servers/lang/", Servers.ConfigDir_Language, ".json", "zh_cn");
        } catch (Exception e) {
            LOGGER.error("**********neko_server******Servers initialization failed", e);
        }
    }
    public Servers(IEventBus modEventBus, ModContainer modContainer) {
        init();
        ListenHandleRegister.register(modEventBus);
    }
}
