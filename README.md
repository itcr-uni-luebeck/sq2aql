# sq2aql

sq2aql is a Java library that translates the Structured Query (SQ) format, an intermediate query format, to Archetype Query Language (AQL). The library uses Maven as a build system.

sq2aql can be used as an alternative to sq2cql in the [Medizininformatik-Initiative feasibility-deploy project](https://github.com/medizininformatik-initiative/feasibility-deploy).


## Usage

To use sq2aql, add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>lorenz.rosenau</groupId>
    <artifactId>sq2aql</artifactId>
    <version>0.0.9-SNAPSHOT</version>
</dependency>
```

In src/test/java/model/structured_query/StructuredQueryTest you can find examples uses:

```java
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
```

The required kds-aql-mapping.json can be generated using [Medizininformatik-Initiative fhir-ontology-generator project](https://github.com/medizininformatik-initiative/fhir-ontology-generator/blob/main/kdsToAqlMapping.py).

## License

sq2aql is licensed under the MIT License. See the LICENSE file for details.

