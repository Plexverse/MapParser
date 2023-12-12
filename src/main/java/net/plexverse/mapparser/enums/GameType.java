package net.plexverse.mapparser.enums;

public enum GameType {
    MICRO_BATTLES("Micro Battles"),
    SKYWARS("Skywars");

    private final String displayName;

    private static final GameType[] VALUES = values();

    GameType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static GameType getNextGameType(GameType gameType) {
        return gameType == null ? GameType.MICRO_BATTLES : (gameType.ordinal() + 1 >= VALUES.length ? null : VALUES[gameType.ordinal() + 1]);
    }
}
