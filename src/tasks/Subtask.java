package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Progress taskProgress) {
        super(name, description, taskProgress);
    }

    public Subtask(String name, String description, Progress taskProgress, LocalDateTime startTime, Duration duration) {
        super(name, description, taskProgress, startTime, duration);
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
    }

    public Subtask(Subtask other) {
        super(other.getName(), other.getDescription(), other.getProgress());
        this.setId(other.getId());
        this.epicId = other.getEpicId();
        this.duration = other.getDuration();
        this.startTime = other.getStartTime();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public String toString() {
        return String.format("%s. id: %s (epic id %s), name: %s, descr: %s, progress: %s, duration: %s, start: %s%n",
                getTaskType(),
                id,
                getEpicId(),
                name,
                description,
                taskProgress,
                formatDuration(duration),
                startTime);

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

    @Override
    public TaskTypes getTaskType() {
        return TaskTypes.SUBTASK;
    }

}
