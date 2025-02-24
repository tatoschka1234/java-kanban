package managers;

import tasks.Epic;
import tasks.Progress;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        List<Task> allTasks = getAllTasks();
        for (Task task : allTasks) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        List<Task> allTasks = getAllSubtasks();
        for (Task task : allTasks) {
            historyManager.remove(task.getId());
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        List<Task> allTasks = getAllEpics();
        for (Task task : allTasks) {
            historyManager.remove(task.getId());
        }
        epics.clear();
        removeAllSubtasks();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
            return new Task(task);
        }
        return null;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    protected Epic getEpicNoHistory(int epicId) {
        return epics.get(epicId);
    }



    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public int getNextId() {
        return id++;
    }

    @Override
    public List<Task> getSubtasks(Epic epic) {
        List<Task> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    @Override
    public void addTask(Task task) {
        if (!tasks.containsKey(id)) {
           task.setId(getNextId());
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (!epics.containsKey(id)) {
            epic.setId(getNextId());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        if (!subtasks.containsKey(id)) {
            subtask.setId(getNextId());
            subtask.setEpicId(epic.getId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicProgress(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicProgress(epics.get(subtask.getEpicId()));
    }

    @Override
    public void deleteTask(Task task) {
        historyManager.remove(task.getId());
        tasks.remove(task.getId());
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateEpicProgress(epic);
        }
    }

    @Override
    public void deleteEpic(Epic epic) {
        epics.remove(epic.getId());
        List<Integer> subtasksToDelete = epic.getSubtaskIds();
        for (Integer subtaskId : subtasksToDelete) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        historyManager.remove(epic.getId());
    }

    @Override
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


    protected void loadTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void loadEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void loadSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

}
