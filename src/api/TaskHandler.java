package api;

import com.google.gson.Gson;
import managers.TaskManager;
import tasks.Task;

import java.util.List;

public class TaskHandler extends BaseTaskHandler<Task> {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson, Task.class);
    }

    @Override
    protected List<Task> getAllTasks() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Task getTaskById(int id) {
        return taskManager.getTask(id);
    }

    @Override
    protected void addTask(Task task) {
        taskManager.addTask(task);
    }

    @Override
    protected void updateTask(Task task) {
        taskManager.updateTask(task);
    }

    @Override
    protected void deleteTask(int id) {
        taskManager.deleteTask(taskManager.getTask(id));
    }
}

