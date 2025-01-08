package nws.mc.servers.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.Config;
import nws.mc.servers.config.Language;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.listen.ListenConfig;
import nws.mc.servers.config.login.LoginConfig;
import nws.mc.servers.helper.ClearHelper;
import nws.mc.servers.helper.LoginHelper;
import nws.mc.servers.helper.MsgHelper;
import nws.mc.servers.listen.Listen;

import java.io.IOException;
import java.util.HashMap;

@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class TickEvent {
    private static boolean runThread = false;
    private static int time = 0;
    private static int itemTime = 0;
    private static boolean clearEntity = false;
    private static boolean clearItem = false;
    private static final String[] msg = {"",""};

    private static final Thread tt = new Thread(() -> {
        while (runThread) {
            try {
                if (!clearItem && ClearConfig.ITEM_CLEAR.getDatas().enable && ClearConfig.ITEM_CLEAR.getDatas().autoClearTime > 0) {
                    if (itemTime < ClearConfig.ITEM_CLEAR.getDatas().autoClearTime) {
                        int msgIndex = ClearConfig.ITEM_CLEAR.getDatas().autoClearTime - itemTime;
                        if (ClearConfig.ITEM_CLEAR.getDatas().msg.containsKey(msgIndex))
                            msg[1] = ClearConfig.ITEM_CLEAR.getDatas().msg.getOrDefault(msgIndex, "");
                        itemTime++;
                    } else {
                        itemTime = 0;
                        clearItem = true;
                    }
                }
                if (!clearEntity && ClearConfig.ENTITY_CLEAR.getDatas().enable && ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime > 0) {
                    if (time < ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime) {
                        int msgIndex = ClearConfig.ENTITY_CLEAR.getDatas().autoClearTime - time;
                        if (ClearConfig.ENTITY_CLEAR.getDatas().msg.containsKey(msgIndex))
                            msg[0] = ClearConfig.ENTITY_CLEAR.getDatas().msg.getOrDefault(msgIndex, "");
                        time++;
                    } else {
                        time = 0;
                        clearEntity = true;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        time = 0;
        itemTime = 0;
        clearEntity = false;
        clearItem = false;
        msg[0] = "";
        msg[1] = "";
    });
    private static boolean ThreadIsRun = false;
    @SubscribeEvent
    public static void onStart(ServerStartedEvent event){
        runThread = true;
        if (!ThreadIsRun) {
            ThreadIsRun = true;
            tt.start();
        }
        if (ListenConfig.I.getDatas().isEnable()) Listen.I.start();
    }
    @SubscribeEvent
    public static void onStop(ServerStoppingEvent event){
        runThread = false;
        Listen.I.close();
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Pre event){
        if (clearEntity) {
            ClearHelper.clearServerEntity(event.getServer());
            clearEntity = false;
        }
        if (clearItem) {
            ClearHelper.clearItem(event.getServer());
            clearItem = false;
        }
        for (int i = 0; i < 2; i++) {
            if (!msg[i].isEmpty()) {
                MsgHelper.sendServerMsg(event.getServer(),msg[i]);
                msg[i] = "";
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
