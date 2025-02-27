package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Integer id;
    protected Progress taskProgress;
    protected Duration duration;
    protected LocalDateTime startTime;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Task(String name, String description, Progress taskProgress) {
        this.name = name;
        this.description = description;
        this.taskProgress = taskProgress;
    }

    public Task(String name, String description, Progress taskProgress, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.taskProgress = taskProgress;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskProgress = Progress.NEW;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.taskProgress = Progress.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Task copyTask) {
        this.id = copyTask.id;
        this.name = copyTask.getName();
        this.description = copyTask.getDescription();
        this.taskProgress = copyTask.getProgress();
        this.duration = copyTask.getDuration();
        this.startTime = copyTask.getStartTime();
    }


    public int getId() {
        if (id == null) {
            return -1;
        }
        return id;
    }

    public TaskTypes getTaskType() {
        return TaskTypes.TASK;
    }

    public String formatDuration(Duration duration) {
        if (duration == null) {
            return "0 мин";
        }
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        return String.format("%s%s%s",
                days > 0 ? days + " д " : "",
                hours > 0 ? hours + " ч " : "",
                minutes > 0 ? minutes + " мин" : "").trim();
    }


    @Override
    public String toString() {
        return String.format("%s. id: %s, name: %s, descr: %s, progress: %s, duration: %s, start: %s%n",
                getTaskType(),
                id,
                name,
                description,
                taskProgress,
                formatDuration(duration),
                startTime);
    }

    public void setId(int id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public Progress getProgress() {
        return this.taskProgress;
    }

    public void setTaskProgress(Progress progess) {
        this.taskProgress = progess;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null || getDuration() == null) {
            return null;
        }
        return getStartTime().plus(getDuration());
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskProgress, description);

    }
}
