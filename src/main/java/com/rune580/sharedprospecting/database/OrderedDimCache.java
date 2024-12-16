package com.rune580.sharedprospecting.database;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

import org.apache.commons.io.FileUtils;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.rune580.sharedprospecting.util.RevisionUtil;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.ServerCache;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class OrderedDimCache {

    private final LongList oreVeins = new LongArrayList();
    private final LongList undergroundFluids = new LongArrayList();
    public final int dimension;

    public OrderedDimCache(int dim) {
        this.dimension = dim;
    }

    public NBTTagCompound save() {
        NBTTagCompound dimCompound = new NBTTagCompound();
        dimCompound.setTag("oreList", saveList(oreVeins));
        dimCompound.setTag("fluidList", saveList(undergroundFluids));
        return dimCompound;
    }

    public void load(NBTTagCompound compound) {
        readOres(compound);
        readFluids(compound);
    }

    public boolean putOreVein(long veinPos) {
        if (oreVeins.contains(veinPos)) {
            return false;
        }

        return oreVeins.add(veinPos);
    }

    public boolean putUndergroundFluid(long fluidPos) {
        if (undergroundFluids.contains(fluidPos)) {
            return false;
        }

        return undergroundFluids.add(fluidPos);
    }

    public long getRevision() {
        return RevisionUtil.packRevision(oreVeins.size(), undergroundFluids.size());
    }

    public List<OreVeinPosition> getOresForRevision(long revision) {
        int oreSize = Math.min(RevisionUtil.getOreSize(revision), oreVeins.size());
        if (getRevision() == revision || oreSize == oreVeins.size()) return Collections.emptyList();

        ObjectList<OreVeinPosition> veins = new ObjectArrayList<>();
        for (long key : oreVeins.subList(oreSize, oreVeins.size())) {
            veins.add(
                ServerCache.instance
                    .getOreVein(dimension, CoordinatePacker.unpackX(key), CoordinatePacker.unpackZ(key)));
        }

        return veins;
    }

    public List<UndergroundFluidPosition> getFluidsForRevision(long revision) {
        int fluidSize = Math.min(RevisionUtil.getFluidSize(revision), undergroundFluids.size());
        if (getRevision() == revision || fluidSize == undergroundFluids.size()) return Collections.emptyList();

        ObjectList<UndergroundFluidPosition> fluids = new ObjectArrayList<>();
        for (long key : undergroundFluids.subList(fluidSize, undergroundFluids.size())) {
            fluids.add(
                ServerCache.instance
                    .getUndergroundFluid(dimension, CoordinatePacker.unpackX(key), CoordinatePacker.unpackZ(key)));
        }

        return fluids;
    }

    private void readOres(NBTTagCompound compound) {
        NBTTagCompound ores = compound.getCompoundTag("ores");
        if (!ores.hasNoTags()) {
            loadFromLegacyTag(oreVeins, ores);
            return;
        }

        NBTTagList oreList = compound.getTagList("oreList", 4);
        for (Object obj : oreList.tagList) {
            undergroundFluids.add(((NBTTagLong) obj).func_150291_c());
        }
    }

    private void readFluids(NBTTagCompound compound) {
        NBTTagCompound fluids = compound.getCompoundTag("fluids");
        if (!fluids.hasNoTags()) {
            loadFromLegacyTag(undergroundFluids, fluids);
            return;
        }

        NBTTagList fluidList = compound.getTagList("fluidList", 4);

        for (Object obj : fluidList.tagList) {
            undergroundFluids.add(((NBTTagLong) obj).func_150291_c());
        }
    }

    private static NBTTagList saveList(LongList list) {
        NBTTagList tagList = new NBTTagList();
        for (long key : list) {
            tagList.appendTag(new NBTTagLong(key));
        }

        return tagList;
    }

    private static void loadFromLegacyTag(LongList list, NBTTagCompound compound) {
        for (String key : compound.func_150296_c()) {
            list.add(Long.parseLong(key));
        }
    }
}
