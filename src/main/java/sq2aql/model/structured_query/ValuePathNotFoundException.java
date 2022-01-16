package sq2aql.model.structured_query;

import sq2aql.model.common.TermCode;

public class ValuePathNotFoundException extends TranslationException {

    private final TermCode concept;

    public ValuePathNotFoundException(TermCode concept) {
        super("ValuePath for concept with system `%s`, code `%s` and display `%s` not found."
                .formatted(concept.getSystem(), concept.getCode(), concept.getDisplay()));
        this.concept = concept;
    }

    public TermCode getConcept() {
        return concept;
    }
}
