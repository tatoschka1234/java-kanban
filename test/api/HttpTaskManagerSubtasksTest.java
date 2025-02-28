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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskManagerSubtasksTest {

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
    public void testCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Subtask 1", "Description");
        subtask.setEpicId(epicId);

        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при создании подзадачи");

        List<Task> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Подзадача должна быть добавлена");
        assertEquals(epicId, ((Subtask) subtasks.get(0)).getEpicId(), "Подзадача должна принадлежать правильному эпику");
    }


    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Progress.NEW);
        taskManager.addSubtask(epic, subtask);
        int subtaskId = subtask.getId();

        Subtask updatedSubtask = new Subtask("Updated Subtask", "Updated Description", Progress.IN_PROGRESS);
        updatedSubtask.setId(subtaskId);
        updatedSubtask.setEpicId(epic.getId());

        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при обновлении подзадачи");

        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertEquals("Updated Subtask", savedSubtask.getName(), "Имя подзадачи должно обновиться");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Подзадача должна оставаться привязанной к тому же эпику");
    }



    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description");
        taskManager.addSubtask(epic, subtask);
        int subtaskId = subtask.getId();

        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении подзадачи");

        Subtask receivedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtaskId, receivedSubtask.getId(), "ID подзадачи должен совпадать");
    }


    @Test
    public void testGetNonExistentSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующей подзадачи");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description");
        taskManager.addSubtask(epic, subtask);
        int subtaskId = subtask.getId();

        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при удалении подзадачи");

        URI getUrl = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Ожидался статус 404 при запросе удалённой подзадачи");
    }


    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description");
        Subtask subtask2 = new Subtask("Subtask 2", "Description");
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasks.length, "Должно быть 2 подзадачи");
    }
}
