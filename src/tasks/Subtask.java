package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Progress taskProgress) {
        super(name, description, taskProgress);
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(Subtask other) {
        super(other.getName(), other.getDescription(), other.getProgress());
        this.setId(other.getId());
        this.epicId = other.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public String toString() {
        return "Tasks.Subtask id: " + id +
                ", Name: " + this.name +
                ", Descr: " + this.description +
                ", Tasks.Progress: " + this.taskProgress +
                ", epic id: " + this.getEpicId() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }


}
