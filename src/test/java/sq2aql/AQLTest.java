package sq2aql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import sq2aql.PrintContext;
import sq2aql.model.aql.ArchetypePredicate;
import sq2aql.model.aql.ClassExprOperand;
import sq2aql.model.aql.PathColumnExpr;
import sq2aql.model.aql.ComparatorExpr;
import sq2aql.model.aql.ContainsExpression;
import sq2aql.model.aql.FromClause;
import sq2aql.model.aql.FromExpr;
import sq2aql.model.aql.IdentifiedPath;
import sq2aql.model.aql.SelectClause;
import sq2aql.model.aql.SelectExpr;
import sq2aql.model.aql.SelectQuery;
import sq2aql.model.aql.StringPrimitive;
import sq2aql.model.aql.WhereClause;
import sq2aql.model.common.Comparator;

public class AQLTest {

  @Test
  void create_age_query() {
    var ageOver20 = """
        SELECT
        o
        FROM
        EHR e
        CONTAINS COMPOSITION c[openEHR-EHR-COMPOSITION.registereintrag.v1]
        CONTAINS OBSERVATION o[openEHR-EHR-OBSERVATION.age.v0]
        WHERE o/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value > 'P20Y'""";




    var identifiedPath = IdentifiedPath.of("o");
    var columnExpr = PathColumnExpr.of(identifiedPath);
    var selectExpr = SelectExpr.of(columnExpr);
    var selectExpressions = new ArrayList<SelectExpr>();
    selectExpressions.add(selectExpr);
    var select = SelectClause.of(false, selectExpressions);


    var ageArchetypePredicate = ArchetypePredicate.of("openEHR-EHR-OBSERVATION.age.v0");
    var observationClassExpr = ClassExprOperand.of("OBSERVATION", "o", ageArchetypePredicate);
    var observationContainsExpr = ContainsExpression.of(observationClassExpr);
    var registerEntry = ArchetypePredicate.of("openEHR-EHR-COMPOSITION.registereintrag.v1");
    var compositionClassExprOperand = ClassExprOperand.of("COMPOSITION", "c", registerEntry);
    var compositionContainsExpr = ContainsExpression.of(compositionClassExprOperand, observationContainsExpr);
    var ehrClassExprOperand = ClassExprOperand.of("EHR", "e");
    var ehrContainsExpr = ContainsExpression.of(ehrClassExprOperand, compositionContainsExpr);
    var fromExpr =  FromExpr.of(ehrContainsExpr);
    var fromClause = FromClause.of(fromExpr);

    var objectPath = "/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value";
    var duration = StringPrimitive.of("P20Y");
    var gt = Comparator.GREATER_THAN;
    var path = IdentifiedPath.of("o", objectPath);
    var whereExpr = ComparatorExpr.of(path, gt, duration);
    var whereClause = WhereClause.of(whereExpr);

    var query = SelectQuery.of(select, fromClause, whereClause);

    var context = PrintContext.ZERO;
    assertEquals(ageOver20, query.print(context));
  }
}
