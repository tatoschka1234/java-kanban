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
public class HttpTaskManagerEpicsTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Desc 1");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидался статус 200 при создании эпика");

        List<Task> epicsFromManager = taskManager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Неверное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.get(0).getName(), "Неверное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "D 1");
        Epic epic2 = new Epic("Epic 2", "D 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении всех эпиков");

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epics, "Ответ пустой");
        assertEquals(2, epics.length, "Неверное количество эпиков");
    }


    @Test
    public void testDeleteEpicById_Success() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "D");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при успешном удалении");

        URI getUrl = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Ожидался статус 404 при запросе удалённого эпика");
    }

    @Test
    public void testDeleteEpicById_NotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/9999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 при удалении несуществующего эпика");
    }


    @Test
    public void testUpdateEpic_Success() throws IOException, InterruptedException {
        Epic epic = new Epic("Old Name", "Old");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        Epic updatedEpic = new Epic("New Name", "New");
        updatedEpic.setId(epicId);
        String updatedEpicJson = gson.toJson(updatedEpic);

        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при обновлении эпика");

        Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic);
        assertEquals("New Name", savedEpic.getName(), "Имя эпика не обновилось");
        assertEquals("New", savedEpic.getDescription(), "Описание эпика не обновилось!!!!");
    }

    @Test
    void testGetSubtasksForEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "d");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Subtask1", "Test subtask", Progress.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Another subtask", Progress.IN_PROGRESS);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);

        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении подзадач");

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertNotNull(subtasks, "Ответ пустой");
        assertEquals(2, subtasks.length, "Неверное количество подзадач");
    }

}
