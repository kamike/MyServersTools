package nws.mc.servers.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.Config;
import nws.mc.servers.config.Language;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.helper.ClearHelper;
import nws.mc.servers.helper.LoginHelper;
import nws.mc.servers.helper.MsgHelper;

import java.util.HashMap;

@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class TickEvent {
    private static int tick = 0;
    private static int time = 0;
    private static int itemTime = 0;
    private static final Thread tt = new Thread(() -> {
        while (true) {
            try {
                if (itemTime < ClearConfig.ITEM_CLEAR.getDatas().autoClearTime) itemTime++;
                if (time < ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime) time++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });
    @SubscribeEvent
    public static void onTick(ServerTickEvent.Pre event){
        if (tick < 20) {
            tick++;
            return;
        }
        tick = 0;
        itemTime++;
        time++;
        if (ClearConfig.ENTITY_CLEAR.getDatas().enable && ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime > 0) {
            if (time >= ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime) {
                time = 0;
                ClearHelper.clearServerEntity(event.getServer());
            } else {
                int msgIndex = ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime - time;
                if (ClearConfig.ENTITY_CLEAR.getDatas().msg.containsKey(msgIndex)) {
                    MsgHelper.sendServerMsg(event.getServer(), ClearConfig.ENTITY_CLEAR.getDatas().msg.getOrDefault(itemTime, ""));
                }
            }
        }
        if (ClearConfig.ITEM_CLEAR.getDatas().enable && ClearConfig.ITEM_CLEAR.getDatas().autoClearTime > 0) {
            if (itemTime >= ClearConfig.ITEM_CLEAR.getDatas().autoClearTime) {
                itemTime = 0;
                ClearHelper.clearItem(event.getServer());
            } else {
                int msgIndex = ClearConfig.ITEM_CLEAR.getDatas().autoClearTime - itemTime;
                if (ClearConfig.ITEM_CLEAR.getDatas().msg.containsKey(msgIndex)) {
                    MsgHelper.sendServerMsg(event.getServer(), ClearConfig.ITEM_CLEAR.getDatas().msg.getOrDefault(msgIndex, ""));
                }
            }
        }
    }
    private static final HashMap<String,Integer> loginTime = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event){
            if (event.getEntity() instanceof ServerPlayer serverPlayer && LoginHelper.checkLogin(serverPlayer)){
                String uuid = serverPlayer.getStringUUID();
                loginTime.put(uuid,loginTime.getOrDefault(uuid,0)+1);
                if (loginTime.get(uuid) >= LoginConfig.INSTANCE.getDatas().time){
                    serverPlayer.connection.disconnect(Language.getComponent("login.failed"));
                    loginTime.remove(uuid);
                }
            }

    }




    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event){
        if (event.getEntity().level().isClientSide()) return;
        if (Config.I.getDatas().clearAnomalousEntity) {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                if (livingEntity.getPose().equals(Pose.DYING)) {
                    if (livingEntity.deathTime > 20) {
                        livingEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
        }
    }
}
