/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.CustomMob;
import me.imdanix.caves.util.Materials;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CaveGolem extends CustomMob implements Listener {
    private static final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0);
    private static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 30, 0);
    private static final PotionEffect CONFUSION = new PotionEffect(PotionEffectType.CONFUSION, 20, 0);
    private static final PotionEffect SLOW_PL = new PotionEffect(PotionEffectType.SLOW, 40, 1);

    private static final ItemStack CHESTPLATE;
    private static final ItemStack LEGGINGS;
    private static final ItemStack BOOTS;
    static {
        CHESTPLATE = Materials.getColored(EquipmentSlot.CHEST, 105, 105, 105);
        LEGGINGS = Materials.getColored(EquipmentSlot.LEGS, 105, 105, 105);
        BOOTS = Materials.getColored(EquipmentSlot.FEET, 105, 105, 105);
    }

    private int weight;
    private String name;
    private double health;

    private final List<ItemStack> heads;
    private boolean slow;
    private boolean distract;
    private double nonPickaxe;
    private double damageModifier;

    public CaveGolem() {
        super(EntityType.SKELETON, "cave-golem");
        this.heads = new ArrayList<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Dead Miner"));
        health = cfg.getDouble("health", 20);

        slow = cfg.getBoolean("slowness", true);
        distract = cfg.getBoolean("distract-attack", true);
        nonPickaxe = cfg.getDouble("nonpickaxe-modifier", 0.07);
        damageModifier = cfg.getDouble("damage-modifier", 1.5);

        heads.clear();
        for(String typeStr : cfg.getStringList("variants")) {
            Material type = Material.getMaterial(typeStr.toUpperCase());
            if(type == null || !type.isBlock()) continue;
            heads.add(new ItemStack(type));
        }
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        Utils.setMaxHealth(entity, health);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(null);
        equipment.setHelmet(Rnd.randomItem(heads)); equipment.setHelmetDropChance(1);
        equipment.setChestplate(CHESTPLATE);        equipment.setChestplateDropChance(0);
        equipment.setLeggings(LEGGINGS);            equipment.setLeggingsDropChance(0);
        equipment.setBoots(BOOTS);                  equipment.setBootsDropChance(0);
        entity.setSilent(true);
        if(slow) entity.addPotionEffect(SLOW);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(isThis(event.getDamager())) {
            if(!(event.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) event.getEntity();
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1, 0.5f);
            if(distract) {
                entity.addPotionEffect(BLINDNESS);
                entity.addPotionEffect(SLOW_PL);
                entity.addPotionEffect(CONFUSION);
            }
            event.setDamage(event.getDamage() * damageModifier);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(isThis(event.getEntity())) {
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.HOSTILE, 1, 1.3f);
            if(event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent enEvent = (EntityDamageByEntityEvent) event;
                if(!(enEvent.getDamager() instanceof LivingEntity)) {
                    LivingEntity entity = (LivingEntity) enEvent.getDamager();
                    EntityEquipment equipment = entity.getEquipment();
                    if(equipment != null && equipment.getItemInMainHand().getType().name().contains("PICKAXE"))
                        return;
                }
            }
            event.setDamage(event.getDamage() * nonPickaxe);
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }
}