package sq2aql.model.structured_query;

import sq2aql.Container;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.AndWhereExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.aql.ComparatorExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.RealPrimitive;
import sq2aql.model.common.Comparator;
import sq2aql.model.common.TermCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@code RangeCriterion} will select all patients that have at least one resource represented by that concept and
 * a range of numeric values.
 * <p>
 * Examples are {@code Observation} resources representing the concept of a numeric laboratory value.
 */
public final class RangeCriterion extends AbstractCriterion {

    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;
    private final String unit;

    private RangeCriterion(TermCode concept, BigDecimal lowerBound, BigDecimal upperBound, String unit) {
        super(concept, List.of());
        this.lowerBound = Objects.requireNonNull(lowerBound);
        this.upperBound = Objects.requireNonNull(upperBound);
        this.unit = unit;
    }

    public static RangeCriterion of(TermCode concept, BigDecimal lowerBound, BigDecimal upperBound) {
        return new RangeCriterion(concept, lowerBound, upperBound, null);
    }

    public static RangeCriterion of(TermCode concept, BigDecimal lowerBound, BigDecimal upperBound, String unit) {
        return new RangeCriterion(concept, lowerBound, upperBound, Objects.requireNonNull(unit));
    }

    public BigDecimal getLowerBound() {
        return lowerBound;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public Optional<String> getUnit() {
        return Optional.ofNullable(unit);
    }



    public Container<BooleanWhereExpr> toAql(MappingContext mappingContext) {
        //TODO: currently ignores unit!!!
        var mapping = mappingContext.getMapping(termCode).orElseThrow(() -> new MappingNotFoundException(termCode));
        var ehrType = mapping.getOpenEhrType();
        var path = mapping.getValuePath();
        if (path.isEmpty()) {
            throw new ValuePathNotFoundException(termCode);
        }
        var alias = ehrType.substring(0, 1);
        var identifiedPath = IdentifiedPath.of(alias, path);
        var lowerBoundExpr = ComparatorExpr.of(identifiedPath , Comparator.GREATER_EQUAL, RealPrimitive.of(lowerBound));
        var upperBoundExpr = ComparatorExpr.of(identifiedPath, Comparator.LESS_EQUAL, RealPrimitive.of(upperBound));
        var andWhere = AndWhereExpr.of(lowerBoundExpr, upperBoundExpr);
        return Container.of(andWhere, mapping.getValuePathElements());
    }

//    private Expression quantityExpression(BigDecimal value, String unit) {
//        return unit == null ? QuantityExpression.of(value) : QuantityExpression.of(value, unit);
//    }
}
