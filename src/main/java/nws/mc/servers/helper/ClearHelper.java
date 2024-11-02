package nws.mc.servers.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import nws.mc.servers.config.clear.ClearConfig;
import nws.mc.servers.menu.TrashBinContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ClearHelper {
    public static boolean isClearTrashBin = false;
    public static void clearEntity(MinecraftServer server){

        int[] clears = {0};
        server.getAllLevels().forEach(serverLevel -> {
            clears[0] = clearEntity(serverLevel);
            if (ClearConfig.ENTITY_CLEAR.getDatas().clearXp) {
                serverLevel.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), experienceOrb -> true).forEach(entity -> entity.remove(Entity.RemovalReason.DISCARDED));
            }
        });
        MsgHelper.sendServerMsg(server,ClearConfig.ENTITY_CLEAR.getDatas().msg.getOrDefault(0,""),MsgHelper.createMsgMap("$clear.count", String.valueOf(clears[0])));
    }

    public static int clearEntity(ServerLevel serverLevel){
        int clears = 0;
        if (serverLevel != null) {
            Iterator<Entity> e = serverLevel.getAllEntities().iterator();
            while (e.hasNext()){
                Entity entity = e.next();
                if (entity instanceof LivingEntity livingEntity && !(livingEntity instanceof ServerPlayer)) {
                    if (ClearConfig.ENTITY_CLEAR.getDatas().whiteList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString())) continue;
                    if (ClearConfig.ENTITY_CLEAR.getDatas().blackList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString())) {
                        clearEntity(serverLevel, livingEntity);
                        clears++;
                        continue;
                    }
                    if (serverLevel.getNearestPlayer(livingEntity, ClearConfig.ENTITY_CLEAR.getDatas().safeDistance) != null) continue;
                    if (livingEntity.getCustomName() != null && !ClearConfig.ENTITY_CLEAR.getDatas().clearName ) continue;
                    if (livingEntity.getType().getCategory().equals(MobCategory.MONSTER) && !ClearConfig.ENTITY_CLEAR.getDatas().clearMob)  continue;
                    else if (livingEntity instanceof Npc && !ClearConfig.ENTITY_CLEAR.getDatas().clearNpc)  continue;
                    else if (livingEntity instanceof Animal && !ClearConfig.ENTITY_CLEAR.getDatas().clearAnimal) continue;
                    else if (livingEntity instanceof TamableAnimal tamableAnimal &&  tamableAnimal.isTame() && !ClearConfig.ENTITY_CLEAR.getDatas().clearPet) continue;
                    clearEntity(serverLevel, livingEntity);
                    clears++;
                }
            }
        }
        return clears;
    }
    private static void clearEntity(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (ClearConfig.ENTITY_CLEAR.getDatas().safeEntityItem) {
            livingEntity.getAllSlots().forEach(itemStack -> {
                if (!itemStack.isEmpty()) {
                    serverLevel.addFreshEntity(new ItemEntity(serverLevel, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), itemStack));
                }
            });
        }
        livingEntity.remove(Entity.RemovalReason.DISCARDED);
    }
    public static void clearItem(MinecraftServer server){
        if (isClearTrashBin) return;
        isClearTrashBin = true;
        if (TrashBinContainer.nowPlayer != null) {
            TrashBinContainer.nowPlayer.closeContainer();
            //TrashBinContainer.nowPlayer = null;
        }
        if (ClearConfig.ITEM_CLEAR.getDatas().clearTrash) TrashBinContainer.slots.clear();
        int[] clears = {0};
        server.getAllLevels().forEach(serverLevel -> serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), itemEntity -> true).forEach(entity -> {
            if (ClearConfig.ITEM_CLEAR.getDatas().whiteList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) return;
            if (ClearConfig.ITEM_CLEAR.getDatas().blackList.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString())) {
                clears[0]++;
                clearItem(entity);
                return;
            }
            if (serverLevel.getNearestPlayer(entity, ClearConfig.ITEM_CLEAR.getDatas().safeDistance) != null) return;
            if (entity.getCustomName() != null && !ClearConfig.ITEM_CLEAR.getDatas().clearName) return;
            clears[0]++;
            clearItem(entity);
        }));
        quickSort();
        MsgHelper.sendServerMsg(server,ClearConfig.ITEM_CLEAR.getDatas().msg.getOrDefault(0,""),MsgHelper.createMsgMap("$clear.count", String.valueOf(clears[0])));
        isClearTrashBin = false;
    }
    public static void quickSort(){
        HashMap<Integer, ItemStack> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        TrashBinContainer.slots.forEach((slot, stack) -> {
            if (!stack.isEmpty()) {
                if (!list.contains(slot)) {
                    list.add(slot);
                    int[] count = {stack.getCount()};
                    TrashBinContainer.slots.forEach((integer, itemStack) -> {
                        if (ItemStack.isSameItemSameComponents(itemStack, stack) && !list.contains(integer)) {
                            list.add(integer);
                            count[0] += itemStack.getCount();
                        }
                    });
                    while (count[0] > 0) {
                        ItemStack is = stack.copy();
                        if (count[0] >= is.getMaxStackSize()) {
                            count[0] -= is.getMaxStackSize();
                            is.setCount(is.getMaxStackSize());
                        }else {
                            is.setCount(count[0]);
                            count[0] = 0;
                        }
                        map.put(map.size(), is);
                    }
                }
            }
        });
        TrashBinContainer.slots.clear();
        TrashBinContainer.slots.putAll(map);
        //System.out.println(TrashBinContainer.slots);
    }
    public static void clearItem(ItemEntity itemEntity) {
        if (ClearConfig.ITEM_CLEAR.getDatas().trash) TrashBinContainer.slots.put(TrashBinContainer.slots.size(),itemEntity.getItem());
        itemEntity.remove(Entity.RemovalReason.DISCARDED);
    }
}
