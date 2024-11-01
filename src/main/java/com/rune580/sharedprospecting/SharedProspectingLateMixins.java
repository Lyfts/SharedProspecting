package com.rune580.sharedprospecting;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.rune580.sharedprospecting.mixins.Mixins;

@LateMixin
public class SharedProspectingLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.sharedprospecting.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Mixins.getLateMixins(loadedMods);
    }
}
