package nws.mc.servers.menu;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import nws.mc.servers.config.Language;
import nws.mc.servers.helper.LoginHelper;

public class LoginMenu extends AbstractContainerMenu {
    private final ItemStackHandler itemHandler;
    private final ServerPlayer serverPlayer;
    private final int[] inputSlots = {
            12,13,14,
            21,22,23,
            30,31,32
    };
    public LoginMenu(int pContainerId, ServerPlayer serverPlayer) {
        super(MenuType.GENERIC_9x6, pContainerId);
        this.serverPlayer = serverPlayer;
        this.itemHandler = new ItemStackHandler(54);
        int a = 0;
        ItemStack itemStack;
        for (int i : inputSlots) {
            a++;
            itemStack = new ItemStack(Items.ENDER_EYE);
            itemStack.set(DataComponents.CUSTOM_NAME, Component.literal(String.valueOf(a)));
            itemHandler.setStackInSlot(i,itemStack);
        }
        itemStack = new ItemStack(Items.BREEZE_ROD);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("login.menu.button.clear"));
        itemHandler.setStackInSlot(39,itemStack);

        itemStack = new ItemStack(Items.ENDER_PEARL);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("0"));
        itemHandler.setStackInSlot(40,itemStack);

        itemStack = new ItemStack(Items.BLAZE_ROD);
        itemStack.set(DataComponents.CUSTOM_NAME, Language.getComponent("login.menu.button.login"));
        itemHandler.setStackInSlot(41,itemStack);
        addSlots();
    }
    private void addSlots(){
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col;
                this.addSlot(new SlotButton(itemHandler, slot, 8 + col * 18, 18 + row * 18, player -> input(itemHandler.getStackInSlot(slot))));
            }
        }
    }

    private void input(ItemStack stack) {
        if (stack.getItem() == Items.ENDER_EYE || stack.getItem() == Items.ENDER_PEARL) {
            Component name = stack.get(DataComponents.CUSTOM_NAME);
            for (int i = 0; i < 9; i++) {
                if (itemHandler.getStackInSlot(i).isEmpty()) {
                    ItemStack itemStack = new ItemStack(Items.NETHER_STAR);
                    itemStack.set(DataComponents.CUSTOM_NAME, name);
                    itemHandler.setStackInSlot(i, itemStack);
                    break;
                }
            }
            return;
        }
        if (stack.getItem() == Items.BREEZE_ROD) {
            for (int i = 8; i >= 0; i--) {
                if (!itemHandler.getStackInSlot(i).isEmpty()) {
                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        } else if (stack.getItem() == Items.BLAZE_ROD) {
            StringBuilder p = new StringBuilder();
            boolean f = true;
            for (int i = 0; i < 9; i++) {
                ItemStack itemStack = itemHandler.getStackInSlot(i);
                if (itemStack.getItem() == Items.NETHER_STAR) {
                    Component name = itemStack.get(DataComponents.CUSTOM_NAME);
                    if (name != null) {
                        p.append(name.getString());
                    }
                } else {
                    f = false;
                    break;
                }
            }
            if (f && LoginHelper.Login(serverPlayer, p.toString())) {
                serverPlayer.closeContainer();
            } else {
                error();
            }
        }

    }
    public void error(){
        for (int j = 8; j >= 0; j--) {
            itemHandler.setStackInSlot(j, new ItemStack(Items.BARRIER));
        }
    }


    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void removed(Player pPlayer) {
        if (!LoginHelper.isLogin((ServerPlayer) pPlayer)){
            serverPlayer.connection.disconnect(Language.getComponent("login.failed"));
        }
        super.removed(pPlayer);
    }
}
