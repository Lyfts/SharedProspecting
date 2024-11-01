package com.rune580.sharedprospecting.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.rune580.sharedprospecting.util.RevisionUtil;
import com.sinthoras.visualprospecting.Utils;
import com.sinthoras.visualprospecting.VP;
import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;
import com.sinthoras.visualprospecting.database.veintypes.VeinTypeCaching;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

public class OrderedDimCache {

    private final Long2ObjectSortedMap<OreVeinPosition> oreVeins = new Long2ObjectLinkedOpenHashMap<>();
    private final Long2ObjectSortedMap<UndergroundFluidPosition> undergroundFluids = new Long2ObjectLinkedOpenHashMap<>();
    public final int dimension;

    public OrderedDimCache(int dim) {
        this.dimension = dim;
    }

    public NBTTagCompound save() {
        NBTTagCompound dimCompound = new NBTTagCompound();
        dimCompound.setTag("ores", saveOres());
        dimCompound.setTag("fluids", saveFluids());
        return dimCompound;
    }

    public void load(NBTTagCompound compound) {
        readOres(compound);
        readFluids(compound);
    }

    public boolean putOreVein(OreVeinPosition vein) {
        return oreVeins.put(getOreVeinKey(vein.chunkX, vein.chunkZ), vein) == null;
    }

    public ObjectCollection<OreVeinPosition> getOreVeins() {
        return oreVeins.values();
    }

    public boolean putUndergroundFluid(UndergroundFluidPosition fluid) {
        return undergroundFluids.put(getUndergroundFluidKey(fluid.chunkX, fluid.chunkZ), fluid) == null;
    }

    public ObjectCollection<UndergroundFluidPosition> getUndergroundFluids() {
        return undergroundFluids.values();
    }

    public long getRevision() {
        return RevisionUtil.packRevision(oreVeins.size(), undergroundFluids.size());
    }

    public List<OreVeinPosition> getOresForRevision(long revision) {
        int oreSize = RevisionUtil.getOreSize(revision);
        if (getRevision() == revision || oreSize == oreVeins.size()) return Collections.emptyList();
        if (oreSize > oreVeins.size()) return new ArrayList<>(getOreVeins());

        return new ArrayList<>(getOreVeins()).subList(oreSize, oreVeins.size());
    }

    public List<UndergroundFluidPosition> getFluidsForRevision(long revision) {
        int fluidSize = RevisionUtil.getFluidSize(revision);
        if (getRevision() == revision || fluidSize == revision) return Collections.emptyList();
        if (fluidSize > undergroundFluids.size()) return new ArrayList<>(getUndergroundFluids());

        return new ArrayList<>(getUndergroundFluids()).subList(fluidSize, undergroundFluids.size());
    }

    private NBTTagCompound saveFluids() {
        NBTTagCompound compound = new NBTTagCompound();
        for (UndergroundFluidPosition fluid : undergroundFluids.values()) {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            fluidCompound.setInteger("chunkX", fluid.chunkX);
            fluidCompound.setInteger("chunkZ", fluid.chunkZ);
            fluidCompound.setString("fluidName", fluid.fluid.getName());
            NBTTagList chunkList = new NBTTagList();
            for (int i = 0; i < VP.undergroundFluidSizeChunkX; i++) {
                chunkList.appendTag(new NBTTagIntArray(fluid.chunks[i]));
            }
            fluidCompound.setTag("chunks", chunkList);
            compound.setTag(String.valueOf(getUndergroundFluidKey(fluid.chunkX, fluid.chunkZ)), fluidCompound);
        }
        return compound;
    }

    private NBTTagCompound saveOres() {
        NBTTagCompound compound = new NBTTagCompound();
        for (OreVeinPosition vein : oreVeins.values()) {
            NBTTagCompound veinCompound = new NBTTagCompound();
            veinCompound.setInteger("chunkX", vein.chunkX);
            veinCompound.setInteger("chunkZ", vein.chunkZ);
            veinCompound.setShort("veinTypeId", vein.veinType.veinId);
            veinCompound.setBoolean("depleted", vein.isDepleted());
            compound.setTag(String.valueOf(getOreVeinKey(vein.chunkX, vein.chunkZ)), veinCompound);
        }
        return compound;
    }

    private void readOres(NBTTagCompound compound) {
        NBTTagCompound ores = compound.getCompoundTag("ores");
        if (ores.hasNoTags()) return;

        for (String key : ores.func_150296_c()) {
            NBTTagCompound veinCompound = ores.getCompoundTag(key);
            int chunkX = veinCompound.getInteger("chunkX");
            int chunkZ = veinCompound.getInteger("chunkZ");
            short veinTypeId = veinCompound.getShort("veinTypeId");
            boolean depleted = veinCompound.getBoolean("depleted");
            VeinType veinType = VeinTypeCaching.getVeinType(veinTypeId);
            oreVeins
                .put(getOreVeinKey(chunkX, chunkZ), new OreVeinPosition(dimension, chunkX, chunkZ, veinType, depleted));
        }
    }

    private void readFluids(NBTTagCompound compound) {
        NBTTagCompound fluids = compound.getCompoundTag("fluids");
        if (fluids.hasNoTags()) return;

        for (String key : fluids.func_150296_c()) {
            NBTTagCompound fluidCompound = fluids.getCompoundTag(key);
            int chunkX = fluidCompound.getInteger("chunkX");
            int chunkZ = fluidCompound.getInteger("chunkZ");
            String fluidName = fluidCompound.getString("fluidName");
            Fluid fluid = FluidRegistry.getFluid(fluidName);
            int[][] chunks = new int[VP.undergroundFluidSizeChunkX][VP.undergroundFluidSizeChunkZ];
            NBTTagList chunkList = fluidCompound.getTagList("chunks", 11);
            for (int i = 0; i < VP.undergroundFluidSizeChunkX; i++) {
                chunks[i] = chunkList.func_150306_c(i);
            }
            undergroundFluids.put(
                getUndergroundFluidKey(chunkX, chunkZ),
                new UndergroundFluidPosition(dimension, chunkX, chunkZ, fluid, chunks));
        }
    }

    public static long getOreVeinKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(Utils.mapToCenterOreChunkCoord(chunkX), Utils.mapToCenterOreChunkCoord(chunkZ));
    }

    public static long getUndergroundFluidKey(int chunkX, int chunkZ) {
        return Utils.chunkCoordsToKey(
            Utils.mapToCornerUndergroundFluidChunkCoord(chunkX),
            Utils.mapToCornerUndergroundFluidChunkCoord(chunkZ));
    }
}
