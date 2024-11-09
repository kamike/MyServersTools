package nws.mc.servers.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.black$list.BlackListConfig;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.config.msg.MsgConfig;
import nws.mc.servers.helper.PlayerHelper;
import nws.mc.servers.helper.LoginHelper;

@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class LoggEvent {
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (BlackListConfig.instance.getDatas().enable) {
                boolean has = PlayerHelper.checkPlayer(serverPlayer, BlackListConfig.instance.getDatas().list);
                if (BlackListConfig.instance.getDatas().allowedMode) {
                    if (!has)
                        serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
                } else {
                    if (has)
                        serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
                }

            }
            if (LoginConfig.INSTANCE.getDatas().enable) LoginHelper.openLogin(serverPlayer);
            if (MsgConfig.firstJoin.getDatas().isEnable()) MsgConfig.firstJoin.send(serverPlayer);
            if (MsgConfig.everyDayJoin.getDatas().isEnable()) MsgConfig.everyDayJoin.send(serverPlayer);
            if (MsgConfig.everyJoin.getDatas().isEnable()) MsgConfig.everyJoin.send(serverPlayer);

        }
    }
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginConfig.INSTANCE.getDatas().enable){
                LoginHelper.removeLogin(serverPlayer);
            }
        }
    }
}
