package sq2aql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import sq2aql.model.common.TermCode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Lorenz Rosenau
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConceptNode(TermCode concept,
                          List<ConceptNode> children) {

    public static ConceptNode of() {
        return new ConceptNode(null, List.of());
    }

    @JsonCreator
    public static ConceptNode of(@JsonProperty("termCode") TermCode concept,
        @JsonProperty("children") ConceptNode... children) {
        return new ConceptNode(Objects.requireNonNull(concept),
            children == null ? List.of() : List.of(children));
    }

    public TermCode getConcept() {
        return concept;
    }

    public List<ConceptNode> getChildren() {
        return children;
    }

    public Stream<TermCode> expand(TermCode concept) {
        if (Objects.requireNonNull(concept).equals(this.concept)) {
            return leafConcepts();
        } else if (children.isEmpty()) {
            return Stream.of();
        } else {
            return children.stream().flatMap(n -> n.expand(concept));
        }
    }

    private Stream<TermCode> leafConcepts() {
        if (children.isEmpty()) {
            return Stream.of(concept);
        } else {
            return Stream.concat(Stream.of(concept), children.stream().flatMap(ConceptNode::leafConcepts));
        }
    }
}
