package com.github.evillootlye.caves.generator;

import com.github.evillootlye.caves.util.random.Rnd;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class StructureGroup {
    private static final List<Material> chestItems = new ArrayList<>();
    private final String id;

    public StructureGroup(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    public abstract void generate(Random random, Chunk currentChunk, Block startBlock);

    public abstract int getWeight();

    public static Material randomStone(Random random) {
        return random.nextBoolean() ? Material.STONE : Material.COBBLESTONE;
    }

    public static void setType(Location loc, Material material) {
        loc.getBlock().setType(material, false);
    }

    public static void fillInventory(Block block) {
        if(chestItems.isEmpty()) return;
        if(!(block.getState() instanceof Container)) return;
        Inventory inventory = ((Container)block.getState()).getInventory();
        int itemsCount = Rnd.nextInt(10) + 2;
        while(itemsCount-- > 0) {
            Material material = chestItems.get(Rnd.nextInt(chestItems.size()));
            inventory.setItem(
                    Rnd.nextInt(inventory.getSize()),
                    new ItemStack(material, material.getMaxStackSize() > 1 ? Rnd.nextInt(3) + 1 : 1)
            );
        }
    }

    public static void setItems(Collection<Material> chestItems) {
        StructureGroup.chestItems.clear();
        StructureGroup.chestItems.addAll(chestItems);
    }
}
