package sq2aql.model.aql;

import java.math.BigDecimal;
import sq2aql.PrintContext;

public record RealPrimitive(BigDecimal real) implements Primitive {

  public static RealPrimitive of(BigDecimal real) {
    return new RealPrimitive(real);
  }

  @Override
  public String print(PrintContext printContext) {
    return String.valueOf(real);
  }
}
