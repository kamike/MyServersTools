package nws.mc.servers.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nws.mc.servers.data$type.CommandData;
import nws.mc.servers.helper.CommandHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandList {
    private static final String root = "servers";
    public List<CommandData> commands;
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    public CommandList(CommandDispatcher<CommandSourceStack> dispatcher) {
        this.dispatcher = dispatcher;
        commands = new ArrayList<>();
        commands.add(CommandData.create(CommandHelper::clearAll,root,"clear","all"));
        commands.add(CommandData.create(CommandHelper::clearItem,root,"clear","item"));
        commands.add(CommandData.create(CommandHelper::clearEntity,root,"clear","entity"));
        commands.add(CommandData.create(CommandHelper::trash,root,"trash"));
        commands.add(CommandData.create(CommandHelper::clearTrash,root,"trash","clear"));
    }
    public void register(){
        for (CommandData commandData : commands) {
            LiteralArgumentBuilder<CommandSourceStack> c = null;
            List<String> cs = commandData.command;
            int size = cs.size() - 1;
            for (int i = size; i >= 0; i--) {
                if (i == size) {
                    c = Commands.literal(cs.get(i))
                            .requires(commandSourceStack -> commandSourceStack.hasPermission(commandData.permission))
                            .executes(commandData::code);
                }else {
                    c = Commands.literal(cs.get(i)).then(c);
                }
            }
            System.out.println(c.getLiteral());
            dispatcher.register(c);
        }
    }
}
