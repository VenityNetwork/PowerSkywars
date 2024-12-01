package com.venitymc.PowerSkywars.map;

import cn.nukkit.Server;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import com.venitymc.PowerSkywars.PowerSkywars;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import com.venitymc.PowerSkywars.util.Utils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SkywarsMap {

    private static Config availableMaps = null;

    public static void init() {
        if (availableMaps != null)
            return;

        availableMaps = new Config(PowerSkywars.getInstance().getDataFolder().getAbsolutePath() + File.separator + "maps_config.yml");
    }

    public static SkywarsMap getRandom(SkywarsGame game) {
        if (availableMaps.getAll().isEmpty()) {
            throw new RuntimeException("No available maps to be loaded!");
        }

        List<String> mapNames = new ArrayList<>(availableMaps.getAll().keySet());
        String mapName = mapNames.get((new Random()).nextInt(mapNames.size()));
        Map<String, Object> mapConfig = (Map<String, Object>) availableMaps.get(mapName);

        return new SkywarsMap(game, mapName, mapConfig);
    }

    @Getter
    private final SkywarsGame game;
    @Getter
    private final String name;
    private final Map<String, Object> config;

    public SkywarsMap(SkywarsGame game, String name, Map<String, Object> config) {
        this.game = game;
        this.name = name;
        this.config = config;
    }

    public Level initWorld() {
        String worldName = "game-" + game.getId();
        String from = PowerSkywars.getInstance().getDataFolder() + File.separator + "maps" + File.separator + name;
        String dest = Server.getInstance().getDataPath() + File.separator + "worlds" + File.separator + worldName;
        Utils.copyFolder(from, dest);

        Server.getInstance().loadLevel(worldName);
        Level world = Server.getInstance().getLevelByName(worldName);
        if (world == null) {
            throw new RuntimeException("World can't be loaded.");
        }
        world.getGameRules().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.getGameRules().setGameRule(GameRule.PVP, true);
        world.setTime(Level.TIME_DAY);
        world.stopTime();
        for(var entity : world.getEntities()){
            entity.close();
        }
        return world;
    }

    public List<List<Integer>> getSpawns() {
        return (List<List<Integer>>) config.get("spawns");
    }

    public Vector3 getMid() {
        List<Integer> mid = (List<Integer>) config.get("mid");
        return new Vector3(mid.get(0), mid.get(1), mid.get(2));
    }

}
