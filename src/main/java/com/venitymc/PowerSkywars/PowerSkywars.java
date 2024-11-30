package com.venitymc.PowerSkywars;

import cn.nukkit.plugin.PluginBase;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import com.venitymc.PowerSkywars.listener.EventListener;
import com.venitymc.PowerSkywars.map.SkywarsMap;
import com.venitymc.PowerSkywars.util.Utils;
import lombok.Getter;
import java.io.File;

public class PowerSkywars extends PluginBase {

    @Getter
    private static PowerSkywars instance = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createMapsFolder();
        saveResource("maps_config.yml");

        SkywarsMap.init();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    private void createMapsFolder() {
        File mapsFolder = new File(getDataFolder(), "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
    }

    @Override
    public void onDisable() {
        Utils.clearGameWorlds();
    }

}
