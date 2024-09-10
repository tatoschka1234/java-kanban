import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    protected long task_id = 0;
    protected long epic_id = 0;
    protected long subtask_id = 0;
    protected HashMap<Long, Task> tasks = new HashMap<>();
    protected HashMap<Long, Subtask> subtasks = new HashMap<>();
    protected HashMap<Long, Epic> epics = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void rmAllTasks() {
        tasks.clear();
    }

    public void rmAllSubtasks() {
        subtasks.clear();
    }

    public void rmAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTask(long taskId) {
        return this.tasks.get(taskId);
    }

    public Epic getEpic(long epicId) {
        return this.epics.get(epicId);
    }

    public Subtask getSubtask(long subtaskId) {
        return this.subtasks.get(subtaskId);
    }

    public void addTask(Task task) {
        if (!tasks.containsKey(task_id)) {
            tasks.put(task_id, task);
            task.setId(task_id++);
        }
    }

    public void addEpic(Epic epic) {
        epics.put(epic_id, epic);
        epic.setId(epic_id++);
    }

    public void addSubtask(Epic epic, Subtask task) {
        task.setId(subtask_id);
        task.setEpic(epic);
        subtasks.put(subtask_id++, task);
        task.epic.updateEpicProgress();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic task) {
        epics.put(task.getId(), task);
    }

    public void updateSubtask(Subtask task) {
        subtasks.put(task.getId(), task);
        task.epic.updateEpicProgress();
    }

    public void deleteTask(Task task) {
        tasks.remove(task.getId());
    }

    public void deleteSubtask(Subtask task) {
        subtasks.remove(task.getId());
        task.getEpic().removeSubtask(task);
    }

    public void deleteEpic(Epic epic) {
        epics.remove(epic.getId());
        ArrayList<Subtask> subtasksToDelete = epic.getSubtasks();
        for (Subtask subtask : subtasksToDelete) {
            subtasks.remove(subtask.getId());
        }
    }

}
