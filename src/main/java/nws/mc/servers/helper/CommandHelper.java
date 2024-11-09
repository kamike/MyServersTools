package nws.mc.servers.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import nws.mc.servers.config.Config;
import nws.mc.servers.config.Language;
import nws.mc.servers.config.ban$item.BanItemConfig;
import nws.mc.servers.config.black$list.BlackListConfig;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.command.CommandConfig;
import nws.mc.servers.config.login$reward.LoginReward;
import nws.mc.servers.config.msg.MsgConfig;
import nws.mc.servers.config.player$data.PlayerData;
import nws.mc.servers.config.player$group.PlayerGroupConfig;
import nws.mc.servers.data$type.PosData;
import nws.mc.servers.menu.LoginMenu;
import nws.mc.servers.menu.TrashBinContainer;

import java.util.HashMap;
import java.util.UUID;

public class CommandHelper {

    public static int check(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(()->Component.literal("servers test"), false);
        return Command.SINGLE_SUCCESS;
    }
    public static int trash(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().trash) {
            ServerPlayer player = context.getSource().getPlayer();
            if (player == null) return 0;
            if (ClearHelper.isClearTrashBin) {
                context.getSource().sendSuccess(() -> Language.getComponent("trash.command.error.cleaning"), false);
                return 1;
            }
            if (TrashBinContainer.nowPlayer != null) {
                context.getSource().sendSuccess(() -> Language.getComponent("trash.command.error.has_player"), false);
                return 1;
            }
            TrashBinContainer.nowPlayer = player;
            player.openMenu(new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new TrashBinContainer(id, playerInventory),
                    Language.getComponent("trash.menu.title")
            ));
            return 1;
        }
        return 0;
    }
    public static int login(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            player.openMenu(new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new LoginMenu(id,player),
                    Language.getComponent("password.menu.title")
            ));
        }
        return 1;
    }
    public static int clearAll(CommandContext<CommandSourceStack> context){
        ClearHelper.clearServerEntity(context.getSource().getServer());
        ClearHelper.clearItem(context.getSource().getServer());
        return 1;
    }
    public static int clearEntity(CommandContext<CommandSourceStack> context) {
        ClearHelper.clearServerEntity(context.getSource().getServer());
        return 1;
    }
    public static int clearItem(CommandContext<CommandSourceStack> context) {
        ClearHelper.clearItem(context.getSource().getServer());
        return 1;
    }
    public static int clearTrash(CommandContext<CommandSourceStack> context) {
        TrashBinContainer.SLOTS.clear();
        return 1;
    }

    public static int reload(CommandContext<CommandSourceStack> context) {
        Config.I.init();
        PlayerGroupConfig.GROUPS.clear();
        PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
        ClearConfig.ENTITY_CLEAR.init();
        ClearConfig.ITEM_CLEAR.init();
        MsgConfig.firstJoin.init();
        MsgConfig.everyDayJoin.init();
        MsgConfig.everyJoin.init();
        Language.I.init();
        BlackListConfig.instance.init();
        BanItemConfig.I.init();
        LoginReward.I.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.all"), false);
        return 1;
    }
    public static int reloadConfig(CommandContext<CommandSourceStack> context) {
        Config.I.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.config"), false);
        return 1;
    }
    public static int reloadPlayerGroup(CommandContext<CommandSourceStack> context) {
        PlayerGroupConfig.GROUPS.clear();
        PlayerGroupConfig.GROUPS.putAll(PlayerGroupConfig.getGroups());
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.player_group"), false);
        return 1;
    }
    public static int reloadClear(CommandContext<CommandSourceStack> context) {
        ClearConfig.ENTITY_CLEAR.init();
        ClearConfig.ITEM_CLEAR.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.clear"), false);
        return 1;
    }
    public static int reloadMsg(CommandContext<CommandSourceStack> context) {
        MsgConfig.firstJoin.init();
        MsgConfig.everyDayJoin.init();
        MsgConfig.everyJoin.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.msg"), false);
        return 1;
    }
    public static int reloadLanguage(CommandContext<CommandSourceStack> context) {
        Language.I.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.language"), false);
        return 1;
    }
    public static int reloadBlackList(CommandContext<CommandSourceStack> context) {
        BlackListConfig.instance.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.black_list"), false);
        return 1;
    }
    public static int reloadBanItem(CommandContext<CommandSourceStack> context) {
        BanItemConfig.I.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.ban_item"), false);
        return 1;
    }
    public static int reloadReward(CommandContext<CommandSourceStack> context) {
        LoginReward.I.init();
        context.getSource().sendSuccess(()->Language.getComponent("reload.success.reward"), false);
        return 1;
    }
    public static int reward(CommandContext<CommandSourceStack> context) {
        if (LoginReward.I.getDatas().enable && CommandConfig.I.getDatas().reward) {
            ServerPlayer ser = context.getSource().getPlayer();
            if (ser == null) return 0;
            LoginRewardHelper.getRewards(ser);
            return 1;

        }
        return 0;
    }

    public static int setHome(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().setHome) {
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                PlayerData pd = PlayerData.get(player.getStringUUID());
                //player.level().dimension().toString();
                pd.setHome(PosData.create(player));
                context.getSource().sendSuccess(() -> Language.getComponent("command.set_home.success"), false);
                return 1;
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.set_home.failed"));
        return 0;
    }

    public static int home(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().home) {


            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                PlayerData pd = PlayerData.get(player.getStringUUID());
                PosData home = pd.getDatas().getHome();
                if (home != null) {
                    pd.addBack(player);
                    if (_MF.tp(player, home)) {
                        context.getSource().sendSuccess(() -> Language.getComponent("command.home.success"), false);
                        return 1;
                    }
                }
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.home.failed"));
        return 0;
    }
    public static int back(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().back) {


            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                PlayerData pd = PlayerData.get(player.getStringUUID());
                PosData back = pd.getDatas().getBack();
                if (back != null) {
                    //pd.getDatas().addBack(PosData.create(player));
                    if (_MF.tp(player, back)) {
                        context.getSource().sendSuccess(() -> Language.getComponent("command.back.success"), false);
                        return 1;
                    }
                }
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.back.failed"));
        return 0;
    }
    private static final HashMap<String, UUID> tpaQueue = new HashMap<>();
    public static int tpa(CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) {
        if (CommandConfig.I.getDatas().tpa) {
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null && targetPlayer != null) {
                tpaQueue.put(targetPlayer.getStringUUID(), player.getUUID());
                context.getSource().sendSuccess(() -> Language.getComponent("command.tpa.success"), false);
                HashMap<String, Object> map = new HashMap<>();

                /*
                player.sendSystemMessage(player.getName().copy()
                        .append(Component.literal(" has sent you a transmission request.")
                                .withStyle(ChatFormatting.GOLD))
                        .append(Component.literal("Check Here And Accept").withStyle(
                        style -> style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaAccept")))
                ));

                 */


                map.put("_tpa_send_player", player);
                MsgHelper.sendWithArgs(targetPlayer, Language.get("command.tpa.msg"), map);
                //MsgHelper.sendMsgToPlayerF(targetPlayer, Language.get("command.tpa.msg"), MsgHelper.createMsgMap("$tpa.send.player", player.getName().getString()));
                return 1;
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.tpa.failed"));
        return 0;
    }

    public static int tpaAccept(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().tpaAccept) {
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                if (tpaQueue.containsKey(player.getStringUUID()) && player.getServer() != null) {
                    ServerPlayer targetPlayer = player.getServer().getPlayerList().getPlayer(tpaQueue.get(player.getStringUUID()));
                    if (targetPlayer != null) {
                        targetPlayer.teleportTo((ServerLevel) player.level(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        context.getSource().sendSuccess(() -> Language.getComponent("command.tpa_accept.success"), false);
                        tpaQueue.remove(player.getStringUUID());
                        return 1;
                    }
                }
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.tpa_accept.failed"));
        return 0;
    }

    public static int tpaDeny(CommandContext<CommandSourceStack> context) {
        if (CommandConfig.I.getDatas().tpaDeny) {
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                if (tpaQueue.containsKey(player.getStringUUID())) {
                    tpaQueue.remove(player.getStringUUID());
                    context.getSource().sendSuccess(() -> Language.getComponent("command.tpa_deny.success"), false);
                    return 1;
                }
            }
        }
        context.getSource().sendFailure(Language.getComponent("command.tpa_deny.failed"));
        return 0;
    }

}
