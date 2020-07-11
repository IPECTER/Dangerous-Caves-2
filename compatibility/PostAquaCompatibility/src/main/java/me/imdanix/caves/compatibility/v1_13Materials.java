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

package me.imdanix.caves.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Switch;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class v1_13Materials implements MaterialsProvider {
    @SuppressWarnings("deprecation")
    @Override
    public void rotate(Block block, BlockFace face) {
        BlockData data = block.getBlockData();
        if(data instanceof Switch) {
            ((Switch) data).setFace(Switch.Face.FLOOR);
        } else if (data instanceof Directional) {
            ((Directional) data).setFacing(face);
        } else if(data instanceof MultipleFacing) {
            ((MultipleFacing) data).setFace(face, true);
        }
        block.setBlockData(data, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getHeadFromValue(String value) {
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:\"" + UUID.nameUUIDFromBytes(value.getBytes()) + "\"," +
                        "Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }
}