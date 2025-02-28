package api;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Task;
import tasks.Progress;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskManagerTasksTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeAll
    public void startServer() throws IOException {
        taskManager = Managers.getDefault();;
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
    public void testAddTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2",
                Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Неверное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Неверное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "D1", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Task 2", "D2", Progress.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertNotNull(tasks, "Ответ пустой");
        assertEquals(2, tasks.length, "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskById_Success() throws IOException, InterruptedException {
        Task task = new Task("Task 3", "qq", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.addTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI getUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Ожидался статус 404 при запросе удалённой задачи");
    }


    @Test
    public void testDeleteTaskById_NotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404");
    }


    @Test
    public void testUpdateTask_Success() throws IOException, InterruptedException {
        Task task = new Task("Old Name", "Old Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.addTask(task);
        int taskId = task.getId();

        Task updatedTask = new Task("New Name", "New Description", Progress.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(15));
        updatedTask.setId(taskId);
        String updatedTaskJson = gson.toJson(updatedTask);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask);
        assertEquals("New Name", savedTask.getName(), "Имя задачи не обновилось");
        assertEquals("New Description", savedTask.getDescription(), "Описание задачи не обновилось");
    }


    @Test
    public void testUpdateTask_Conflict() throws IOException, InterruptedException {
        Task task1 = new Task("Task 4", "D1", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(20));
        Task task2 = new Task("Task 5", "D2", LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(10));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        int taskId = task1.getId();

        Task updatedTask = new Task("Updated Task", "Updated D", Progress.NEW,
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(60));
        updatedTask.setId(taskId);
        String updatedTaskJson = gson.toJson(updatedTask);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Ожидался статус 406 при конфликте");
    }
}
