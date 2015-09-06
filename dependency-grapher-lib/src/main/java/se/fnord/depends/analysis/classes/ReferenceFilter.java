package se.fnord.depends.analysis.classes;

import net.openhft.koloboke.collect.set.hash.HashObjSet;
import net.openhft.koloboke.collect.set.hash.HashObjSets;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReferenceFilter implements Predicate<String> {
    private final Set<String> prefixes;
    private final Pattern pattern;

    public static ReferenceFilter create(String... prefixes) {
        return new ReferenceFilter(HashObjSets.newImmutableSet(prefixes));
    }

    private ReferenceFilter(Set<String> prefixes) {
        this.prefixes = prefixes;
        final String regex = prefixes.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|", "^", ""));
        pattern = Pattern.compile(regex);
    }

    public ReferenceFilter add(String prefix) {
        final HashObjSet<String> prefixes = HashObjSets.newImmutableSet(this.prefixes, HashObjSets.newImmutableSetOf(prefix));
        return new ReferenceFilter(prefixes);
    }

    @Override
    public boolean test(String s) {
        return !pattern.matcher(s).find();
    }
}
