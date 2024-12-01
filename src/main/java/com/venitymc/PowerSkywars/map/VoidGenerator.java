package com.venitymc.PowerSkywars.map;

import cn.nukkit.level.DimensionData;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.Generator;

import java.util.Map;

public class VoidGenerator extends Generator {

    public VoidGenerator(DimensionData dimensionData, Map<String, Object> options) {
        super(dimensionData, options);
    }

    @Override
    public void stages(GenerateStage.Builder builder) {
    }

    @Override
    public String getName() {
        return "void";
    }
}
