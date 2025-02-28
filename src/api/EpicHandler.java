package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class EpicHandler extends BaseTaskHandler<Epic> {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Epic.class);
    }

    @Override
    protected List<Epic> getAllTasks() {
        return taskManager.getAllEpics().stream()
                .filter(task -> task instanceof Epic)
                .map(task -> (Epic) task)
                .toList();
    }

    @Override
    protected Epic getTaskById(int id) {
        return taskManager.getEpic(id);
    }

    @Override
    protected void addTask(Epic epic) {
        taskManager.addEpic(epic);
    }

    @Override
    protected void updateTask(Epic epic) {
        taskManager.updateEpic(epic);
    }

    @Override
    protected void deleteTask(int id) {
        taskManager.deleteEpic(taskManager.getEpic(id));
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("GET".equals(method) && pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                int epicId = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpic(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                List<Task> subtasks = taskManager.getSubtasks(epic);
                sendText(exchange, gson.toJson(subtasks));
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


