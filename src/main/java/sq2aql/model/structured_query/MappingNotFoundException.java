package sq2aql.model.structured_query;

import sq2aql.model.common.TermCode;

/**
 * @author Alexander Kiel, Lorenz Rosenau
 */
public class MappingNotFoundException extends TranslationException {

    private final TermCode concept;

    public MappingNotFoundException(TermCode concept) {
        super("Mapping for concept with system `%s`, code `%s` and display `%s` not found."
                .formatted(concept.getSystem(), concept.getCode(), concept.getDisplay()));
        this.concept = concept;
    }

    public TermCode getConcept() {
        return concept;
    }
}
