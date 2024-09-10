public class Subtask extends Task {
    protected final TaskType taskType = TaskType.SUBTASK;
    protected Epic epic;

    public Subtask(String name, String description, Progress taskProgress) {
        super(name, description, taskProgress);
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    protected void setEpic(Epic epic) {
        this.epic = epic;
        epic.addSubtask(this);
    }

    protected Epic getEpic() {
        return this.epic;
    }

}
