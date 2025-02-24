import managers.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tasks.Epic;
import tasks.Progress;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static File testFile;

    @BeforeAll
    static void setUp() throws IOException {
        testFile = File.createTempFile("tmp", ".csv");
    }

    @AfterEach
    void cleanUp() {
        testFile.delete();
    }

    @Test
    public void testEmptyFileSaveAndLoad() {
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());

        manager.save();


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testSaveMultipleTasks() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);

        Task task1 = new Task("Task1", "Description1", Progress.NEW);
        Task task2 = new Task("Task2", "Description2", Progress.DONE);
        Epic epic1 = new Epic("Epic1", "Epic description");
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", Progress.IN_PROGRESS);

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubtask(epic1, subtask1);

        List<String> lines = Files.readAllLines(testFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(5, lines.size(), "В файле должно быть 5 строк (1 заголовок + 4 задачи)");
    }

    @Test
    public void testLoadMultipleTasks() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic\n");
        sb.append("0,TASK,Task1,NEW,Description1,\n");
        sb.append("1,TASK,Task2,DONE,Description2,\n");
        sb.append("2,EPIC,Epic1,NEW,Epic description,\n");
        sb.append("3,SUBTASK,Subtask1,IN_PROGRESS,Subtask description,2\n");

        Files.write(testFile.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(2, loadedManager.getAllTasks().size(), "Должно быть 2 обычных задачи");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть 1 эпик");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Должен быть 1 сабтаск");

        Epic loadedEpic = loadedManager.getEpic(2);
        assertNotNull(loadedEpic, "Эпик с id 2 должен быть загружен");

        assertTrue(loadedEpic.getSubtaskIds().contains(3), "Эпик должен содержать сабтаск с id 3");

        loadedManager.deleteEpic(loadedEpic);
    }
}
