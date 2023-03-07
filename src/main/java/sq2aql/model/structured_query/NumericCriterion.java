package sq2aql.model.structured_query;

import static sq2aql.model.aql.OpenEhrDataTypes.DV_DURATION;
import static sq2aql.model.aql.OpenEhrDataTypes.DV_QUANTITY;
import static sq2aql.model.aql.OpenEhrDataTypes.UCUM_TO_ISO8601;

import java.util.stream.Stream;
import sq2aql.Container;
import sq2aql.model.Mapping;
import sq2aql.model.aql.AndWhereExpr;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.aql.ComparatorExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.MatchesExpr;
import sq2aql.model.aql.RealPrimitive;
import sq2aql.model.aql.StringPrimitive;
import sq2aql.model.aql.ValueListMatchesOperand;
import sq2aql.model.common.Comparator;
import sq2aql.model.common.TermCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code NumericCriterion} will select all patients that have at least one resource represented
 * by that concept and numeric value.
 * <p>
 * Examples are {@code Observation} resources representing the concept of a numeric laboratory
 * value.
 */
public final class NumericCriterion extends AbstractCriterion {

  private final Comparator comparator;
  private final BigDecimal value;
  private final String unit;

  private NumericCriterion(List<TermCode> concepts, Comparator comparator, BigDecimal value,
      String unit) {
    super(concepts, List.of());
    this.value = Objects.requireNonNull(value);
    this.comparator = Objects.requireNonNull(comparator);
    this.unit = unit;
  }

  /**
   * Returns a {@code NumericCriterion}.
   *
   * @param concepts   the concept the criterion represents
   * @param comparator the comparator that should be used in the value comparison
   * @param value      the value that should be used in the value comparison
   * @return the {@code NumericCriterion}
   */
  public static NumericCriterion of(List<TermCode> concepts, Comparator comparator,
      BigDecimal value) {
    return new NumericCriterion(concepts, comparator, value, null);
  }

  /**
   * Returns a {@code NumericCriterion}.
   *
   * @param concepts   the concept the criterion represents
   * @param comparator the comparator that should be used in the value comparison
   * @param value      the value that should be used in the value comparison
   * @param unit       the unit of the value
   * @return the {@code NumericCriterion}
   */
  public static NumericCriterion of(List<TermCode> concepts, Comparator comparator,
      BigDecimal value,
      String unit) {
    return new NumericCriterion(concepts, comparator, value, Objects.requireNonNull(unit));
  }

  public Comparator getComparator() {
    return comparator;
  }

  public BigDecimal getValue() {
    return value;
  }

  public Optional<String> getUnit() {
    return Optional.ofNullable(unit);
  }

  public Container<BooleanWhereExpr> toAql(MappingContext mappingContext) {
    var mapping = mappingContext.getMapping(termCode)
        .orElseThrow(() -> new MappingNotFoundException(termCode));
    var ehrType = mapping.getOpenEhrType();
    var path = mapping.getTermCodePath() + "/defining_code/code_string";
    var alias = mapping.getTermCodePathElements().get(mapping.getTermCodePathElements().size() - 1)
        .alias();
    var codeIdentifiedPath = IdentifiedPath.of(alias, path);
    var codeMatchesExpr = MatchesExpr.of(codeIdentifiedPath, ValueListMatchesOperand.of(
        List.of(StringPrimitive.of(termCode.getCode()))));
    var codSystemPath = mapping.getTermCodePath() + "/defining_code/terminology_id/value";
    var codeSystemPath = IdentifiedPath.of(alias, codSystemPath);
    var codeSystemExpr = MatchesExpr.of(codeSystemPath, ValueListMatchesOperand.of(
        List.of(StringPrimitive.of(termCode.getSystem()))));
    Container<BooleanWhereExpr> termCodeContainer = Container.of(
        AndWhereExpr.of(codeMatchesExpr, codeSystemExpr), mapping.getTermCodePathElements());
    if (ehrType.equals(DV_QUANTITY)) {
      var quantityContainer = quantityToAql(mapping);
      return Stream.of(termCodeContainer, quantityContainer)
          .reduce(Container.empty(), Container.WHERE_AND);
    } else if (ehrType.equals(DV_DURATION)) {
      var durationContainer = durationToAql(mapping);
      return Stream.of(termCodeContainer, durationContainer)
          .reduce(Container.empty(), Container.WHERE_AND);
    } else {
      // Throw!
    }
    return null;
  }


  /**
   * @return String representation of the value as duration. Aql uses the format 'P20Y' to represent
   * 20 years.
   */
  private String getAqlDurationRepresentation() {
    return "P%s%s".formatted(value.setScale(0), UCUM_TO_ISO8601.get(unit));
  }


  private Container<BooleanWhereExpr> durationToAql(Mapping mapping) {
    var path = mapping.getValuePath() + "/value";
    var alias = mapping.getValuePathElements().get(mapping.getValuePathElements().size() - 1)
        .alias();
    var valueIdentifiedPath = IdentifiedPath.of(alias, path);
    var comparatorExpr = ComparatorExpr.of(valueIdentifiedPath, comparator,
        StringPrimitive.of(getAqlDurationRepresentation()));
    return Container.of(comparatorExpr, mapping.getValuePathElements());
  }

  private Container<BooleanWhereExpr> quantityToAql(Mapping mapping) {
    var path = mapping.getValuePath();
    var alias = mapping.getValuePathElements().get(mapping.getValuePathElements().size() - 1)
        .alias();
    var valuePath = path + "/magnitude";
    var valueIdentifiedPath = IdentifiedPath.of(alias, valuePath);
    var comparatorExpr = ComparatorExpr.of(valueIdentifiedPath, comparator,
        RealPrimitive.of(value));

    if (!getUnit().isEmpty()) {
      var unitPath = path + "/units";
      var unitIdentifiedPath = IdentifiedPath.of(alias, unitPath);
      var matchesExpr = MatchesExpr.of(unitIdentifiedPath, ValueListMatchesOperand.of(
          List.of(StringPrimitive.of(unit))));
      return Container.of(AndWhereExpr.of(comparatorExpr, matchesExpr),
          mapping.getValuePathElements());
    }
    return Container.of(comparatorExpr, mapping.getValuePathElements());
  }

}
