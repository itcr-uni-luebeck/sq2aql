package sq2aql.model.structured_query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sq2aql.model.common.Comparator.GREATER_THAN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
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


  public static final MappingContext MAPPING_CONTEXT = MappingContext.of(
      Map.of(RESPIRATORY_RATE, Mapping.of(RESPIRATORY_RATE, "DV_QUANTITY", List.of(
          ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.report-result.v1"),
          ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.laboratory_test_result.v1"),
          ValuePathElement.of("CLUSTER", "openEHR-EHR-CLUSTER.laboratory_test_analyte.v1")
      ), "/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(
          ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.report-result.v1"),
          ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.laboratory_test_result.v1"),
          ValuePathElement.of("CLUSTER", "openEHR-EHR-CLUSTER.laboratory_test_analyte.v1")
      ), "/items[at0024]/value"), AGE, Mapping.of(AGE, "DV_DURATION",
          List.of(ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1"),
              ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.age.v0")),
          "/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(), "")),
      ConceptNode.of());

  @Test
  void fromJson() throws Exception {
    var mapper = new ObjectMapper();

    var criterion = (NumericCriterion) mapper.readValue("""
        {
          "termCodes": [{
            "system": "http://loinc.org",
            "code": "9279-1",
            "display": "Respiratory rate"
          }],
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
    Criterion criterion = NumericCriterion.of(List.of(RESPIRATORY_RATE), GREATER_THAN,
        BigDecimal.valueOf(20), "/min");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assertEquals("""
            (CLUSlaboratory_test_analyte/items[at0024]/value/defining_code/code_string MATCHES {'9279-1'} AND
            CLUSlaboratory_test_analyte/items[at0024]/value/defining_code/terminology_id/value MATCHES {'http://loinc.org'} AND
            CLUSlaboratory_test_analyte/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/magnitude > 20 AND
            CLUSlaboratory_test_analyte/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/units MATCHES {'/min'})""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }

  @Disabled
  @Test
  void toAql_duration() {
    Criterion criterion = NumericCriterion.of(List.of(AGE), GREATER_THAN, BigDecimal.valueOf(20),
        "a");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assertEquals("""
            OBSEage/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/value > 'P20Y'""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }

}
