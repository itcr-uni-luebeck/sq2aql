package sq2aql.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonProperty;
import sq2aql.model.common.TermCode;
import sq2aql.model.structured_query.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * @author Lorenz Rosenau
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Mapping {

    private final TermCode concept;
    private final String openEhrType;
    private final List<ValuePathElement> valuePathElements;
    private final String valuaPath;
    private final List<Modifier> fixedCriteria;
    private final String termCodePath;
    private final List<ValuePathElement> termCodePathElements;

    private Mapping(TermCode concept, String openEhrType, List<ValuePathElement> valuePathElements,
        String path, List<ValuePathElement> termCodePathElements, String termCodePath, List<Modifier> fixedCriteria) {
        this.concept = Objects.requireNonNull(concept);
        this.openEhrType = Objects.requireNonNull(openEhrType);
        this.valuePathElements = valuePathElements;
        this.valuaPath = path;
        this.fixedCriteria = Objects.requireNonNull(fixedCriteria);
        this.termCodePath = termCodePath;
        this.termCodePathElements = termCodePathElements;
    }

    public static Mapping of(TermCode concept, String resourceType, String valuaPath, String termCodePath) {
        return new Mapping(Objects.requireNonNull(concept), Objects.requireNonNull(resourceType), List.of(),
            valuaPath, List.of(), termCodePath, List.of());
    }

    @JsonCreator
    public static Mapping of(@JsonProperty("key") TermCode concept,
                             @JsonProperty("openEhrType") String openEhrType,
                             @JsonProperty("valuePathElements") List<ValuePathElement> valuePathElements,
                             @JsonProperty("valuePath") String path,
                             @JsonProperty("termCodePathElements") List<ValuePathElement> termCodePathElements,
                             @JsonProperty("TermCodePath") String termCodePath,
                             @JsonProperty("fixedCriteria") Modifier... fixedCriteria) {
        return new Mapping(Objects.requireNonNull(concept), Objects.requireNonNull(openEhrType), valuePathElements,
            path, termCodePathElements, termCodePath, fixedCriteria == null ? List.of() : List.of(fixedCriteria));
    }

    public TermCode getConcept() {
        return concept;
    }

    public String getOpenEhrType() {
        return openEhrType;
    }

    public List<Modifier> getFixedCriteria() {
        return fixedCriteria;
    }

    public TermCode getTermCode() {
        return concept;
    }

    public List<ValuePathElement> getValuePathElements() {
        return valuePathElements;
    }

    public String getValuePath() {
        return valuaPath;
    }

    public String getTermCodePath() {
        return termCodePath;
    }

    public List<ValuePathElement> getTermCodePathElements() {
        return termCodePathElements;
    }
}
