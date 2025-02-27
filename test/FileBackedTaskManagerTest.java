import managers.FileBackedTaskManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Progress;
import tasks.Subtask;
import tasks.Task;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static Path testFile;

    @BeforeAll
    static void setUpTestFile() throws IOException {
        testFile = Files.createTempFile("test", ".csv");
    }

    @AfterAll
    static void cleanUp() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(testFile.toFile());
    }

    @Test
    public void testEmptyFileSaveAndLoad() {
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile.toFile());

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile.toFile());

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testSaveMultipleTasks() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile.toFile());

        Task task1 = new Task("Task1", "Description1", Progress.NEW);
        Task task2 = new Task("Task2", "Description2", Progress.DONE);
        Epic epic1 = new Epic("Epic1", "Epic description");
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", Progress.IN_PROGRESS);

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubtask(epic1, subtask1);

        List<String> lines = Files.readAllLines(testFile, StandardCharsets.UTF_8);
        assertEquals(5, lines.size(), "В файле должно быть 5 строк (1 заголовок + 4 задачи)");
    }

    @Test
    public void testLoadMultipleTasks() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic,start,duration\n");
        sb.append("0,TASK,Task1,NEW,Description1,,2025-01-01T10:10:10,10\n");
        sb.append("1,TASK,Task2,DONE,Description1,,2025-01-02T10:10:10,10\n");
        sb.append("2,EPIC,Epic1,IN_PROGRESS,Epic description,,2023-01-12T10:00:00,60\n");
        sb.append("3,SUBTASK,Subtask1,DONE,Subtask description,2,2023-01-12T10:00:00,30\n");
        sb.append("4,SUBTASK,Subtask2,NEW,Subtask2 description,2,2025-01-13T10:00:00,30\n");

        Files.write(testFile, sb.toString().getBytes(StandardCharsets.UTF_8));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile.toFile());
        assertEquals(2, loadedManager.getAllTasks().size(), "Должно быть 2 обычных задачи");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть 1 эпик");
        assertEquals(2, loadedManager.getAllSubtasks().size(), "Должен быть 2 сабтаскa");

        Epic loadedEpic = loadedManager.getEpic(2);
        assertNotNull(loadedEpic, "Эпик с id 2 должен быть загружен");

        assertTrue(loadedEpic.getSubtaskIds().contains(3), "Эпик должен содержать сабтаск с id 3");

        loadedManager.deleteEpic(loadedEpic);
    }

    @Test
    void shouldMatchMemoryAndFileBackedManagers() {
        TaskManager memoryManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description 1", Progress.NEW);
        Task task2 = new Task("Task 2", "Description 2", Progress.IN_PROGRESS);
        memoryManager.addTask(task1);
        memoryManager.addTask(task2);

        Epic epic = new Epic("Epic 1", "Epic description");
        memoryManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask desc", Progress.DONE);
        memoryManager.addSubtask(epic, subtask1);

        memoryManager.getTask(task1.getId());
        memoryManager.getTask(task2.getId());
        memoryManager.getEpic(epic.getId());
        memoryManager.getSubtask(subtask1.getId());

        memoryManager.deleteTask(task1);

        FileBackedTaskManager fileManager = new FileBackedTaskManager(testFile.toFile());
        fileManager.addEpic(epic);
        fileManager.addTask(task1);
        fileManager.addTask(task2);
        fileManager.addSubtask(epic, subtask1);
        fileManager.deleteTask(task1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile.toFile());

        assertEquals(memoryManager.getAllTasks(), loadedManager.getAllTasks(), "Список задач не совпадает");
        assertEquals(memoryManager.getAllEpics(), loadedManager.getAllEpics(), "Список эпиков не совпадает");
        assertEquals(memoryManager.getAllSubtasks(), loadedManager.getAllSubtasks(), "Список подзадач не совпадает");

        assertEquals(memoryManager.getHistory(), loadedManager.getHistory(), "История просмотров не совпадает");

        assertEquals(memoryManager.getNextId(), fileManager.getNextId(), "Следующий ID не совпадает");
        memoryManager.removeAllEpics();
        memoryManager.removeAllTasks();
    }

}
