package sq2aql.model.structured_query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import sq2aql.Container;
import sq2aql.model.ValuePathElement;
import sq2aql.model.aql.ArchetypePredicate;
import sq2aql.model.aql.BooleanContainsExpr;
import sq2aql.model.aql.ClassExprOperand;
import sq2aql.model.aql.ContainsExpression;
import sq2aql.model.common.TermCode;
import java.util.List;
import java.util.Objects;

/**
 * Abstract criterion holding the concept, every non-static criterion has.
 */
public abstract class AbstractCriterion implements Criterion {

    final TermCode termCode;
    final List<Modifier> modifiers;

    AbstractCriterion(List<TermCode> termCodes, List<Modifier> modifiers) {
        this.termCode = Objects.requireNonNull(termCodes.get(0));
        this.modifiers = Objects.requireNonNull(modifiers);
    }


    // FIXME: There should be a better way to do this without recursion and without requiring a reversed valuePathElements List and without remove!
    static Container<BooleanContainsExpr> chainContains(List<ValuePathElement> valuePathElements, Optional<BooleanContainsExpr> containsExpr)
        throws Exception {
        if (valuePathElements.isEmpty()) {
            return Container.of(containsExpr.orElseThrow(() -> new Exception("valuePath error") ));
        }
        var element = valuePathElements.get(0);
        var alias = element.alias();
        var archetype = ArchetypePredicate.of(element.archetypeId());
        var classExpr = ClassExprOperand.of(element.openEhrType(), alias, archetype);
        var expr = (containsExpr.isEmpty() ?  ContainsExpression.of(classExpr) : ContainsExpression.of(classExpr, containsExpr.get()));
        valuePathElements.remove(0);
        return chainContains(valuePathElements, Optional.of(expr));
    }

    public static Container<BooleanContainsExpr> containsExpr(List<ValuePathElement> valuePathElements,
        Optional<BooleanContainsExpr> containsExpr)
        throws Exception {
        var valuePathElementsCopy = new ArrayList<>(valuePathElements);
        Collections.reverse(valuePathElementsCopy);
        return chainContains(valuePathElementsCopy, containsExpr);
    }

    public TermCode getTermCode() {
        return termCode;
    }
}
