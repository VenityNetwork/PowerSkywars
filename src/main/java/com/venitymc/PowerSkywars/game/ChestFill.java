package com.venitymc.PowerSkywars.game;

import cn.nukkit.block.Block;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChestFill {

    private static final List<Item> SWORDS = List.of(
            Item.get(Item.DIAMOND_SWORD),
            Item.get(Item.IRON_SWORD),
            Item.get(Item.STONE_SWORD)
    );

    private static final List<Item> TOOLS = List.of(
            Item.get(Item.DIAMOND_AXE),
            Item.get(Item.IRON_AXE),
            Item.get(Item.STONE_AXE),
            Item.get(Item.DIAMOND_PICKAXE),
            Item.get(Item.IRON_PICKAXE),
            Item.get(Item.STONE_PICKAXE)
    );

    private static final List<Item> ARMORS = List.of(
            Item.get(Item.DIAMOND_HELMET),
            Item.get(Item.DIAMOND_CHESTPLATE),
            Item.get(Item.DIAMOND_LEGGINGS),
            Item.get(Item.DIAMOND_BOOTS),
            Item.get(Item.IRON_HELMET),
            Item.get(Item.IRON_CHESTPLATE),
            Item.get(Item.IRON_LEGGINGS),
            Item.get(Item.IRON_BOOTS),
            Item.get(Item.CHAINMAIL_HELMET),
            Item.get(Item.CHAINMAIL_CHESTPLATE),
            Item.get(Item.CHAINMAIL_LEGGINGS),
            Item.get(Item.CHAINMAIL_BOOTS)
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

    public static void randomFill(ChestInventory inventory){
        inventory.clearAll();
        var items = new ObjectArrayList<Item>();
        items.add(SWORDS.get(ThreadLocalRandom.current().nextInt(SWORDS.size())));

        int armorCount = Math.random() <= 0.4 ? 2 : 1;
        for(int i = 0; i < armorCount; i++) {
            items.add(ARMORS.get(ThreadLocalRandom.current().nextInt(SWORDS.size())));
        }

        // TODO: belum beres
    }
}
