package com.venitymc.PowerSkywars.game;

import cn.nukkit.block.Block;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChestFill {

    private static final List<String> SWORDS = List.of(
            Item.DIAMOND_SWORD,
            Item.IRON_SWORD,
            Item.STONE_SWORD
    );

    private static final List<String> TOOLS = List.of(
            Item.DIAMOND_AXE,
            Item.IRON_AXE,
            Item.STONE_AXE,
            Item.DIAMOND_PICKAXE,
            Item.IRON_PICKAXE,
            Item.STONE_PICKAXE
    );

    private static final List<String> ARMORS = List.of(
            Item.DIAMOND_HELMET,
            Item.DIAMOND_CHESTPLATE,
            Item.DIAMOND_LEGGINGS,
            Item.DIAMOND_BOOTS,
            Item.IRON_HELMET,
            Item.IRON_CHESTPLATE,
            Item.IRON_LEGGINGS,
            Item.IRON_BOOTS,
            Item.CHAINMAIL_HELMET,
            Item.CHAINMAIL_CHESTPLATE,
            Item.CHAINMAIL_LEGGINGS,
            Item.CHAINMAIL_BOOTS
    );

    private static final List<String> BLOCK_NAMES = List.of(
            Block.DIRT,
            Block.COBBLESTONE,
            Block.STONE,
            Block.OAK_PLANKS
    );

    private static final List<String> FOOD_NAMES = List.of(
            Item.BREAD,
            Item.CARROT,
            Item.GOLDEN_APPLE,
            Item.APPLE,
            Item.COOKED_BEEF,
            Item.COOKED_CHICKEN,
            Item.COOKED_SALMON
    );

    private static final List<Integer> POTION_TYPES = List.of(
            PotionType.SWIFTNESS.id(),
            PotionType.LEAPING.id(),
            PotionType.HEALING.id()
    );

    private static <T> T listRandom(List<T> list){
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static void randomFill(ChestInventory inventory){
        inventory.clearAll();
        var items = new ObjectArrayList<Item>();
        items.add(Item.get(listRandom(SWORDS)));

        int armorCount = Math.random() <= 0.4 ? 2 : 1;
        for(int i = 0; i < armorCount; i++) {
            items.add(Item.get(listRandom(ARMORS)));
        }

        if(Math.random() <= 0.5) {
            int toolCount = Math.random() <= 0.3 ? 2 : 1;
            for (int i = 0; i < toolCount; i++) {
                items.add(Item.get(listRandom(TOOLS)));
            }
        }

        int blockCount = Math.random() <= 0.5 ? 2 : 1;
        for(int i = 0; i < blockCount; i++) {
            items.add(Item.get(listRandom(BLOCK_NAMES), 0, ThreadLocalRandom.current().nextInt(20, 64)));
        }

        int foodCount = Math.random() <= 0.5 ? 2 : 1;
        for(int i = 0; i < foodCount; i++) {
            items.add(Item.get(listRandom(FOOD_NAMES), 0, ThreadLocalRandom.current().nextInt(1, 5)));
        }

        if(Math.random() <= 0.12){
            items.add(Item.get(Item.BOW, 0, 1));
            items.add(Item.get(Item.ARROW, 0, ThreadLocalRandom.current().nextInt(5, 16)));
        }

        if(Math.random() <= 0.1){
            items.add(Item.get(Item.POTION, listRandom(POTION_TYPES)));
        }

        if(Math.random() <= 0.08){
            items.add(Item.get(Item.ENDER_PEARL, 0, ThreadLocalRandom.current().nextInt(1, 3)));
        }

        var indexes = new ObjectArrayList<Integer>();
        for (int i = 0; i < inventory.getSize(); i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);

        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(indexes.get(i), items.get(i));
        }
    }
}
