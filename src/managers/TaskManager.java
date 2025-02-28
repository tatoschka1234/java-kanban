package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Task> getAllSubtasks();

    List<Task> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Epic getEpicNoHistory(int epicId);

    Subtask getSubtask(int subtaskId);

    List<Task> getSubtasks(Epic epic);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Epic epic, Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(Task task);

    void deleteSubtask(Subtask subtask);

    void deleteEpic(Epic epic);

    void updateEpicProgress(Epic epic);

    List<Task> getHistory();

    int getNextId();

    List<Task> getPrioritizedTasks();

}
