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
import org.junit.jupiter.api.Disabled;
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
              "/content[openEHR-EHR-OBSERVATION.age.v0]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value",
              List.of(), "")
      ),
      ConceptNode.of());

  @Test
  void fromJson() throws Exception {
    var mapper = new ObjectMapper();

    var sq = mapper.readValue("""
        {"version":"http://to_be_decided.com/draft-1/schema#","inclusionCriteria":[[{"termCodes":[{"code":"30525-0","system":"http://loinc.org","display":"Age"},{"code":"424144002","system":"http://snomed.info/sct","display":"Current chronological age (observable entity)"}],"attributeFilters":[],"valueFilter":{"type":"quantity-comparator","selectedConcepts":[],"comparator":"gt","unit":{"code":"a","display":"a"},"value":20.0,"minValue":0.0,"maxValue":0.0},"timeRestriction":{}}]]}
        """, StructuredQuery.class);

    var criterion = (NumericCriterion) sq.getInclusionCriteria().get(0).get(0);
    assertEquals(AGE, criterion.getTermCode());
    assertEquals(GREATER_THAN, criterion.getComparator());
    assertEquals(BigDecimal.valueOf(20.0), criterion.getValue());
    assertEquals(Optional.of("a"), criterion.getUnit());
  }


  StructuredQuery get_sq() throws Exception {
    var mapper = new ObjectMapper();

    return mapper.readValue("""
        {
            "version": "http://to_be_decided.com/draft-1/schema#",
            "display": "",
            "inclusionCriteria": [
                [
                    {
                        "termCodes": [
                            {
                                "code": "72166-2",
                                "system": "http://loinc.org",
                                "display": "Tobacco smoking status"
                            } ],
                        "valueFilter": {
                            "selectedConcepts": [
                                {
                                    "code": "LA18976-3",
                                    "system": "http://loinc.org",
                                    "display": "Current every day smoker"
                                }
                            ],
                            "type": "concept"
                        }
                    }
                ]
            ]
        }                                                 """, StructuredQuery.class);
  }


  @Disabled
  @Test
  void toAql() {
    Criterion criterion = NumericCriterion.of(List.of(RESPIRATORY_RATE), GREATER_THAN,
        BigDecimal.valueOf(20), "/min");

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    assert container != null;
    assertEquals("""
            (O/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/magnitude > 20 AND
            O/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/units MATCHES {'/min'})""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));
  }

  @Disabled
  @Test
  void toAql_duration() throws Exception {
    var sq = get_sq();
    var objectMapper = new ObjectMapper();

    var mappingsFile = "C:\\Users\\Lorenz\\Documents\\Programmieren\\Third_Party\\sq2aql\\src\\test\\resources\\gecco-aql-mapping.json";

    var mappings = objectMapper.readValue(new File(mappingsFile), Mapping[].class);
    var translator = Translator.of(MappingContext.of(
        Stream.of(mappings).collect(
            Collectors.toMap(Mapping::getConcept, Function.identity(), (a, b) -> a)),
        ConceptNode.of()));
    assertEquals("""
            SELECT DISTINCT
            C
            FROM
            EHR e
            CONTAINS COMPOSITION C[openEHR-EHR-COMPOSITION.registereintrag.v1]
            CONTAINS OBSERVATION O[openEHR-EHR-OBSERVATION.age.v0]
            WHERE O/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value/value > 'P20Y'""",
        translator.toAql(sq).print(PrintContext.ZERO));
  }


  StructuredQuery get_numeric_sq() throws Exception {
    var mapper = new ObjectMapper();

    return mapper.readValue("""
        {
          "version": "http://to_be_decided.com/draft-1/schema#",
          "display": "",
          "inclusionCriteria": [
            [
              {
                "termCodes": [
                  {
                    "code": "19113-0",
                    "system": "http://loinc.org",
                    "display": "IgE"
                  }
                ],
                "valueFilter": {
                  "selectedConcepts": [],
                  "type": "quantity-comparator",
                  "unit": {
                    "code": "[IU]/L",
                    "display": "[IU]/L"
                  },
                  "value": 0,
                  "comparator": "gt"
                }
              }
            ]
          ]
        }""", StructuredQuery.class);
  }


  @Test
  void toAQL_numeric() throws Exception {
    var sq = get_numeric_sq();
    var objectMapper = new ObjectMapper();

    var mappingsFile = "C:\\Users\\Lorenz\\Documents\\Programmieren\\Third_Party\\sq2aql\\src\\test\\resources\\kds-aql-mapping.json";

    var mappings = objectMapper.readValue(new File(mappingsFile), Mapping[].class);
    var translator = Translator.of(MappingContext.of(
        Stream.of(mappings).collect(
            Collectors.toMap(Mapping::getConcept, Function.identity(), (a, b) -> a)),
        ConceptNode.of()));
    assertEquals("""
            SELECT DISTINCT
            e/ehr_id/value
            FROM
            EHR e
            CONTAINS COMPOSITION COMPreport-result[openEHR-EHR-COMPOSITION.report-result.v1]
            CONTAINS OBSERVATION OBSElaboratory_test_result[openEHR-EHR-OBSERVATION.laboratory_test_result.v1]
            CONTAINS CLUSTER CLUSlaboratory_test_analyte[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1]  
            WHERE (CLUSlaboratory_test_analyte/items[at0001]/value/magnitude > 0 AND
            CLUSlaboratory_test_analyte/items[at0001]/value/units MATCHES {'[IU]/L'})""",
        translator.toAql(sq).print(PrintContext.ZERO));

  }


  StructuredQuery get_concept_sq() throws Exception {
    var mapper = new ObjectMapper();

    return mapper.readValue("""
        {
          "version": "http://to_be_decided.com/draft-1/schema#",
          "display": "",
          "inclusionCriteria": [
            [
              {
                "termCodes": [
                  {
                    "code": "94533-7",
                    "system": "http://loinc.org",
                    "display": "SARS coronavirus 2 N gene [Presence] in Respiratory specimen by NAA with probe detection"
                  }
                ],
                "valueFilter": {
                  "selectedConcepts": [
                    {
                      "code": "LA6576-8",
                      "display": "Positive",
                      "system": "http://loinc.org"
                    }
                  ],
                  "type": "concept"
                }
              }
            ]
          ]
        }""", StructuredQuery.class);
  }


  @Test
  void toAql_concept() throws Exception {
    var sq = get_concept_sq();
    var objectMapper = new ObjectMapper();

    var mappingsFile = "C:\\Users\\Lorenz\\Documents\\Programmieren\\Third_Party\\sq2aql\\src\\test\\resources\\kds-aql-mapping.json";

    var mappings = objectMapper.readValue(new File(mappingsFile), Mapping[].class);
    var translator = Translator.of(MappingContext.of(
        Stream.of(mappings).collect(
            Collectors.toMap(Mapping::getConcept, Function.identity(), (a, b) -> a)),
        ConceptNode.of()));
    assertEquals("""
            SELECT DISTINCT
            e/ehr_id/value
            FROM
            EHR e
            CONTAINS COMPOSITION COMPreport-result[openEHR-EHR-COMPOSITION.report-result.v1]
            CONTAINS OBSERVATION OBSElaboratory_test_result[openEHR-EHR-OBSERVATION.laboratory_test_result.v1]
            CONTAINS CLUSTER CLUSlaboratory_test_analyte[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1]
            WHERE (CLUSlaboratory_test_analyte/items[at0024]/value/defining_code/code_string MATCHES {'94533-7'} AND
            CLUSlaboratory_test_analyte/items[at0024]/value/defining_code/terminology_id/value MATCHES {'http://loinc.org'} AND
            CLUSlaboratory_test_analyte/items[at0001]/value/defining_code/code_string MATCHES {'LA6576-8'} AND
            CLUSlaboratory_test_analyte/items[at0001]/value/defining_code/terminology_id/value MATCHES {'http://loinc.org'})""",
        translator.toAql(sq).print(PrintContext.ZERO));

  }



}
