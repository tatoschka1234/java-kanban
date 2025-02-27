package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void deleteTask(Task task) {
        super.deleteTask(task);
        save();
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        super.addSubtask(epic, subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    public void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic,start,duration\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
            }
            for (Task epic : getAllEpics()) {
                writer.write(toString(epic));
            }
            for (Task subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения", e);
        }
    }

    String toString(Task task) {
        String epicField = "";
        if (task instanceof Subtask) {
            epicField = ((Subtask) task).getEpicId().toString();
        }
        String startTimeStr = (task.getStartTime() != null)
                ? task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "";

        String durationStr = (task.getDuration() != null)
                ? String.valueOf(task.getDuration().toMinutes())
                : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s%n",
                task.getId(),
                task.getTaskType(),
                task.getName(),
                task.getProgress(),
                task.getDescription(),
                epicField,
                startTimeStr,
                durationStr
        );
    }

    static Task fromString(String csvLine) {
        String[] fields = csvLine.split(",", -1);

        int id = Integer.parseInt(fields[0].trim());
        TaskTypes type = TaskTypes.valueOf(fields[1].trim());
        String name = fields[2].trim();
        Progress status = Progress.valueOf(fields[3].trim());
        String description = fields[4].trim();

        String startTimeField = fields[6].trim();
        LocalDateTime startTime = startTimeField.isEmpty() ? null :
                LocalDateTime.parse(startTimeField, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String durationField = fields[7].trim();
        Duration duration = durationField.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(durationField));

        Task task;

        switch (type) {
            case TASK:
                task = new Task(name, description, status, startTime, duration);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                task = new Subtask(name, description, status, startTime, duration);
                int epicId = Integer.parseInt(fields[5].trim());
                ((Subtask) task).setEpicId(epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists()) {
            System.out.println("Файл не найден- возвращаем пустой менеджер");
            return new FileBackedTaskManager(file, Managers.getDefaultHistory());
        }
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return manager;
            }
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                switch (task.getTaskType()) {
                    case TASK:
                        manager.loadTask(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        Epic epic = manager.getEpicNoHistory(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                            manager.loadSubtask(subtask);
                            manager.updateEpicProgress(epic);
                            manager.updateEpicStartAndDuration(epic);
                        } else {
                            System.err.println("Не найден эпик для сабтаска с id: " + subtask.getId());
                        }
                        break;
                    case EPIC:
                        manager.loadEpic((Epic) task);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getTaskType());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }
        return manager;
    }

}
