package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public String toString() {
        return "Tasks.Epic id: " + id +
                ", Name: " + this.name +
                ", Descr: " + this.description +
                ", Tasks.Progress: " + this.taskProgress +
                ", Num of subtasks: " + this.getSubtaskIds().size() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public TaskTypes getTaskType() {
        return TaskTypes.EPIC;
    }
}
