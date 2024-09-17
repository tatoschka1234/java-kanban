public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        manager.addTask(task1);
        manager.addTask(task2);
        System.out.println("All tasks1: " + manager.getAllTasks());

        // task upd
        Task taskUpd = new Task("Task2 new", "new", Progress.IN_PROGRESS);
        taskUpd.setId(task2.getId());
        manager.updateTask(taskUpd);
        System.out.println("All tasks2: " + manager.getAllTasks());

        // Epic
        Epic epic1 = new Epic("Epic1", "Epic descr");
        manager.addEpic(epic1);
        System.out.println("All epics1: " + manager.getAllEpics());

        Subtask subtask1_epic1 = new Subtask("epic1 subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1_epic1);
        Subtask subtask2_epic1 = new Subtask("epic1 subtask2", "subtask2_descr", Progress.IN_PROGRESS);
        manager.addSubtask(epic1, subtask2_epic1);

        Epic epic2 = new Epic("Epic2", "epic2_desc");
        manager.addEpic(epic2);
        Subtask subtask1_epic2 = new Subtask("epic2 subtask1", "ddd");
        manager.addSubtask(epic2, subtask1_epic2);

        System.out.println("All epics2: " + manager.getAllEpics());
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("epic1 subtasks: " + manager.getSubtasks(epic1));

        System.out.println("Epic2");
        System.out.println("Epic2 progress: " + epic2.getProgress());
        System.out.println("epic2 subtasks: " + manager.getSubtasks(epic2));

        // subtask upd
        Subtask subtaskUpd = new Subtask("epic1 subtask2 new", "new descr", Progress.DONE);
        subtaskUpd.setId(subtask2_epic1.getId());
        subtaskUpd.setEpicId(subtask2_epic1.getEpicId());
        manager.updateSubtask(subtaskUpd);

        System.out.println("\nAfter upd:");
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("Epic1 progress: " + epic1.getProgress());
        System.out.println("epic1 subtasks: " + manager.getSubtasks(epic1));
        System.out.println("Epic2 progress: " + epic2.getProgress());
        System.out.println("epic2 subtasks: " + manager.getSubtasks(epic2));

        System.out.println("\nAll tasks: " + manager.getAllTasks());
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("All epics: " + manager.getAllEpics());

        System.out.println("\nGet by id");
        System.out.println("Get task: " + manager.getTask(task1.getId()));
        System.out.println("Get epic: " + manager.getEpic(epic1.getId()));
        System.out.println("Get subtask: " + manager.getSubtask(subtask1_epic1.getId()));

        System.out.println("\nDelete by id");
        manager.deleteTask(task2);
        manager.deleteEpic(epic2);
        manager.deleteSubtask(subtask2_epic1);
        System.out.println("All tasks: " + manager.getAllTasks());
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("All epics: " + manager.getAllEpics());
        System.out.println("Epic1 progress: " + epic1.getProgress());
        System.out.println("epic1 subtasks: " + manager.getSubtasks(epic1));
        System.out.println("Epic2 progress: " + epic2.getProgress());
        System.out.println("epic2 subtasks: " + manager.getSubtasks(epic2));

        System.out.println("\nDelete All");
        manager.removeAllTasks();
        System.out.println("All tasks: " + manager.getAllTasks());
        manager.removeAllSubtasks();
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        manager.removeAllEpics();
        System.out.println("All epics: " + manager.getAllEpics());
    }
}
