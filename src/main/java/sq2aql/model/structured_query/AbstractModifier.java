package sq2aql.model.structured_query;

import java.util.Objects;

/**
 * @author Alexander Kiel
 */
abstract class AbstractModifier implements Modifier {

    final String path;

    AbstractModifier(String path) {
        this.path = Objects.requireNonNull(path);
    }
}
