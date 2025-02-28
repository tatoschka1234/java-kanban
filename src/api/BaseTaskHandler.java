package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import exceptions.NotFoundException;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseTaskHandler<T extends Task> extends BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    private final Class<T> taskClass;

    public BaseTaskHandler(TaskManager taskManager, Gson gson, Class<T> taskClass) {
        this.taskManager = taskManager;
        this.gson = gson;
        this.taskClass = taskClass;
    }

    protected abstract List<T> getAllTasks();

    protected abstract T getTaskById(int id);

    protected abstract void addTask(T task);

    protected abstract void updateTask(T task);

    protected abstract void deleteTask(int id);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("GET".equals(method) && pathParts.length == 2) {
                sendText(exchange, gson.toJson(getAllTasks()));

            } else if ("POST".equals(method) && pathParts.length == 2) {
                T task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), taskClass);
                addTask(task);
                sendText(exchange, "Task added successfully");

            } else if ("POST".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (getTaskById(id) == null) {
                    sendNotFound(exchange);
                    return;
                }

                T updatedTask = gson.fromJson(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), taskClass);
                updatedTask.setId(id);
                updateTask(updatedTask);
                sendText(exchange, "Task updated successfully");

            } else if ("GET".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                sendText(exchange, gson.toJson(getTaskById(id)));

            } else if ("DELETE".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                deleteTask(id);
                sendText(exchange, "Task deleted successfully");

            } else {
                exchange.sendResponseHeaders(405, -1);
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

