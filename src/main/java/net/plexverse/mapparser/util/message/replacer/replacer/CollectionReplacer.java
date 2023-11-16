package net.plexverse.mapparser.util.message.replacer.replacer;

import net.plexverse.mapparser.util.message.Replacer;
import net.plexverse.mapparser.util.message.replacer.IReplacerSkeleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public class CollectionReplacer implements IReplacerSkeleton<Collection> {
    @Override
    public boolean accepts(Class<?> aClass) {
        return Collection.class.isAssignableFrom(aClass);
    }

    @Override
    public Collection accept(Collection object, Replacer replacer) {
        final Collection copy = new ArrayList<>();
        final Map<Class<?>, IReplacerSkeleton> classToReplacer = new IdentityHashMap<>();

        for (final Object content : object) {
            final IReplacerSkeleton skeleton = classToReplacer.computeIfAbsent(content.getClass(), ($) -> {
                final IReplacerSkeleton replacerSkeleton = Replacer.findReplacer(content.getClass());
                if (replacerSkeleton == null) {
                    throw new IllegalStateException(String.format("No acceptable replacer found for %s", content.getClass()));
                }

                return replacerSkeleton;
            });

            final Object accept = skeleton.accept(content, replacer);
            copy.add(accept);
        }

        return copy;
    }
}