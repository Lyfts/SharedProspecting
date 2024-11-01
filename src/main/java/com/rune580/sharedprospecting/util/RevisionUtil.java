package com.rune580.sharedprospecting.util;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

public class RevisionUtil {

    public static long packRevision(int oreSize, int fluidSize) {
        return CoordinatePacker.pack(oreSize, 0, fluidSize);
    }

    public static int getOreSize(long revision) {
        return CoordinatePacker.unpackX(revision);
    }

    public static int getFluidSize(long revision) {
        return CoordinatePacker.unpackZ(revision);
    }
}
