package net.plexverse.mapparser.enums;

public enum Team {
    BLUE("<blue>Blue"),
    RED("<red>Red"),
    GREEN("<green>Green"),
    YELLOW("<yellow>Yellow");

    private final String displayName;
    public static final Team[] VALUES = values();

    Team(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
