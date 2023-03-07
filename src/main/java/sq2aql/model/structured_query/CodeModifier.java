package sq2aql.model.structured_query;

import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Kiel, Lorenz Rosenau
 */
public final class CodeModifier extends AbstractModifier {

    private final List<String> codes;

    private CodeModifier(String path, List<String> codes) {
        super(path);
        this.codes = codes;
    }

    public static CodeModifier of(String path, String... codes) {
        return new CodeModifier(path, codes == null ? List.of() : List.of(codes));
    }

//    public Container<BooleanContainsExpr> expression(MappingContext mappingContext, Expression alias) {
//        var propertyExpr = InvocationExpression.of(alias, path);
//        var list = ListSelector.of(codes.stream().map(StringLiteralExpression::of).collect(Collectors.toList()));
//        return Container.of(MembershipExpression.in(propertyExpr, list));
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeModifier that = (CodeModifier) o;
        return path.equals(that.path) && codes.equals(that.codes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, codes);
    }
}
