package nws.mc.servers.event;

import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nws.dev.core.math._Math;
import nws.mc.servers.Servers;
import nws.mc.servers.config.Config;
import nws.mc.servers.config.black$list.BlackListConfig;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.msg.MsgConfig;
import nws.mc.servers.data$type.ClearList;
import nws.mc.servers.helper.ClearHelper;
import nws.mc.servers.helper.CommandHelper;
import nws.mc.servers.helper.MsgHelper;
import nws.mc.servers.helper.PlayerHelper;

//@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class GameEvent {
    private static final int Permission_OP = 2;
    private static final int Permission_Player = 0;
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
    public static void onStart(ServerStartedEvent event){
        //tt.start();
    }
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        if (BlackListConfig.instance.getDatas().enable) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                boolean has = PlayerHelper.checkPlayer(serverPlayer, BlackListConfig.instance.getDatas().list);
                if (BlackListConfig.instance.getDatas().allowedMode) {
                    if (!has) serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
                }else {
                    if (has) serverPlayer.connection.disconnect(Component.literal(BlackListConfig.instance.getDatas().msg));
                }
            }
        }
    }
    @SubscribeEvent
    public static void regCommand(RegisterCommandsEvent event) {
        CommandList commandList = new CommandList(event.getDispatcher());
        commandList.register();
        /*
        event.getDispatcher()
                .register(Commands.literal("servers")
                        .then(Commands.literal("trash")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_Player))
                                .executes(CommandHelper::trash)
                                .then(Commands.literal("clear")
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_OP))
                                        .executes(CommandHelper::clearTrash))
                        )
                        .then(Commands.literal("clear")
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_OP))
                                .then(Commands.literal("all")
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_OP))
                                        .executes(CommandHelper::clearAll))
                                .then(Commands.literal("item")
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_OP))
                                        .executes(CommandHelper::clearItem))
                                .then(Commands.literal("entity")
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(Permission_OP))
                                        .executes(CommandHelper::clearEntity))
                        )
                );

         */
    }

    public static int wait = 0;
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (wait > 0) {
            wait--;
            return;
        }
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (MsgConfig.firstJoin.getDatas().isEnable()) MsgConfig.firstJoin.send(serverPlayer);
            if (MsgConfig.everyDayJoin.getDatas().isEnable()) MsgConfig.everyDayJoin.send(serverPlayer);
            if (MsgConfig.everyJoin.getDatas().isEnable()) MsgConfig.everyJoin.send(serverPlayer);
        }else if (event.getEntity() instanceof LivingEntity livingEntity){
            if (ClearConfig.ENTITY_CLEAR.getDatas().enable){
                int entityLimit = ClearConfig.ENTITY_CLEAR.getEntityLimit(livingEntity);
                if (entityLimit == 0 || ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit == 0) return;
                if (ClearConfig.ENTITY_CLEAR.isInWhiteList(livingEntity)) return;
                if (livingEntity.level() instanceof ServerLevel serverLevel){
                    ClearList clearList = new ClearList();
                    ClearList allClearList = new ClearList();
                    int[] count ={0,0,0};
                    Iterable<Entity> entities = serverLevel.getAllEntities();
                    for (Entity entity : entities) {
                        if (entity instanceof ServerPlayer) continue;
                        allClearList.add(entity);
                        count[0]++;
                        if (entity.getType() == livingEntity.getType()) {
                            clearList.add(entity);
                            if (count[1] >= entityLimit){
                                count[2] = 1;
                                break;
                            }
                            count[1]++;
                        }
                        if (count[0] >= ClearConfig.ENTITY_CLEAR.getDatas().allEntityLimit){
                            count[2] = 2;
                            break;
                        }
                    }
                    if (count[2] == 1) {
                        if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) event.setCanceled(true);
                        MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
                        MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(clearList.clearEntity()));
                    }else if (count[2] == 2) {
                        wait = 200;
                        if (ClearConfig.ENTITY_CLEAR.getDatas().stopSpawn) event.setCanceled(true);
                        MsgHelper.sendServerMsg(serverLevel, ClearConfig.ENTITY_CLEAR.getDatas().LimitClearMsg);
                        MsgHelper.sendServerMsg(serverLevel.getServer(), ClearConfig.ENTITY_CLEAR.getMsg(0), ClearHelper.pullClearCount(allClearList.clearEntityWithCheck()));

                    }
                }
            }
        }
    }
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
