package sq2aql.model.structured_query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sq2aql.model.common.Comparator.GREATER_THAN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import sq2aql.Container;
import sq2aql.PrintContext;
import sq2aql.Translator;
import sq2aql.model.ConceptNode;
import sq2aql.model.Mapping;
import sq2aql.model.MappingContext;
import sq2aql.model.ValuePathElement;
import sq2aql.model.aql.BooleanWhereExpr;
import sq2aql.model.common.TermCode;

public class StructuredQueryTest {

  public static final TermCode RESPIRATORY_RATE = TermCode.of("http://loinc.org", "9279-1",
      "Respiratory rate");
  public static final TermCode AGE = TermCode.of("http://loinc.org", "30525-0", "Age");


  public static final MappingContext MAPPING_CONTEXT = MappingContext.of(Map.of(
          RESPIRATORY_RATE, Mapping.of(RESPIRATORY_RATE, "DV_QUANTITY", List.of(
                  ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1"),
                  ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.respiration.v2")),
              "/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(), ""),
          AGE, Mapping.of(AGE, "DV_DURATION", List.of(
                  ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1"),
                  ValuePathElement.of("OBSERVATION", "openEHR-EHR-OBSERVATION.age.v0")),
              "/content[openEHR-EHR-OBSERVATION.age.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value", List.of(), "")
      ),
      ConceptNode.of());

  @Test
  void fromJson() throws Exception {
    var mapper = new ObjectMapper();

    var sq = (StructuredQuery) mapper.readValue("""
        {
          "inclusionCriteria": [
            [
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
            ]
          ],
          "version": "http://to_be_decided.com/draft-1/schema#"
        }
            """, StructuredQuery.class);

    var criterion = (NumericCriterion)sq.getInclusionCriteria().get(0).get(0);
    assertEquals(RESPIRATORY_RATE, criterion.getTermCode());
    assertEquals(GREATER_THAN, criterion.getComparator());
    assertEquals(BigDecimal.valueOf(50), criterion.getValue());
    assertEquals(Optional.of("g/dl"), criterion.getUnit());
  }


  StructuredQuery get_sq() throws Exception {
    var mapper = new ObjectMapper();

    return mapper.readValue("""
        {
          "inclusionCriteria": [
            [
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
              },
               {
                "termCode": {
                  "system": "http://loinc.org",
                  "code": "30525-0",
                  "display": "Age"
                },
                "valueFilter": {
                  "type": "quantity-comparator",
                  "comparator": "gt",
                  "unit": {
                    "code": "a"
                  },
                  "value": 20
                }
              }
          ],
            [

            ]
              
          ],
          "version": "http://to_be_decided.com/draft-1/schema#"
        }
            """, StructuredQuery.class);
  }


  @Test
  void toAql() {
    Criterion criterion = NumericCriterion.of(RESPIRATORY_RATE, GREATER_THAN,
        BigDecimal.valueOf(20), "/min");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assert container != null;
    assertEquals("""
            C/content[openEHR-EHR-OBSERVATION.respiration.v2]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/magnitude > 20 AND
            C/content[openEHR-EHR-OBSERVATION.respiration.v2]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/units MATCHES {'/min'}""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }


  @Test
  void toAql_duration() throws Exception {
    var sq = get_sq();
    var objectMapper = new ObjectMapper();

    var mappingsFile = "C:\\Users\\Lorenz\\Documents\\Programmieren\\Third_Party\\sq2aql\\src\\test\\resources\\gecco-aql-mapping.json";

    var mappings = objectMapper.readValue(new File(mappingsFile), Mapping[].class);
    var translator = Translator.of(MappingContext.of(
        Stream.of(mappings).collect(
            Collectors.toMap(Mapping::getConcept, Function.identity(), (a, b) -> a)), ConceptNode.of()));
    System.out.println(translator.toAql(sq).print(PrintContext.ZERO));
  }


}
