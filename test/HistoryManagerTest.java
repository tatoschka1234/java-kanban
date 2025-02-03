import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import tasks.Epic;
import tasks.Progress;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static HistoryManager historyManager;
    private static TaskManager manager;

    @BeforeAll
    static void beforeAll() {
        historyManager = Managers.getDefaultHistory();
        manager = Managers.getDefault();
    }

    @AfterEach
    void afterEach() {
        manager.removeAllEpics();
        manager.removeAllTasks();
    }
    @Test
    void addToHistory() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTask(task2.getId());
        List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "Неправильный размер истории");
        List<Task> history2 = historyManager.getHistory();
        assertEquals(1, history2.size(), "Неправильный размер истории");
    }

    @Test
    void addToHistory11els() {
        Task task;
        for (int i=0; i<11; i++) {
            task = new Task("Task" + i, "descr");
            manager.addTask(task);
            manager.getTask(task.getId());
        }
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals("Task0", history.get(0).getName(), "Неправильное имя таски");
    }

    @Test
    void addToHistoryTwice() {
        Task task  = new Task("Task", "descr");
        manager.addTask(task);
        manager.getTask(task.getId());
        manager.getTask(task.getId());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Неправильный размер истории");
    }

    @Test
    void addToHistoryOder() {
        Task task  = new Task("Task1", "descr");
        Task task2  = new Task("Task2", "descr");
        manager.addTask(task);
        manager.addTask(task2);
        manager.getTask(task.getId());
        manager.getTask(task2.getId());
        manager.getTask(task.getId());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Неправильный размер истории");
        assertEquals("Task1", history.get(history.size()-1).getName(), "Неправильное имя таски");
    }

    @Test
    void rmHistoryIfEpicRemoved() {
        Epic epic1 = new Epic("Epic1", "Tasks.Epic descr");
        manager.addEpic(epic1);
        Subtask subtask1_epic1 = new Subtask("subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1_epic1);
        Subtask subtask2_epic1 = new Subtask("subtask2", "subtask2_descr", Progress.IN_PROGRESS);
        manager.addSubtask(epic1, subtask2_epic1);

        manager.getSubtask(subtask2_epic1.getId());
        manager.getSubtask(subtask1_epic1.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История пустая");

        manager.deleteEpic(epic1);
        List<Task> history1 = manager.getHistory();
        assertEquals(0, history1.size(), "История не пустая");
    }

    @Test
    void rmHistoryIfSubTaskRemoved() {
        Epic epic1 = new Epic("Epic1", "Tasks.Epic descr");
        manager.addEpic(epic1);
        Subtask subtask1_epic1 = new Subtask("subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1_epic1);
        Subtask subtask2_epic1 = new Subtask("subtask2", "subtask2_descr", Progress.IN_PROGRESS);
        manager.addSubtask(epic1, subtask2_epic1);

        manager.getSubtask(subtask2_epic1.getId());
        manager.getSubtask(subtask1_epic1.getId());

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size(), "История пустая");

        manager.deleteSubtask(subtask1_epic1);
        List<Task> history1 = manager.getHistory();
        assertEquals(1, history1.size(), "Неверный рвзмер истории");
    }

    @Test
    void rmHistoryIfTaskRemoved() {
        Task task  = new Task("Task1", "descr");
        Task task2  = new Task("Task2", "descr");
        manager.addTask(task);
        manager.addTask(task2);
        manager.getTask(task.getId());
        manager.getTask(task2.getId());
        manager.getTask(task.getId());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "Неправильный размер истории");

        manager.deleteTask(task2);
        List<Task> history1 = manager.getHistory();
        assertEquals(1, history1.size(), "Неверный рвзмер истории");
    }
}
