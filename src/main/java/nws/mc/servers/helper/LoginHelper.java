package nws.mc.servers.helper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import nws.dev.core.bytes._Byte;
import nws.mc.servers.config.Language;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.menu.LoginMenu;

import java.util.ArrayList;
import java.util.List;

public class LoginHelper {
    private static final List<String> players = new ArrayList<>();

    public static boolean checkLogin(ServerPlayer serverPlayer) {
        if (LoginConfig.INSTANCE.getDatas().enable) {
            return !LoginHelper.isLogin(serverPlayer);
        }
        return false;
    }




    public static void openLogin(ServerPlayer serverPlayer) {
        if (!serverPlayer.isAlive()){
            serverPlayer.setHealth(serverPlayer.getMaxHealth());
            serverPlayer.respawn();
        }
        serverPlayer.openMenu(new SimpleMenuProvider(
                (id, playerInventory, playerEntity) -> new LoginMenu(id,serverPlayer),
                Language.getComponent("login.menu.title")
        ));
    }



    public static boolean isLogin(ServerPlayer player) {
        return players.contains(player.getStringUUID());
    }

    public static void addLogin(ServerPlayer player) {
        players.add(player.getStringUUID());
    }

    public static void removeLogin(ServerPlayer player) {
        players.remove(player.getStringUUID());
    }

    public static boolean Login(ServerPlayer player,String password) {
        if (isLogin(player)) {
            MsgHelper.sendMsgToPlayerF(player,LoginConfig.INSTANCE.getDatas().fail);
            return false;
        }
        if (!LoginConfig.INSTANCE.getPasswords().containsKey(player.getStringUUID())) {
            LoginConfig.INSTANCE.getPasswords().put(player.getStringUUID(),encodePassword(password));
            LoginConfig.INSTANCE.save();
            addLogin(player);
            MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.getDatas().success);
            return true;
        }else {
            if (LoginConfig.INSTANCE.getPasswords().get(player.getStringUUID()).equals(encodePassword(password))) {
                addLogin(player);
                MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.getDatas().success);
                return true;
            }
            MsgHelper.sendMsgToPlayerF(player, LoginConfig.INSTANCE.getDatas().fail);
            return false;
        }
    }
    public static String encodePassword(String password) {
        return _Byte.getMd5(password);
    }
}
