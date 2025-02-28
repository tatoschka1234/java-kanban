import tasks.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    void subtasksShouldBeEqualIfTheirIDsEqual() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1_descr");
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("subtask1", "subtask1_descr");
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "СабТаски не равны");
    }
}
