package sq2aql.model.structured_query;

import sq2aql.Container;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.common.TermCode;
import java.util.List;

/**
 * A {@code ValueSetCriterion} will select all patients that have at least one resource represented by that concept and
 * coded value.
 * <p>
 * Examples are {@code Observation} resources representing the concept of a coded laboratory value.
 */
public final class ValueSetCriterion extends AbstractCriterion {

    private final List<TermCode> selectedConcepts;

    private ValueSetCriterion(TermCode concept, List<TermCode> selectedConcepts) {
        super(concept, List.of());
        this.selectedConcepts = selectedConcepts;
    }

    /**
     * Returns a {@code ValueSetCriterion}.
     *
     * @param concept          the concept the criterion represents
     * @param selectedConcepts at least one selected value concept
     * @return the {@code ValueSetCriterion}
     */
    public static ValueSetCriterion of(TermCode concept, TermCode... selectedConcepts) {
        if (selectedConcepts == null || selectedConcepts.length == 0) {
            throw new IllegalArgumentException("empty selected concepts");
        }
        return new ValueSetCriterion(concept, List.of(selectedConcepts));
    }

    public List<TermCode> getSelectedConcepts() {
        return selectedConcepts;
    }

    public Container<BooleanWhereExpr> toAql(MappingContext mappingContext) {
        return Container.of(BooleanWhereExpr.FALSE);
    }



//    public Container<BooleanContainsExpr> toAql(MappingContext mappingContext) {
//        //TODO: expand the termCode, look into ConceptCriterion#fullExpr
//        return retrieveExpr(mappingContext, termCode).flatMap(retrieveExpr -> {
//            var alias = AliasExpression.of(retrieveExpr.getResourceType().substring(0, 1));
//            var sourceClause = SourceClause.of(retrieveExpr, alias);
//            var mapping = mappingContext.getMapping(termCode).orElseThrow(() -> new MappingNotFoundException(termCode));
//            var valueFhirPath = mapping.getValueFhirPath().orElseThrow(() -> new ValueFhirPathNotFoundException(termCode));
//            var codingExpr = InvocationExpression.of(InvocationExpression.of(alias, valueFhirPath), "coding");
//            return whereExpr(mappingContext, codingExpr).map(whereExpr ->
//                    ExistsExpression.of(QueryExpression.of(sourceClause, WhereClause.of(whereExpr))));
//        });
//    }
//
//    private Container<BooleanContainsExpr> whereExpr(MappingContext mappingContext, Expression codingExpr) {
//        return selectedConcepts.stream()
//                .map(concept -> codeSelector(mappingContext, concept).map(terminology ->
//                        (BooleanContainsExpr) MembershipExpression.contains(codingExpr, terminology)))
//                .reduce(Container.empty(), Container.OR);
//    }
}
