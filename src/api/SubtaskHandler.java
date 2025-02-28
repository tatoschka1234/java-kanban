package api;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseTaskHandler<Subtask> {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Subtask.class);
    }

    @Override
    protected List<Subtask> getAllTasks() {
        return taskManager.getAllSubtasks().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task)
                .toList();
    }

    @Override
    protected Subtask getTaskById(int id) {
        return taskManager.getSubtask(id);
    }

    @Override
    protected void addTask(Subtask subtask) {
        taskManager.addSubtask(taskManager.getEpicNoHistory(subtask.getEpicId()), subtask);
    }

    @Override
    protected void updateTask(Subtask subtask) {
        taskManager.updateSubtask(subtask);
    }

    @Override
    protected void deleteTask(int id) {
        taskManager.deleteSubtask(taskManager.getSubtask(id));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("POST".equals(method) && pathParts.length == 2) {
                Subtask subtask = gson.fromJson(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Subtask.class);

                Epic epic = taskManager.getEpic(subtask.getEpicId());
                taskManager.addSubtask(epic, subtask);
                sendText(exchange, "Subtask added successfully");
            } else {
                super.handle(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IllegalArgumentException e) {
            areOverlapping(exchange);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sendResponse(exchange, 500, "Internal Server Error:\n" + sw.toString());
        }
    }

}

