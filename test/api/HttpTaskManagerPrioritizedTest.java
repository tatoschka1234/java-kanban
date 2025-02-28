package api;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Progress;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskManagerPrioritizedTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeAll
    public void startServer() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        gson = taskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterAll
    public void stopServer() {
        taskServer.stop();
    }

    @AfterEach
    public void clearData() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
    }


    @Test
    public void testPrioritizedTasksInitiallyEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении задач");
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized, "Список  задач не должен быть null");
        assertEquals(0, prioritized.length, "список должен быть пустым");
    }


    @Test
    public void testPrioritizedTasksOrderedByStartTime() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Progress.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description", Progress.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task3 = new Task("Task 3", "Description", Progress.NEW, LocalDateTime.now().plusHours(3), Duration.ofMinutes(30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);

        assertNotNull(prioritized);
        assertEquals(3, prioritized.length, "список должен содержать 3 задачи");
        assertEquals("Task 2", prioritized[0].getName(), "Task 2 должна быть самой ранней");
        assertEquals("Task 1", prioritized[1].getName(), "Task 1 должна быть средней");
        assertEquals("Task 3", prioritized[2].getName(), "Task 3 должна быть самой поздней");
    }


    @Test
    public void testPrioritizedTasksRemoveTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);
        int taskId = task.getId();

        URI deleteUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(0, prioritized.length, "После удаления задач список должен быть пустым");
    }


    @Test
    public void testPrioritizedTasksClearOnDeleteAll() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description", Progress.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.removeAllTasks();

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(0, prioritized.length, "После удаления всех задач список должен быть пустым");
    }

    @Test
    public void testTasksWithoutStartTimeGoLast() throws IOException, InterruptedException {
        Task taskWithTime = new Task("Timed Task", "Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task taskWithoutTime = new Task("No Time Task", "Description", Progress.NEW, null, Duration.ofMinutes(30));

        taskManager.addTask(taskWithTime);
        taskManager.addTask(taskWithoutTime);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(2, prioritized.length, "список должен содержать 2 задачи");
        assertEquals("Timed Task", prioritized[0].getName(), "Задача с временем должна идти первой");
        assertEquals("No Time Task", prioritized[1].getName(), "Задача без времени должна идти последней");
    }
}
