package nws.mc.servers.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.helper.LoginHelper;

@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class DamageEvent {
    @SubscribeEvent
    public static void onDamageFirst(EntityInvulnerabilityCheckEvent event){
        if (LoginConfig.INSTANCE.getDatas().enable) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                if (!LoginHelper.isLogin(serverPlayer)) event.setInvulnerable(true);
            }
        }
    }
}
