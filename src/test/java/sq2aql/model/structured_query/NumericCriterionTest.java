package sq2aql.model.structured_query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sq2aql.model.common.Comparator.GREATER_THAN;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sq2aql.Container;
import sq2aql.PrintContext;
import sq2aql.model.ConceptNode;
import sq2aql.model.Mapping;
import sq2aql.model.MappingContext;
import sq2aql.model.ValuePathElement;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.common.TermCode;

public class NumericCriterionTest {

  public static final TermCode RESPIRATORY_RATE = TermCode.of("http://loinc.org", "9279-1",
      "Respiratory rate");
  public static final TermCode AGE = TermCode.of("num.abide", "age", "age");


  public static final MappingContext MAPPING_CONTEXT = MappingContext.of(Map.of(
          RESPIRATORY_RATE, Mapping.of(RESPIRATORY_RATE, "DV_QUANTITY", List.of(
                  ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1"),
                  ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.respiration.v2")),
              "/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(), ""),
          AGE, Mapping.of(AGE, "DV_DURATION", List.of(
                  ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1")),
              "/content[openEHR-EHR-OBSERVATION.age.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(), "")
          ),
      ConceptNode.of());

  @Test
  void fromJson() throws Exception {
    var mapper = new ObjectMapper();

    var criterion = (NumericCriterion) mapper.readValue("""
        {
          "termCode": {
            "system": "http://loinc.org",
            "code": "9279-1",
            "display": "Respiratory rate"
          },
          "valueFilter": {
            "type": "quantity-comparator",
            "comparator": "gt",
            "unit": {
              "code": "g/dl"
            },
            "value": 50
          }
        }
        """, Criterion.class);

    assertEquals(RESPIRATORY_RATE, criterion.getTermCode());
    assertEquals(GREATER_THAN, criterion.getComparator());
    assertEquals(BigDecimal.valueOf(50), criterion.getValue());
    assertEquals(Optional.of("g/dl"), criterion.getUnit());
  }

  @Test
  void toAql() {
    Criterion criterion = NumericCriterion.of(RESPIRATORY_RATE, GREATER_THAN,
        BigDecimal.valueOf(20), "/min");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assertEquals("""
            C/content[openEHR-EHR-OBSERVATION.respiration.v2]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/magnitude > 20 AND
            C/content[openEHR-EHR-OBSERVATION.respiration.v2]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/units MATCHES {'/min'}""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }


  @Test
  void toAql_duration() {
    Criterion criterion = NumericCriterion.of(AGE, GREATER_THAN,
        BigDecimal.valueOf(20), "a");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assertEquals("""
            C/content[openEHR-EHR-OBSERVATION.age.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/value > 'P20Y'""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }

}
