import managers.Managers;
import managers.TaskManager;
import tasks.*;

public class Main {
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasks((Epic) epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Tasks.Epic descr");
        manager.addEpic(epic1);

        Subtask subtask1_epic1 = new Subtask("subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1_epic1);
        Subtask subtask2_epic1 = new Subtask("subtask2", "subtask2_descr", Progress.IN_PROGRESS);
        manager.addSubtask(epic1, subtask2_epic1);
        Subtask subtask3_epic1 = new Subtask("subtask3", "subtask3_descr", Progress.NEW);
        manager.addSubtask(epic1, subtask3_epic1);

        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1_epic1.getId());
        manager.getEpic(epic1.getId());
        manager.getTask(task2.getId());

        System.out.println("History:\n " + manager.getHistory());
    }
}
