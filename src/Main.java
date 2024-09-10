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

        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("Epic1 progress: " + epic1.getEpicProgress());
        System.out.println("epic1 subtasks: " + epic1.getSubtasks());

        System.out.println("Epic2");
        System.out.println("Epic2 progress: " + epic2.getEpicProgress());
        System.out.println("epic2 subtasks: " + epic2.getSubtasks());

        // subtask upd
        Subtask subtaskUpd = new Subtask("epic1 subtask2 new", "new descr", Progress.DONE);
        subtaskUpd.setId(subtask2_epic1.getId());
        subtaskUpd.setEpic(subtask2_epic1.getEpic());
        manager.updateSubtask(subtaskUpd);

        System.out.println("\nAfter upd:");
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("Epic1 progress: " + epic1.getEpicProgress());
        System.out.println("epic1 subtasks: " + epic1.getSubtasks());

        System.out.println("Epic2 progress: " + epic2.getEpicProgress());
        System.out.println("epic2 subtasks: " + epic2.getSubtasks());

        System.out.println("\nAll tasks: " + manager.getAllTasks());
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("All epics: " + manager.getAllEpics());

        System.out.println("\nGet by id");
        System.out.println("Get task: " + manager.getTask(1));
        System.out.println("Get epic: " + manager.getEpic(1));
        System.out.println("Get subtask: " + manager.getSubtask(1));

        System.out.println("\nDelete by id");
        manager.deleteTask(task2);
        manager.deleteEpic(epic2);
        manager.deleteSubtask(subtask2_epic1);
        System.out.println("All tasks: " + manager.getAllTasks());
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        System.out.println("All epics: " + manager.getAllEpics());
        System.out.println("Epic1 progress: " + epic1.getEpicProgress());
        System.out.println("epic1 subtasks: " + epic1.getSubtasks());
        System.out.println("Epic2 progress: " + epic2.getEpicProgress());
        System.out.println("epic2 subtasks: " + epic2.getSubtasks());

        System.out.println("\nDelete All");
        manager.rmAllTasks();
        System.out.println("All tasks: " + manager.getAllTasks());
        manager.rmAllSubtasks();
        System.out.println("All subtasks: " + manager.getAllSubtasks());
        manager.rmAllEpics();
        System.out.println("All epics: " + manager.getAllEpics());
    }
}
