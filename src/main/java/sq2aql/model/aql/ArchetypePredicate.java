package sq2aql.model.aql;

import java.util.Objects;
import sq2aql.PrintContext;

public record ArchetypePredicate(String archetype_hrid) implements PathPredicate {

  public static ArchetypePredicate of(String archetype_hrid) {
    return new ArchetypePredicate(archetype_hrid);
  }

  @Override
  public String print(PrintContext printContext) {
    return "[" + archetype_hrid + "]";
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchetypePredicate that = (ArchetypePredicate) o;
    return archetype_hrid.equals(that.archetype_hrid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(archetype_hrid);
  }
}
