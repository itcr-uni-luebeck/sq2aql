package sq2aql;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.Optional;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.aql.ClassExprOperand;
import sq2aql.model.aql.ContainsExpression;
import sq2aql.model.aql.FromClause;
import sq2aql.model.aql.FromExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.LogicalExpression;
import sq2aql.model.aql.Minus;
import sq2aql.model.aql.PathColumnExpr;
import sq2aql.model.aql.SelectClause;
import sq2aql.model.aql.SelectExpr;
import sq2aql.model.aql.SelectQuery;
import sq2aql.model.aql.WhereClause;
import sq2aql.model.structured_query.AbstractCriterion;
import sq2aql.model.structured_query.Criterion;
import sq2aql.model.structured_query.StructuredQuery;
import java.util.List;

/**
 * The translator from Structured Query to AQL.
 * <p>
 * It needs {@code mappings} and will produce a AQL {@link SelectQuery} by calling {@link
 * #toAql(StructuredQuery) toAql}.
 * <p>
 * Instances are immutable and thread-safe.
 *
 * @author Lorenz Rosenau
 */
public final class Translator {

  private final MappingContext mappingContext;

  private Translator(MappingContext mappingContext) {
    this.mappingContext = mappingContext;
  }

  /**
   * Returns a translator without any mappings.
   *
   * @return a translator without any mappings
   */
  public static Translator of() {
    return new Translator(MappingContext.of());
  }

  /**
   * Returns a translator with mappings defined in {@code mappingContext}.
   *
   * @return a translator with mappings defined in {@code mappingContext}
   */
  public static Translator of(MappingContext mappingContext) {
    return new Translator(mappingContext);
  }


  public Container<LogicalExpression> toSelectQuery(Container<BooleanWhereExpr> whereExpression) {
    assert whereExpression.getExpression().isPresent();

    HashSet<Container<BooleanContainsExpr>> containsRoots = new HashSet<>();

    whereExpression.getValuePathElements().forEach(valuePathElements -> {
          try {
            var contains = AbstractCriterion.containsExpr(valuePathElements, Optional.empty());
            containsRoots.add(contains);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    var expr = containsRoots.stream().reduce(Container.empty(), Container.CONTAINS_OR);
    var select = defaultSelectClause();

    var fromClause = defaultFromClause(expr);

    var whereClause = WhereClause.of(whereExpression.getExpression().get());

    return Container.of(SelectQuery.of(select, fromClause, whereClause));

  }


  public LogicalExpression toAql(StructuredQuery structuredQuery) {
    Container<LogicalExpression> inclusionExpr = inclusionExpr(
        structuredQuery.getInclusionCriteria());
    Container<LogicalExpression> exclusionExpr = exclusionExpr(
        structuredQuery.getExclusionCriteria());

    if (inclusionExpr.isEmpty()) {
      throw new IllegalStateException("Inclusion criteria lead to empty inclusion expression.");
    }

    return exclusionExpr.isEmpty()
            ? inclusionExpr.getExpression().get()
            : Minus.of(inclusionExpr.getExpression().get(), exclusionExpr.getExpression().get());
  }


  private Container<LogicalExpression> inclusionExpr(List<List<Criterion>> criteria) {
    return criteria.stream().map(this::orExpr).reduce(Container.empty(), Container.AND);
  }

  private Container<LogicalExpression> orExpr(List<Criterion> criteria) {
    return criteria.stream().map(c -> c.toAql(mappingContext)).map(this::toSelectQuery)
        .reduce(Container.empty(), Container.OR);
  }


  private Container<LogicalExpression> exclusionExpr(List<List<Criterion>> criteria) {
    return criteria.stream().map(this::andExpr).reduce(Container.empty(), Container.OR);
  }

  private Container<LogicalExpression> andExpr(List<Criterion> criteria) {
    return criteria.stream().map(c -> c.toAql(mappingContext)).map(this::toSelectQuery)
        .reduce(Container.empty(), Container.AND);
  }

  private static SelectClause defaultSelectClause() {
    var identifiedPath = IdentifiedPath.of("e/ehr_id/value");
    var columnExpr = PathColumnExpr.of(identifiedPath);
    var selectExpr = SelectExpr.of(columnExpr);
    var selectExpressions = new ArrayList<SelectExpr>();
    selectExpressions.add(selectExpr);
    return SelectClause.of(true, selectExpressions);
  }


  // TODO: Probably we want to collect the ContainsExpr in the Container and extract them from the container and concatenate them with OR here
  private static FromClause defaultFromClause(Container<BooleanContainsExpr> container) {
    var ehrClassExprOperand = ClassExprOperand.of("EHR", "e");
    var containsExpression = ContainsExpression.of(ehrClassExprOperand,
        container.getExpression().orElseThrow());
    var fromExpr = FromExpr.of(containsExpression);
    return FromClause.of(fromExpr);
  }

}

