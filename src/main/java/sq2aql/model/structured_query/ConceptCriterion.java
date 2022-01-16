package sq2aql.model.structured_query;

import sq2aql.Container;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.AndWhereExpr;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.MatchesExpr;
import sq2aql.model.aql.StringPrimitive;
import sq2aql.model.aql.ValueListMatchesOperand;
import sq2aql.model.common.TermCode;
import java.util.List;

/**
 * A {@code ConceptCriterion} will select all patients that have at least one resource represented by that concept.
 * <p>
 * Examples are {@code Condition} resources representing the concept of a particular disease.
 */
public final class ConceptCriterion extends AbstractCriterion {

    private ConceptCriterion(TermCode concept, List<Modifier> modifiers) {
        super(concept, modifiers);
    }

    /**
     * Returns a {@code ConceptCriterion}.
     *
     * @param concept   the concept the criterion represents
     * @param modifiers mofifiers to use in addition to {@code concept}
     * @return the {@code ConceptCriterion}.
     */
    public static ConceptCriterion of(TermCode concept, Modifier... modifiers) {
        return new ConceptCriterion(concept, List.of(modifiers));
    }

//    /**
//     * Translates this criterion into a AQL expression.
//     * <p>
//     * Expands {@link #getTermCode() concept} returning a disjunction of exists expressions on multiple expansions.
//     *
//     * @param mappingContext contains the mappings needed to create the CQL expression
//     * @return a {@link Container} of the {@link BooleanContainsExpr} together with its used {@link CodeSystemDefinition
//     * CodeSystemDefinitions}; never {@link Container#isEmpty() empty}
//     * @throws TranslationException if this criterion can't be translated into a {@link BooleanContainsExpr}
//     */
    public Container<BooleanWhereExpr> toAql(MappingContext mappingContext) {
        //TODO: Expand code using tree! And create OR Expressions for each code
        var mapping = mappingContext.getMapping(termCode).orElseThrow(() -> new MappingNotFoundException(termCode));
        var path = mapping.getValuePath() + "/defining_code/code_string";
        var alias = mapping.getValuePathElements().get(mapping.getValuePathElements().size() - 1).openEhrType().substring(0,1);
        var codeIdentifiedPath = IdentifiedPath.of(alias, path);
        var codeMatchesExpr = MatchesExpr.of(codeIdentifiedPath, ValueListMatchesOperand.of(
            List.of(StringPrimitive.of(termCode.getCode()))));
        var codSystemPath =  mapping.getValuePath() + "/defining_code/terminology_id/value";
        var codeSystemPath = IdentifiedPath.of(alias, codSystemPath);
        var codeSystemExpr = MatchesExpr.of(codeSystemPath, ValueListMatchesOperand.of(
            List.of(StringPrimitive.of(termCode.getSystem()))));
        return Container.of(AndWhereExpr.of(codeMatchesExpr, codeSystemExpr), mapping.getValuePathElements());
    }
//
//    /**
//     * Builds an OR-expression with an expression for each concept of the expansion of
//     * {@code termCode}.
//     */
//    private Container<BooleanContainsExpr> fullExpr(MappingContext mappingContext) {
//        return mappingContext.expandConcept(termCode)
//                .map(termCode -> expr(mappingContext, termCode))
//                .reduce(Container.empty(), Container.OR);
//    }
//
//    private Container<BooleanContainsExpr> expr(MappingContext mappingContext, TermCode termCode) {
//        var mapping = mappingContext.getMapping(termCode)
//                .orElseThrow(() -> new MappingNotFoundException(termCode));
//        var modifiers = Lists.concat(mapping.getFixedCriteria(), this.modifiers);
//        if (modifiers.isEmpty()) {
//            return retrieveExpr(mappingContext, termCode).map(ExistsExpression::of);
//        } else {
//            return retrieveExpr(mappingContext, termCode).flatMap(retrieveExpr -> {
//                var alias = AliasExpression.of(retrieveExpr.getResourceType().substring(0, 1));
//                return modifiersExpr(modifiers, mappingContext, alias)
//                        .map(modifiersExpr -> ExistsExpression.of(QueryExpression.of(SourceClause.of(retrieveExpr,
//                                alias), WhereClause.of(modifiersExpr))));
//            });
//        }
//    }
}
