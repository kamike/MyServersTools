package nws.mc.servers.event;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import nws.mc.servers.Servers;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.command.CommandConfig;
import nws.mc.servers.config.player$data.PlayerData;
import nws.mc.servers.data$type.ClearList;
import nws.mc.servers.helper.*;

//@OnlyIn(Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = Servers.MOD_ID,bus = EventBusSubscriber.Bus.GAME)
public class GameEvent {
    @SubscribeEvent
    public static void onStart(ServerStartedEvent event){
        //tt.start();
    }

    @SubscribeEvent
    public static void onPickup(ItemEntityPickupEvent.Pre event){
        if (BanItemHelper.checkItemAndSend(event.getItemEntity())) {
            event.getItemEntity().discard();
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void regCommand(RegisterCommandsEvent event) {
        CommandList commandList = new CommandList(event.getDispatcher());
        commandList.register();
        event.getDispatcher()
                .register(Commands.literal("tpa")
                        .then(Commands.argument("player",EntityArgument.player())
                                .executes(context -> CommandHelper.tpa(context, EntityArgument.getPlayer(context, "player")))));

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
        if (BanItemHelper.checkItemAndSend(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (wait > 0) {
            wait--;
            return;
        }
        if (event.getEntity() instanceof LivingEntity livingEntity){
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
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (CommandConfig.I.getDatas().back){
                PlayerData.get(serverPlayer.getStringUUID()).addBack(serverPlayer);
            }
        }
    }
}
