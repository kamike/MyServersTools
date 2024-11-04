package nws.mc.servers.helper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import nws.mc.servers.config.Language;
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
}
