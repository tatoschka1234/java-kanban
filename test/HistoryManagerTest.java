import managers.HistoryManager;
import managers.Managers;
import tasks.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private static HistoryManager historyManager;

    @BeforeAll
    static void beforeAll() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addToHistory() {
        historyManager.add(new Task("Task2", "task2_descr"));
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Неправильный размер истории");
    }

    @Test
    void addToHistory11els() {
        for (int i=0; i<11; i++) {
            historyManager.add(new Task("Task" + i, "descr"));

        }
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");

        assertEquals(10, history.size(), "Неправильный размер истории");
        assertEquals("Task1", history.get(0).getName(), "Неправильное имя таски");
    }
}
