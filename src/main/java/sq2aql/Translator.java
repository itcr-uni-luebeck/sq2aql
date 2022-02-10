package sq2aql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import sq2aql.model.MappingContext;
import sq2aql.model.ValuePathElement;
import sq2aql.model.aql.AndContainsExpr;
import sq2aql.model.aql.ArchetypePredicate;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.aql.ClassExprOperand;
import sq2aql.model.aql.ComparatorExpr;
import sq2aql.model.aql.ContainsExpr;
import sq2aql.model.aql.ContainsExpression;
import sq2aql.model.aql.FromClause;
import sq2aql.model.aql.FromExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.OrContainsExpr;
import sq2aql.model.aql.PathColumnExpr;
import sq2aql.model.aql.PathPredicate;
import sq2aql.model.aql.SelectClause;
import sq2aql.model.aql.SelectExpr;
import sq2aql.model.aql.SelectQuery;
import sq2aql.model.aql.StringPrimitive;
import sq2aql.model.aql.WhereClause;
import sq2aql.model.common.Comparator;
import sq2aql.model.structured_query.AbstractCriterion;
import sq2aql.model.structured_query.Criterion;
import sq2aql.model.structured_query.StructuredQuery;
import sq2aql.model.structured_query.TranslationException;
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

  private static SelectQuery inclusionOnlyQuery(Container<BooleanWhereExpr> inclusionExpr)
      throws Exception {
    assert inclusionExpr.getExpression().isPresent();

    var select = defaultSelectClause("COMPregistereintrag");

    var containsRoots = new HashSet<ContainsExpression>();

    inclusionExpr.getValuePathElements().stream().forEach(valuePathElements -> {
      valuePathElements.stream().reduce((curr, next) -> {
            if (containsRoots.isEmpty()) {
              var alias = curr.alias();
              var path = ArchetypePredicate.of(curr.archetypeId());
              var classExpr = ClassExprOperand.of(curr.openEhrType(), alias, path);
              var containsExpr = ContainsExpression.of(classExpr);
              containsRoots.add(containsExpr);
            }
            var currAlias = curr.alias();
            var currPath = ArchetypePredicate.of(curr.archetypeId());
            var classExpr = ClassExprOperand.of(curr.openEhrType(), currAlias, currPath);
            var currContainsExpr = ContainsExpression.of(classExpr);

            var nextAlias = next.alias();
            var nextPath = ArchetypePredicate.of(next.archetypeId());
            var nextExpr = ClassExprOperand.of(next.openEhrType(), nextAlias, nextPath);
            var nextContainsExpr = ContainsExpression.of(nextExpr);
            for (var root : containsRoots) {
              if (currContainsExpr.sameClassExpr(root)) {
                root.setContainsExpr(OrContainsExpr.of(
                    (BooleanContainsExpr) root.getContainsExpr(), nextContainsExpr));
              }
            }
            return next;
          }
      );
    });

    var expr = containsRoots.stream().map(c -> Container.of((BooleanContainsExpr)c)).reduce(Container.empty(), Container.CONTAINS_OR);

    var fromClause = defaultFromClause(expr);

    var whereClause = WhereClause.of(inclusionExpr.getExpression().get());

    return SelectQuery.of(select, fromClause, whereClause);
  }


  //    /**
//     * Translates the given {@code structuredQuery} into a CQL {@link Library}.
//     *
//     * @param structuredQuery the Structured Query to translate
//     * @return the translated CQL {@link Library}
//     * @throws TranslationException if the given {@code structuredQuery} can't be translated into a CQL {@link Library}
//     */
  public SelectQuery toAql(StructuredQuery structuredQuery) throws Exception {
    Container<BooleanWhereExpr> inclusionExpr = inclusionExpr(
        structuredQuery.getInclusionCriteria());
//        Container<BooleanContainsExpr> exclusionExpr = exclusionExpr(structuredQuery.getExclusionCriteria());

    if (inclusionExpr.isEmpty()) {
      throw new IllegalStateException("Inclusion criteria lead to empty inclusion expression.");
    }

    return inclusionOnlyQuery(inclusionExpr);

//        return exclusionExpr.isEmpty()
//                ? inclusionOnlyLibrary(inclusionExpr)
//                : library(inclusionExpr, exclusionExpr);
  }

  //    /**
//     * Builds the inclusion expression as conjunctive normal form (CNF) of {@code criteria}.
//     *
//     * @param criteria a list of lists of {@link Criterion} representing a CNF
//     * @return a {@link Container} of the boolean inclusion expression together with the used
//     * {@link CodeSystemDefinition CodeSystemDefinitions}
//     */
  private Container<BooleanWhereExpr> inclusionExpr(List<List<Criterion>> criteria) {
    return criteria.stream().map(this::orExpr).reduce(Container.empty(), Container.AND);
  }

  private Container<BooleanWhereExpr> orExpr(List<Criterion> criteria) {
    return criteria.stream().map(c -> c.toAql(mappingContext))
        .reduce(Container.empty(), Container.OR);
  }

//    /**
//     * Builds the exclusion expression as disjunctive normal form (DNF) of {@code criteria}.
//     *
//     * @param criteria a list of lists of {@link Criterion} representing a DNF
//     * @return a {@link Container} of the boolean exclusion expression together with the used
//     * {@link CodeSystemDefinition CodeSystemDefinitions}
//     */
//    private Container<BooleanContainsExpr> exclusionExpr(List<List<Criterion>> criteria) {
//        return criteria.stream().map(this::andExpr).reduce(Container.empty(), Container.OR);
//    }
//
//    private Container<BooleanContainsExpr> andExpr(List<Criterion> criteria) {
//        return criteria.stream().map(c -> c.toAql(mappingContext)).reduce(Container.empty(), Container.AND);
//    }

  private static SelectClause defaultSelectClause(String alias) {
    var identifiedPath = IdentifiedPath.of(alias);
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

