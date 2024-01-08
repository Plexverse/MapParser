package net.plexverse.mapparser.mapsettings.objects;

import lombok.Data;
import net.plexverse.mapparser.enums.GameType;

import java.util.*;

@Data
public class MapMeta {

    private String author;
    private GameType gameType;
    private String mapName;
    private boolean legacy;

    public MapMeta() {
        author = null;
        gameType = GameType.SKYWARS;
        mapName = "Map Name";
        legacy = false;
    }

    public List<UUID> parseAuthorUuids() {
        if(author == null || author.equals("null")) {
            return new ArrayList<>();
        }
        return Arrays.stream(author.split(",")).map(UUID::fromString).toList();
    }

}
