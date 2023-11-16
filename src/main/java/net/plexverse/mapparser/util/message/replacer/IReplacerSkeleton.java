package net.plexverse.mapparser.util.message.replacer;

import net.plexverse.mapparser.util.message.Replacer;

public interface IReplacerSkeleton<T> {
    boolean accepts(Class<?> aClass);

    T accept(T object, Replacer replacer);
}
