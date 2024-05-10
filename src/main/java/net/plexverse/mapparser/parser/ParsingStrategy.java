package net.plexverse.mapparser.parser;

public interface ParsingStrategy {
    void parse(Runnable onComplete);
}
