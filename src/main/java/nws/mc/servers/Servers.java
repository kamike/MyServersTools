package nws.mc.servers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import nws.dev.core.pack._Pack;
import nws.dev.core.system._File;
import nws.mc.servers.listen.ListenHandleRegister;

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
    public static final String ConfigDir_Data = ConfigDir + "Data/";
    public static final String ConfigDir_PlayerData = ConfigDir_Data + "Player/";
    public static final String ConfigDir_ListenHandle = ConfigDir + "ListenHandle/";
    static {
        _File.checkAndCreateDir(ConfigDir);
        _File.checkAndCreateDir(ConfigDir_Language);
        _File.checkAndCreateDir(ConfigDir_Msg);
        _File.checkAndCreateDir(ConfigDir_Clear);
        _File.checkAndCreateDir(ConfigDir_PlayerGroup);
        _File.checkAndCreateDir(ConfigDir_BlackList);
        _File.checkAndCreateDir(ConfigDir_JavaScript);
        _File.checkAndCreateDir(ConfigDir_Login);
        _File.checkAndCreateDir(ConfigDir_Data);
        _File.checkAndCreateDir(ConfigDir_PlayerData);
        _File.checkAndCreateDir(ConfigDir_ListenHandle);


        _Pack.writeFiles("assets/servers/lang/",Servers.ConfigDir_Language,".json","zh_cn");
        _Pack.writeFiles("assets/servers/js/",Servers.ConfigDir_JavaScript,".js","TpaRequest");
    }
    public Servers(IEventBus modEventBus, ModContainer modContainer) {
        ListenHandleRegister.register(modEventBus);
    }
}
