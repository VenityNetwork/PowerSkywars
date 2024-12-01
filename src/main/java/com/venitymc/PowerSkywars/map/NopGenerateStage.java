package com.venitymc.PowerSkywars.map;

import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;

public class NopGenerateStage extends GenerateStage {

    public static final String NAME = "nop";

    @Override
    public void apply(ChunkGenerateContext context) {
    }

    @Override
    public String name() {
        return NAME;
    }
}
