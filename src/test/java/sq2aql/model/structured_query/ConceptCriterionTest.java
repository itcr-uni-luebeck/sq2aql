package sq2aql.model.structured_query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
import sq2aql.model.aql.ClassExprOperand;
import sq2aql.model.aql.ContainsExpression;
import sq2aql.model.aql.FromClause;
import sq2aql.model.aql.FromExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.PathColumnExpr;
import sq2aql.model.aql.SelectClause;
import sq2aql.model.aql.SelectExpr;
import sq2aql.model.aql.SelectQuery;
import sq2aql.model.aql.WhereClause;
import sq2aql.model.common.TermCode;

public class ConceptCriterionTest {

  public static final TermCode GENDER_MALE = TermCode.of(
      "http://hl7.org/fhir/administrative-gender", "male", "Male");


  public static final MappingContext MAPPING_CONTEXT = MappingContext.of(Map.of(GENDER_MALE,
      Mapping.of(GENDER_MALE, "DV_CODED_TEXT", List.of(
              ValuePathElement.of("COMPOSITION", "openEHR-EHR-COMPOSITION.registereintrag.v1"),
              ValuePathElement.of("EVALUATION", "openEHR-EHR-EVALUATION.gender.v1")),"/data[at0002]/items[at0022]/value",
          List.of(), "")), ConceptNode.of());


  @Disabled
  @Test
  void toAql() throws Exception {
    Criterion criterion = ConceptCriterion.of(List.of(GENDER_MALE));

    Container<BooleanWhereExpr> container = criterion.toAql(MAPPING_CONTEXT);

    var identifiedPath = IdentifiedPath.of("C");
    var columnExpr = PathColumnExpr.of(identifiedPath);
    var selectExpr = SelectExpr.of(columnExpr);
    var selectExpressions = new ArrayList<SelectExpr>();
    selectExpressions.add(selectExpr);
    var select = SelectClause.of(true, selectExpressions);
    var ehrClassExprOperand = ClassExprOperand.of("EHR", "e");
    var containsExpression = ContainsExpression.of(ehrClassExprOperand,
        AbstractCriterion.containsExpr(MAPPING_CONTEXT.getMapping(GENDER_MALE).get()
                .getValuePathElements(), Optional.empty())
            .getExpression().orElseThrow());
    var fromExpr = FromExpr.of(containsExpression);
    var fromClause = FromClause.of(fromExpr);
    var whereExpr = container.getExpression().orElseThrow();
    var whereClause = WhereClause.of(whereExpr);

    assertEquals("""
            COMPOSITION C[openEHR-EHR-COMPOSITION.registereintrag.v1]
            CONTAINS EVALUATION E[openEHR-EHR-EVALUATION.gender.v1]""",
        AbstractCriterion.containsExpr(MAPPING_CONTEXT.getMapping(GENDER_MALE).get()
                .getValuePathElements(), Optional.empty())
            .getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));

    assertEquals("""
            (E/data[at0002]/items[at0022]/value/defining_code/code_string MATCHES {'male'} AND
            E/data[at0002]/items[at0022]/value/defining_code/terminology_id/value MATCHES {'http://hl7.org/fhir/administrative-gender'})""",
        container.getExpression().map(e -> e.print(PrintContext.ZERO)).orElse(""));

    assertEquals("""
            SELECT DISTINCT
            C
            FROM
            EHR e
            CONTAINS COMPOSITION C[openEHR-EHR-COMPOSITION.registereintrag.v1]
            CONTAINS EVALUATION E[openEHR-EHR-EVALUATION.gender.v1]
            WHERE (E/data[at0002]/items[at0022]/value/defining_code/code_string MATCHES {'male'} AND
            E/data[at0002]/items[at0022]/value/defining_code/terminology_id/value MATCHES {'http://hl7.org/fhir/administrative-gender'})""",
        SelectQuery.of(select, fromClause, whereClause).print(PrintContext.ZERO));

  }
}
