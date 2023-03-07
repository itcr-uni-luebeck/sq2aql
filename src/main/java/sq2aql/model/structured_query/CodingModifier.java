package sq2aql.model.structured_query;


import sq2aql.Container;
import sq2aql.model.MappingContext;
import sq2aql.model.common.TermCode;
import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Kiel, Lorenz Rosenau
 */
public final class CodingModifier extends AbstractModifier {

    private final List<TermCode> concepts;

    private CodingModifier(String path, List<TermCode> concepts) {
        super(path);
        this.concepts = concepts;
    }

    public static CodingModifier of(String path, TermCode... concepts) {
        return new CodingModifier(path, List.of(concepts));
    }

//    public Container<BooleanContainsExpr> expression(MappingContext mappingContext, Expression alias) {
//        var codingExpr = InvocationExpression.of(InvocationExpression.of(alias, path), "coding");
//        return concepts.stream()
//                .map(concept -> codeSelector(mappingContext, concept).map(terminology ->
//                        (BooleanContainsExpr) MembershipExpression.contains(codingExpr, terminology)))
//                .reduce(Container.empty(), Container.OR);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodingModifier that = (CodingModifier) o;
        return path.equals(that.path) && concepts.equals(that.concepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, concepts);
    }
}
