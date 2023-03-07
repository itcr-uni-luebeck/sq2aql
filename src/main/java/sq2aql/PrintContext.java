package sq2aql;

/**
 * @author Alexander Kiel
 */
public record PrintContext(int indent, int precedence) {

    public static final PrintContext ZERO = new PrintContext(0, 0);

    public String getIndent() {
        return " ".repeat(indent);
    }

    public String parenthesize(int precedence, String s) {
        return precedence != this.precedence ? "(%s)".formatted(s) : s;
    }

    public PrintContext increase() {
        return new PrintContext(indent + 2, precedence);
    }

    public PrintContext withPrecedence(int precedence) {
        return new PrintContext(indent, precedence);
    }

    public PrintContext resetPrecedence() {
        return new PrintContext(indent, 0);
    }
}
