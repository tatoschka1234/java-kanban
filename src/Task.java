import java.util.Arrays;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected long id;
    protected Progress taskProgress;
    protected final TaskType taskType = TaskType.TASK; // I'm going to use it! somehow

    public Task(String name, String description, Progress taskProgress) {
        this.name = name;
        this.description = description;
        this.taskProgress = taskProgress;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskProgress = Progress.NEW;
    }

    protected long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Type: " + getClass().getName() +
                ", id: " + id +
                ", Name: " + this.name +
                ", Descr: " + this.description +
                ", Progress: " + this.taskProgress;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Progress getTaskProgress() {
        return this.taskProgress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(taskType, task.taskType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskProgress, description);

    }
}
