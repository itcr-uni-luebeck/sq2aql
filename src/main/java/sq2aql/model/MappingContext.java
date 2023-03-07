package sq2aql.model;

import sq2aql.model.common.TermCode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Alexander Kiel, Lorenz Rosenau
 */
public final class MappingContext {

    private final Map<TermCode, Mapping> mappings;
    private final ConceptNode conceptTree;

    private MappingContext(Map<TermCode, Mapping> mappings, ConceptNode conceptTree) {
        this.mappings = Objects.requireNonNull(mappings);
        this.conceptTree = Objects.requireNonNull(conceptTree);
    }

    public static MappingContext of() {
        return new MappingContext(Map.of(), ConceptNode.of());
    }

    public static MappingContext of(Map<TermCode, Mapping> mappings, ConceptNode conceptTree) {
        return new MappingContext(Map.copyOf(mappings), conceptTree);
    }

    public Optional<Mapping> getMapping(TermCode concept) {
        return Optional.ofNullable(mappings.get(Objects.requireNonNull(concept)));
    }

    public Stream<TermCode> expandConcept(TermCode concept) {
        return conceptTree.expand(concept);
    }
}
