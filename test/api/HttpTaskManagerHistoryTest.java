package api;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Progress;
import tasks.Subtask;
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
public class HttpTaskManagerHistoryTest {

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
    public void testHistoryIsInitiallyEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении истории");
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(0, history.length, "История должна быть пустой");
    }


    @Test
    public void testHistoryRecordsViewedTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "D", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);

        Epic epic = new Epic("Epic", "d");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "S", Progress.NEW);
        taskManager.addSubtask(epic, subtask);

        sendGetRequest("http://localhost:8080/tasks/" + task.getId());
        sendGetRequest("http://localhost:8080/epics/" + epic.getId());
        sendGetRequest("http://localhost:8080/subtasks/" + subtask.getId());

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(3, history.length, "История должна содержать 3 задачи");
    }


    @Test
    public void testHistoryClearsWhenTasksAreDeleted() throws IOException, InterruptedException {
        Task task = new Task("Task", "D", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);

        Epic epic = new Epic("Epic", "E");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "S", Progress.NEW);
        taskManager.addSubtask(epic, subtask);

        sendGetRequest("http://localhost:8080/tasks/" + task.getId());
        sendGetRequest("http://localhost:8080/epics/" + epic.getId());
        sendGetRequest("http://localhost:8080/subtasks/" + subtask.getId());

        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(0, history.length, "История должна быть пустой после удаления всех задач");
    }


    @Test
    public void testHistoryDoesNotContainDuplicates() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description", Progress.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);

        sendGetRequest("http://localhost:8080/tasks/" + task.getId());
        sendGetRequest("http://localhost:8080/tasks/" + task.getId());

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.length, "История не должна содержать дубликаты");
    }

    private void sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
