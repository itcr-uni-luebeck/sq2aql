package sq2aql.model.common;

import static sq2aql.model.common.Comparator.EQUAL;
import static sq2aql.model.common.Comparator.GREATER_EQUAL;
import static sq2aql.model.common.Comparator.GREATER_THAN;
import static sq2aql.model.common.Comparator.LESS_EQUAL;
import static sq2aql.model.common.Comparator.LESS_THAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author Alexander Kiel
 */
class ComparatorTest {

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void fromJson() {
        assertEquals(EQUAL, Comparator.fromJson("eq"));
        assertEquals(LESS_EQUAL, Comparator.fromJson("le"));
        assertEquals(LESS_THAN, Comparator.fromJson("lt"));
        assertEquals(GREATER_EQUAL, Comparator.fromJson("ge"));
        assertEquals(GREATER_THAN, Comparator.fromJson("gt"));
        assertThrows(IllegalArgumentException.class, () -> Comparator.fromJson("foo"));
    }
}
