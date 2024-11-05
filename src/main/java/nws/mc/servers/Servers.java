package nws.mc.servers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import nws.dev.core.system._File;

@Mod(Servers.MOD_ID)
public class Servers {
    public static final String MOD_ID = "neko_server";
    public static final String ConfigDir = _File.getFileFullPathWithRun("NekoServers/");
    public static final String ConfigDir_Language = ConfigDir + "Lang/";
    public static final String ConfigDir_Msg = ConfigDir + "Msg/";
    public static final String ConfigDir_Clear = ConfigDir + "Clear/";
    public static final String ConfigDir_PlayerGroup = ConfigDir + "PlayerGroup/";
    public static final String ConfigDir_BlackList = ConfigDir + "BlackList/";
    public static final String ConfigDir_JavaScript = ConfigDir + "JavaScript/";
    public static final String ConfigDir_Login = ConfigDir + "Login/";
    public Servers(IEventBus modEventBus, ModContainer modContainer) {
        _File.checkAndCreateDir(ConfigDir);
        _File.checkAndCreateDir(ConfigDir_Language);
        _File.checkAndCreateDir(ConfigDir_Msg);
        _File.checkAndCreateDir(ConfigDir_Clear);
        _File.checkAndCreateDir(ConfigDir_PlayerGroup);
        _File.checkAndCreateDir(ConfigDir_BlackList);
        _File.checkAndCreateDir(ConfigDir_JavaScript);
        _File.checkAndCreateDir(ConfigDir_Login);
    }
}
