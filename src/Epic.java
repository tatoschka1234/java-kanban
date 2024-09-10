import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> epicSubtasks = new ArrayList<>();
    protected final TaskType taskType = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description);
    }

    protected void addSubtask(Subtask task) {
        for (int i = 0; i < epicSubtasks.size(); i++) {
            if (epicSubtasks.get(i).getId() == task.getId()) {
                epicSubtasks.set(i, task);
                return;
            }
        }
        epicSubtasks.add(task);
    }


    public ArrayList<Subtask> getSubtasks() {
        return this.epicSubtasks;
    }

    public Progress getEpicProgress() {
        return this.taskProgress;
    }

    protected void updateEpicProgress() {
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask task : this.epicSubtasks) {
            Progress taskProgress = task.getTaskProgress();

            if (taskProgress != Progress.NEW) {
                allNew = false;
            }
            if (taskProgress != Progress.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            this.taskProgress = Progress.NEW;
        } else if (allDone) {
            this.taskProgress = Progress.DONE;
        } else {
            this.taskProgress = Progress.IN_PROGRESS;
        }
    }

    public void removeSubtask(Subtask task) {
        epicSubtasks.remove(task);
    }


}
