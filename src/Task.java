import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Progress taskProgress;

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

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskProgress = Progress.NEW;
    }

    protected int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task id: " + id +
                ", Name: " + this.name +
                ", Descr: " + this.description +
                ", Progress: " + this.taskProgress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Progress getProgress() {
        return this.taskProgress;
    }

    public void setTaskProgress(Progress progess) {
        this.taskProgress = progess;
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
