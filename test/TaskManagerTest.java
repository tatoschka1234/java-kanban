import exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import managers.TaskManager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @AfterEach
    void afterEach() {
        taskManager.removeAllEpics();
        taskManager.removeAllTasks();
    }

    @Test
    void shouldAddAndRetrieveTask() {
        Task task = new Task("Task1", "Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    void shouldAddAndRetrieveEpic() {
        Epic epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldAddAndRetrieveSubtask() {
        Epic epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask Desc", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.addSubtask(epic, subtask);

        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void shouldNotAllowOverlappingTasks() {
        Task task1 = new Task("Task1", "Descr", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        Task task2 = new Task("Task2", "Descr", Progress.NEW, task1.getStartTime(), Duration.ofMinutes(30));

        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2),
                "Должно выбрасываться исключение при пересечении задач");
    }

    @Test
    void shouldBeNewWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic1", "Epic descr");
        Subtask sub1 = new Subtask("Subtask1", "desc", Progress.NEW);
        Subtask sub2 = new Subtask("Subtask2", "desc", Progress.NEW);
        taskManager.addSubtask(epic, sub1);
        taskManager.addSubtask(epic, sub2);

        assertEquals(Progress.NEW, epic.getProgress());
    }

    @Test
    void shouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic1", "Epic descr");

        Subtask sub1 = new Subtask("Subtask1", "desc", Progress.DONE);
        Subtask sub2 = new Subtask("Subtask2", "desc", Progress.DONE);
        taskManager.addSubtask(epic, sub1);
        taskManager.addSubtask(epic, sub2);

        assertEquals(Progress.DONE, epic.getProgress());
    }

    @Test
    void shouldBeInProgressWhenSubtasksMixedNewAndDone() {
        Epic epic = new Epic("Epic1", "Epic descr");
        Subtask sub1 = new Subtask("Subtask1", "desc", Progress.NEW);
        Subtask sub2 = new Subtask("Subtask2", "desc", Progress.DONE);

        taskManager.addSubtask(epic, sub1);
        taskManager.addSubtask(epic, sub2);

        assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    void shouldBeInProgressWhenAllSubtasksInProgress() {
        Epic epic = new Epic("Epic1", "Epic descr");

        Subtask sub1 = new Subtask("Subtask1", "desc", Progress.IN_PROGRESS);
        Subtask sub2 = new Subtask("Subtask2", "desc", Progress.IN_PROGRESS);
        taskManager.addSubtask(epic, sub1);
        taskManager.addSubtask(epic, sub2);

        assertEquals(Progress.IN_PROGRESS, epic.getProgress());
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);

        Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic1);

        Epic savedEpic = taskManager.getEpic(epic1.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        List<Task> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic1, subtask1Epic1);
        Subtask savedSubtask = taskManager.getSubtask(subtask1Epic1.getId());

        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask1Epic1, savedSubtask, "Сабтаски не совпадают.");

        Epic epic2 = new Epic("Epic2", "Epic descr");
        taskManager.addEpic(epic2);
        Subtask subtask1Epic2 = new Subtask("epic2 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic2, subtask1Epic2);

        List<Task> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask1Epic1, subtasks.get(0), "Сабтаски не совпадают.");

        List<Task> subtasks1 = taskManager.getSubtasks(epic1);
        assertNotNull(subtasks1, "Сабтаски для эпика не возвращаются.");
        assertEquals(1, subtasks1.size(), "Неверное количество сабтасок.");
        assertEquals(subtask1Epic1, subtasks1.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void deleteEpicAndItsSubtasks() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic1, subtask1Epic1);

        Epic epic2 = new Epic("Epic2", "Epic descr");
        taskManager.addEpic(epic2);
        Subtask subtask1Epic2 = new Subtask("epic2 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic2, subtask1Epic2);
        assertEquals(2, taskManager.getAllEpics().size(), "неверное число эпиков");
        assertEquals(2, taskManager.getAllSubtasks().size(), "неверное число сабтасок");

        taskManager.deleteEpic(epic1);
        assertEquals(1, taskManager.getAllEpics().size(), "неверное число эпиков");
        assertEquals(1, taskManager.getAllSubtasks().size(), "неверное число сабтасок");

        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllEpics().size(), "Эпики возвращаются.");
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.deleteTask(task1);
        assertEquals(1, taskManager.getAllTasks().size(), "неверное число тасок");

        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "неверное число тасок");
    }

    @Test
    void deleteSubTasks() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic1, subtask1Epic1);
        Subtask subtask2Epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        taskManager.addSubtask(epic1, subtask2Epic1);
        assertEquals(2, taskManager.getAllSubtasks().size(), "неверное число сабтасок");

        taskManager.deleteSubtask(subtask1Epic1);
        assertEquals(1, taskManager.getAllSubtasks().size(), "неверное число сабтасок");

        taskManager.removeAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(), "неверное число сабтасок");
    }

    @Test
    void taskDoesNotChangedInTaskManager() {
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        taskManager.addTask(task1);

        Task savedTask = taskManager.getTask(task1.getId());

        assertEquals(task1.getId(), savedTask.getId(), "IDs are not rhe same");
        assertEquals(task1.getName(), savedTask.getName(), "Names are not the same");
        assertEquals(task1.getDescription(), savedTask.getDescription(), "Descriptions are not the same");
        assertEquals(task1.getProgress(), savedTask.getProgress(), "Progresses are not the same");

    }

    @Test
    void shouldRemoveSubtaskAndClearEpicRef() {

        Epic epic = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        taskManager.addSubtask(epic, subtask);

        int subtaskId = subtask.getId();
        taskManager.deleteSubtask(subtask);

        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(subtaskId));

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "Эпик не должен содержать старый ID подзадачи");
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicWhenSubtaskRm() {
        Epic epic = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        taskManager.addSubtask(epic, subtask);

        int subtaskId = subtask.getId();
        taskManager.deleteSubtask(subtask);

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "ID подзадачи должен быть удалён из эпика");
    }


    @Test
    void shouldThrowExceptionWhenChangeTaskId() {

        Task task = new Task("Task1", "Task1 descr", Progress.NEW);
        taskManager.addTask(task);

        int oldId = task.getId();
        assertEquals(oldId, task.getId(), "ID задачи не должен изменяться после назначения");
    }


    @Test
    void shouldNotChangeEpicProgressWhenSubtaskProgressChangedDirectly() {

        Epic epic = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        taskManager.addSubtask(epic, subtask);

        subtask.setTaskProgress(Progress.DONE);
        assertNotEquals(Progress.DONE, taskManager.getEpic(epic.getId()).getProgress(),
                "Статус эпика не должен меняться без updateSubtask");
    }

    @Test
    void shouldNotChangeEpicReferenceWhenSubtaskEpicIdChangedDirectly() {
        Epic epic1 = new Epic("Epic1", "Epic descr");
        Epic epic2 = new Epic("Epic2", "Another descr");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask = new Subtask("Subtask1", "Subtask descr", Progress.NEW);
        taskManager.addSubtask(epic1, subtask);

        subtask.setEpicId(epic2.getId());
        assertTrue(epic1.getSubtaskIds().contains(subtask.getId()),
                "Старая связь с эпиком не должна исчезать без удаления из менеджера");
    }

    @Test
    void shouldHaveEpicWhenSubtaskCreated() {
        Epic epic = new Epic("Epic1", "Epic descr");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "descr");
        taskManager.addSubtask(epic, subtask);

        assertNotNull(subtask.getEpicId(), "У подзадачи должен быть связанный эпик");
        assertEquals(epic.getId(), subtask.getEpicId(), "ID эпика у подзадачи должен соответствовать ожидаемому");
    }


}
