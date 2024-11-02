package nws.mc.servers.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class TrashMenu extends ChestMenu {
    public TrashMenu( int pContainerId, Inventory pPlayerInventory, Container pContainer) {
        super(MenuType.GENERIC_9x6, pContainerId, pPlayerInventory,pContainer, 6);
    }
}
