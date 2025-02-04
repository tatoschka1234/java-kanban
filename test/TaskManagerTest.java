import managers.Managers;
import managers.TaskManager;
import tasks.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private static TaskManager manager;

    @BeforeAll
    static void beforeAll() {
        manager = Managers.getDefault();
    }

    @AfterEach
    void afterEach() {
        manager.removeAllEpics();
        manager.removeAllTasks();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        manager.addTask(task);

        Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic1);

        Epic savedEpic = manager.getEpic(epic1.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        List<Task> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1Epic1);
        Subtask savedSubtask = manager.getSubtask(subtask1Epic1.getId());

        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask1Epic1, savedSubtask, "Сабтаски не совпадают.");

        Epic epic2 = new Epic("Epic2", "Epic descr");
        manager.addEpic(epic2);
        Subtask subtask1Epic2 = new Subtask("epic2 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic2, subtask1Epic2);

        List<Task> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask1Epic1, subtasks.get(0), "Сабтаски не совпадают.");

        List<Task> subtasks1 = manager.getSubtasks(epic1);
        assertNotNull(subtasks1, "Сабтаски для эпика не возвращаются.");
        assertEquals(1, subtasks1.size(), "Неверное количество сабтасок.");
        assertEquals(subtask1Epic1, subtasks1.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void deleteEpicAndItsSubtasks() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1Epic1);

        Epic epic2 = new Epic("Epic2", "Epic descr");
        manager.addEpic(epic2);
        Subtask subtask1Epic2 = new Subtask("epic2 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic2, subtask1Epic2);
        assertEquals(2, manager.getAllEpics().size(), "неверное число эпиков");
        assertEquals(2, manager.getAllSubtasks().size(), "неверное число сабтасок");

        manager.deleteEpic(epic1);
        assertEquals(1, manager.getAllEpics().size(), "неверное число эпиков");
        assertEquals(1, manager.getAllSubtasks().size(), "неверное число сабтасок");

        manager.removeAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "Эпики возвращаются.");
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        manager.addTask(task1);
        manager.addTask(task2);

        manager.deleteTask(task1);
        assertEquals(1, manager.getAllTasks().size(), "неверное число тасок");

        manager.removeAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "неверное число тасок");
    }

    @Test
    void deleteSubTasks() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1Epic1);
        Subtask subtask2Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask2Epic1);
        assertEquals(2, manager.getAllSubtasks().size(), "неверное число сабтасок");

        manager.deleteSubtask(subtask1Epic1);
        assertEquals(1, manager.getAllSubtasks().size(), "неверное число сабтасок");

        manager.removeAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size(), "неверное число сабтасок");
    }


    @Test
    void taskDoesNotChangedInTaskManager() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        manager.addTask(task1);

        Task savedTask = manager.getTask(task1.getId());

        assertEquals(task1.getId(), savedTask.getId(), "IDs are not rhe same");
        assertEquals(task1.getName(), savedTask.getName(), "Names are not the same");
        assertEquals(task1.getDescription(), savedTask.getDescription(), "Descriptions are not the same");
        assertEquals(task1.getProgress(), savedTask.getProgress(), "Progresses are not the same");

    }

    @Test
    void shouldRemoveSubtaskAndClearEpicRef() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        manager.addSubtask(epic, subtask);

        int subtaskId = subtask.getId();
        manager.deleteSubtask(subtask);

        assertNull(manager.getSubtask(subtaskId), "Subtask должен быть удалён");
        assertFalse(epic.getSubtaskIds().contains(subtaskId), "Эпик не должен содержать старый ID подзадачи");
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicWhenSubtaskRm() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        manager.addSubtask(epic, subtask);

        int subtaskId = subtask.getId();
        manager.deleteSubtask(subtask);

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "ID подзадачи должен быть удалён из эпика");
    }

    @Test
    void shouldThrowExceptionWhenChangeTaskId() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Task1", "Task1 descr", Progress.NEW);
        manager.addTask(task);

        int oldId = task.getId();
        assertThrows(UnsupportedOperationException.class, () -> {
            task.setId(999);
        }, "Попытка изменить ID задачи после назначения должна вызывать исключение");
        assertEquals(oldId, task.getId(), "ID задачи не должен изменяться после назначения");
    }


    @Test
    void shouldNotChangeEpicProgressWhenSubtaskProgressChangedDirectly() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        manager.addSubtask(epic, subtask);

        subtask.setTaskProgress(Progress.DONE);
        assertNotEquals(Progress.DONE, manager.getEpic(epic.getId()).getProgress(),
                "Статус эпика не должен меняться без updateSubtask");
    }

    @Test
    void shouldNotChangeEpicReferenceWhenSubtaskEpicIdChangedDirectly() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic("Epic1", "Epic descr");
        Epic epic2 = new Epic("Epic2", "Another descr");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        manager.addSubtask(epic1, subtask);

        subtask.setEpicId(epic2.getId());
        assertTrue(epic1.getSubtaskIds().contains(subtask.getId()),
                "Старая связь с эпиком не должна исчезать без удаления из менеджера");
    }


}
