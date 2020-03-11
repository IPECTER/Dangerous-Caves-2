package com.github.evillootlye.caves.ticks;

// TODO: Configurable?
public enum TickLevel {
    WORLD(6300), ENTITY(4);

    public final int ticks;

    TickLevel(int ticks) {
        this.ticks = ticks;
    }
}
