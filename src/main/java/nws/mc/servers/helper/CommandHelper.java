package nws.mc.servers.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import nws.mc.servers.config.Config;
import nws.mc.servers.config.Language;
import nws.mc.servers.config.black$list.BlackListConfig;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.config.msg.MsgConfig;
import nws.mc.servers.config.player$group.PlayerGroupConfig;
import nws.mc.servers.menu.LoginMenu;
import nws.mc.servers.menu.TrashBinContainer;

public class CommandHelper {

    public static int check(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(()->Component.literal("servers test"), false);
        return Command.SINGLE_SUCCESS;
    }
    public static int trash(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (ClearHelper.isClearTrashBin){
            context.getSource().sendSuccess(()-> Language.getComponent("trash.command.error.cleaning"), false);
            return 1;
        }
        if (TrashBinContainer.nowPlayer != null){
            context.getSource().sendSuccess(()-> Language.getComponent("trash.command.error.has_player"), false);
            return 1;
        }
        TrashBinContainer.nowPlayer = player;
        player.openMenu(new SimpleMenuProvider(
                (id, playerInventory, playerEntity) -> new TrashBinContainer(id, playerInventory),
                Language.getComponent("trash.menu.title")
        ));
        return 1;
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
}
