import tasks.Progress;
import tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void tasksShouldBeEqualIfTheirIDsEqual() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        task1.setId(1);
        Task task2 = new Task("Task1", "task1_descr", Progress.DONE);
        task2.setId(1);
        assertEquals(task1, task2, "Таски не равны");
    }

}
