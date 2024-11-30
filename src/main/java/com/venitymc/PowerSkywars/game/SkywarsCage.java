package com.venitymc.PowerSkywars.game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.venitymc.PowerSkywars.game.player.SkywarsPlayer;

import java.util.ArrayList;
import java.util.List;

public class SkywarsCage {

    private final Position pos;
    private final Vector3[] blockCoordinates;
    private boolean isCageSet = false;

    public SkywarsCage(Position pos) {
        this.pos = pos;
        this.blockCoordinates = getBlockCoordinates();
    }

    public Vector3[] getBlockCoordinates() {
        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        // Cage dimensions
        int radiusX = 1; // Cage extends 1 block to the left and right
        int radiusZ = 1; // Cage extends 1 block forward and backward
        int height = 5;  // Cage is 5 blocks tall

        List<Vector3> blocks = new ArrayList<>();

        // Generate blocks for the walls, floor, and ceiling
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    // Floor and ceiling (at y == 0 or y == height - 1)
                    if (dy == 0 || dy == height - 1) {
                        blocks.add(new Vector3(x + dx, y + dy, z + dz));
                    }
                    // Walls (outer blocks only, excluding inner blocks)
                    else if (dx == -radiusX || dx == radiusX || dz == -radiusZ || dz == radiusZ) {
                        blocks.add(new Vector3(x + dx, y + dy, z + dz));
                    }
                }
            }
        }

        // Convert the list to an array and return it
        return blocks.toArray(new Vector3[0]);
    }

    public void set() {
        if (isCageSet) {
            return;
        }

        Level world = pos.getLevel();

        Vector3[] cageBlocks = getBlockCoordinates();
        for (Vector3 block : cageBlocks) {
            world.setBlock(block, Block.get(Block.GLASS));
        }

        isCageSet = true;
    }

    public void unset() {
        if (!isCageSet) {
            return;
        }

        Level world = pos.getLevel();

        Vector3[] cageBlocks = getBlockCoordinates();
        for (Vector3 block : cageBlocks) {
            world.setBlock(block, Block.get(Block.AIR));
        }

        isCageSet = false;
    }


}
