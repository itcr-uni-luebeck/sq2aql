package sq2aql.model.structured_query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import sq2aql.Container;
import sq2aql.model.MappingContext;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.common.Comparator;
import sq2aql.model.common.TermCode;

import java.util.stream.StreamSupport;

/**
 * A single, atomic criterion in Structured Query.
 *
 * @author Alexander Kiel
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public interface Criterion {

//    /**
//     * A criterion that always evaluates to {@code true}.
//     */
//    Criterion TRUE = mappingContext -> Container.of(BooleanContainsExpr.TRUE);
//
//    /**
//     * A criterion that always evaluates to {@code false}.
//     */
//    Criterion FALSE = mappingContext -> Container.of(BooleanContainsExpr.FALSE);

    @JsonCreator
    static Criterion create(@JsonProperty("termCodes") List<TermCode> termCodes,
                            @JsonProperty("valueFilter") ObjectNode valueFilter,
                            @JsonProperty("timeRestriction") ObjectNode timeRestriction) {
        if (valueFilter == null) {
            return ConceptCriterion.of(termCodes);
        }

        var type = valueFilter.get("type").asText();
        if ("quantity-comparator".equals(type)) {
            var comparator = Comparator.fromJson(valueFilter.get("comparator").asText());
            var value = valueFilter.get("value").decimalValue();
            var unit = valueFilter.get("unit");
            if (unit == null) {
                return NumericCriterion.of(termCodes, comparator, value);
            } else {
                return NumericCriterion.of(termCodes, comparator, value, unit.get("code").asText());
            }
        }
        if ("quantity-range".equals(type)) {
            var lowerBound = valueFilter.get("minValue").decimalValue();
            var upperBound = valueFilter.get("maxValue").decimalValue();
            var unit = valueFilter.get("unit");
            if (unit == null) {
                return RangeCriterion.of(termCodes, lowerBound, upperBound);
            } else {
                return RangeCriterion.of(termCodes, lowerBound, upperBound, unit.get("code").asText());
            }
        }
        if ("concept".equals(type)) {
            var selectedConcepts = valueFilter.get("selectedConcepts");
            if (selectedConcepts == null) {
                throw new IllegalArgumentException("Missing `selectedConcepts` key in concept criterion.");
            }
            return ValueSetCriterion.of(termCodes, StreamSupport.stream(selectedConcepts.spliterator(), false)
                    .map(TermCode::fromJsonNode).toArray(TermCode[]::new));
        }
        throw new IllegalArgumentException("unknown valueFilter type: " + type);
    }

    /**
     * Translates this criterion into a AQL expression.
     *
     * @param mappingContext contains the mappings needed to create the CQL expression
     * @return a {@link Container} of the AQL expression together with its used
     * */
    Container<BooleanWhereExpr> toAql(MappingContext mappingContext);
}
