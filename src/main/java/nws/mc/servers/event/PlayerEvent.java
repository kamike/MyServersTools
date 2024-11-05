package nws.mc.servers.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.helper.LoginHelper;

@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class PlayerEvent {
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }

    public static void onInteraction(PlayerInteractEvent.RightClickEmpty event){
        if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
            //event..setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (LoginHelper.checkLogin(serverPlayer)) {
                event.setCanceled(true);
            }
        }
    }

    public static void onInteraction(PlayerInteractEvent.LeftClickEmpty event){
        if (LoginHelper.checkLogin((ServerPlayer) event.getEntity())) {
            //event.setCanceled(true);
        }
    }


}
