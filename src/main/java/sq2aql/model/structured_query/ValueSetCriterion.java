package sq2aql.model.structured_query;

import java.util.Objects;
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
 * A {@code ValueSetCriterion} will select all patients that have at least one resource represented
 * by that concept and coded value.
 * <p>
 * Examples are {@code Observation} resources representing the concept of a coded laboratory value.
 */
public final class ValueSetCriterion extends AbstractCriterion {

  private final List<TermCode> selectedConcepts;

  private ValueSetCriterion(List<TermCode> concepts, List<TermCode> selectedConcepts) {
    super(concepts, List.of());
    this.selectedConcepts = selectedConcepts;
  }

  /**
   * Returns a {@code ValueSetCriterion}.
   *
   * @param concepts         the concept the criterion represents
   * @param selectedConcepts at least one selected value concept
   * @return the {@code ValueSetCriterion}
   */
  public static ValueSetCriterion of(List<TermCode> concepts, TermCode... selectedConcepts) {
    if (selectedConcepts == null || selectedConcepts.length == 0) {
      throw new IllegalArgumentException("empty selected concepts");
    }
    return new ValueSetCriterion(concepts, List.of(selectedConcepts));
  }

  public List<TermCode> getSelectedConcepts() {
    return selectedConcepts;
  }

  public Container<BooleanWhereExpr> toAql(MappingContext mappingContext) {
    var mapping = mappingContext.getMapping(termCode)
        .orElseThrow(() -> new MappingNotFoundException(termCode));
    if(Objects.nonNull(mapping.getTermCodePath())) {
      var path = mapping.getTermCodePath() + "/defining_code/code_string";
      var alias = mapping.getTermCodePathElements()
          .get(mapping.getTermCodePathElements().size() - 1)
          .alias();
      var codeIdentifiedPath = IdentifiedPath.of(alias, path);
      var codeMatchesExpr = MatchesExpr.of(codeIdentifiedPath, ValueListMatchesOperand.of(
          List.of(StringPrimitive.of(termCode.getCode()))));
      var codSystemPath = mapping.getTermCodePath() + "/defining_code/terminology_id/value";
      var codeSystemPath = IdentifiedPath.of(alias, codSystemPath);
      var codeSystemExpr = MatchesExpr.of(codeSystemPath, ValueListMatchesOperand.of(
          List.of(StringPrimitive.of(termCode.getSystem()))));
      var definingCodeExpr = Container.of(
          (BooleanWhereExpr) AndWhereExpr.of(codeMatchesExpr, codeSystemExpr),
          mapping.getTermCodePathElements());
      var selectedCodeExpr = selectedConceptsWhereExpr(mappingContext);
      return List.of(definingCodeExpr, selectedCodeExpr).stream().reduce(Container.empty(), Container.AND);
    }
    return selectedConceptsWhereExpr(mappingContext);
  }


  private Container<BooleanWhereExpr> selectedConceptsWhereExpr(MappingContext mappingContext) {
        return selectedConcepts.stream().map(
            concept -> {
              var mapping = mappingContext.getMapping(termCode)
                  .orElseThrow(() -> new MappingNotFoundException(termCode));
              var path = mapping.getValuePath() + "/defining_code/code_string";
              var alias = mapping.getValuePathElements()
                  .get(mapping.getValuePathElements().size() - 1)
                  .alias();
              var codeIdentifiedPath = IdentifiedPath.of(alias, path);
              var codeMatchesExpr = MatchesExpr.of(codeIdentifiedPath, ValueListMatchesOperand.of(
                  List.of(StringPrimitive.of(concept.getCode()))));
              var codSystemPath = mapping.getValuePath() + "/defining_code/terminology_id/value";
              var codeSystemPath = IdentifiedPath.of(alias, codSystemPath);
              var codeSystemExpr = MatchesExpr.of(codeSystemPath, ValueListMatchesOperand.of(
                  List.of(StringPrimitive.of(concept.getSystem()))));
              var definingCode = Container.of((BooleanWhereExpr) AndWhereExpr.of(codeMatchesExpr, codeSystemExpr),
                  mapping.getValuePathElements());
              return definingCode;
            }).reduce(Container.empty(), Container.OR);
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
