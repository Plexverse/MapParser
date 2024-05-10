package net.plexverse.mapparser.saving;

import java.io.File;

public interface SavingStrategy {
    void save(File worldFile, String folder);
}
