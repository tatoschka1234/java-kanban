import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import tasks.*;

import java.util.List;

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
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task1", "task1_descr", Progress.DONE);
        Task task2 = new Task("Task2", "task2_descr");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Tasks.Epic descr");
        manager.addEpic(epic1);

        Subtask subtask1Epic1 = new Subtask("subtask1", "subtask1_descr", Progress.DONE);
        manager.addSubtask(epic1, subtask1Epic1);
        Subtask subtask2Epic1 = new Subtask("subtask2", "subtask2_descr", Progress.IN_PROGRESS);
        manager.addSubtask(epic1, subtask2Epic1);
        Subtask subtask3Epic1 = new Subtask("subtask3", "subtask3_descr", Progress.NEW);
        manager.addSubtask(epic1, subtask3Epic1);

        manager.getSubtask(subtask3Epic1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1Epic1.getId());
        manager.getEpic(epic1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task2.getId());
        System.out.println("History:\n " + manager.getHistory());
        List<Task> history = manager.getHistory();

        List<Task> history2 = historyManager.getHistory();

        manager.deleteSubtask(subtask3Epic1);
        System.out.println("History:\n " + manager.getHistory());
        manager.deleteEpic(epic1);
        System.out.println("History:\n " + manager.getHistory());
        manager.deleteTask(task2);
        System.out.println("History:\n " + manager.getHistory());
    }
}
