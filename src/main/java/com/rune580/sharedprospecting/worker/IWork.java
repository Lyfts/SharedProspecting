package com.rune580.sharedprospecting.worker;

public interface IWork {
    /**
     * Runs on tick
     * @return true to mark work as finished.
     */
    boolean run();
}
