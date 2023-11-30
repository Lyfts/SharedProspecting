package com.rune580.sharedprospecting.utils;

import java.io.File;

public final class FsUtils {

    public static boolean recursiveDelete(File file) {
        File[] children = file.listFiles();

        if (children == null) return false;

        for (File child : children) {
            if (!recursiveDelete(child)) return false;
        }

        return file.delete();
    }
}
