import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int id = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtask(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public List<Task> getSubtasks(Epic epic) {
        List<Task> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    public void addTask(Task task) {
        if (!tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(task.getId(), task);
            id++;
        }
    }

    public void addEpic(Epic epic) {
        if (!epics.containsKey(id)) {
            epic.setId(id);
            epics.put(epic.getId(), epic);
            id++;
        }
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        if (!subtasks.containsKey(id)) {
            subtask.setId(id);
            subtask.setEpicId(epic.getId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(id);
            id++;
            updateEpicProgress(epic);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicProgress(epics.get(subtask.getEpicId()));
    }

    public void deleteTask(Task task) {
        tasks.remove(task.getId());
    }

    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIds().remove((Integer) subtask.getId());
        }
    }

    public void deleteEpic(Epic epic) {
        epics.remove(epic.getId());
        List<Integer> subtasksToDelete = epic.getSubtaskIds();
        for (Integer subtaskId : subtasksToDelete) {
            subtasks.remove(subtaskId);
        }
    }

    public void updateEpicProgress(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setTaskProgress(Progress.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getProgress() != Progress.NEW) {
                allNew = false;
            }
            if (subtask.getProgress() != Progress.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setTaskProgress(Progress.NEW);
        } else if (allDone) {
            epic.setTaskProgress(Progress.DONE);
        } else {
            epic.setTaskProgress(Progress.IN_PROGRESS);
        }
    }

}
