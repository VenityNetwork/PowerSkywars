package com.venitymc.PowerSkywars.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    @SneakyThrows
    public static void copyFolder(String source, String dest) {
        FileUtils.copyDirectory(new File(source), new File(dest));
    }

    public static void resetPlayer(Player player){
        player.getInventory().clearAll();
        player.getInventory().getArmorInventory().clearAll();
        player.getEnderChestInventory().clearAll();
        player.removeAllEffects();
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setExperience(0, 0);
        player.getFoodData().setFood(player.getFoodData().getMaxFood());
    }

    public static void clearGameWorlds() {
        String worldsFolderPath = Server.getInstance().getDataPath() + File.separator + "worlds";
        File worldsFolder = new File(worldsFolderPath);

        if (!worldsFolder.exists() || !worldsFolder.isDirectory()) {
            System.out.println("Worlds folder not found!");
            return;
        }

        File[] worldFolders = worldsFolder.listFiles(File::isDirectory);

        if (worldFolders != null) {
            for (File worldFolder : worldFolders) {
                Level world = Server.getInstance().getLevelByName(worldFolder.getName());

                if (worldFolder.getName().equals(Server.getInstance().getDefaultLevel().getFolderName())) {
                    continue;
                }

                if (world != null) {
                    forceUnloadWorld(world);
                }

                try {
                    System.out.println("Deleting world: " + worldFolder.getName());
                    FileUtils.deleteDirectory(worldFolder);
                } catch (IOException e) {
                    System.err.println("Failed to delete world folder: " + worldFolder.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No world directories found.");
        }
    }

    private static void forceUnloadWorld(Level world) {
        Server.getInstance().unloadLevel(world, true);
        Server.getInstance().getLevels().remove(world.getId());
        Path path = Paths.get(world.getFolderPath(), "config.json");
        try {
            Files.newOutputStream(path).close();
            Files.newInputStream(path).close();
        } catch (IOException ignored) {
        }
    }

    public static String formatSecondsToMinutesAndSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

}
