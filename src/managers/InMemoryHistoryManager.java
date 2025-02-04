package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    class Node {

        Task data;
        Node next;
        Node prev;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());

        Node newNode = new Node(task);
        history.put(task.getId(), newNode);
        addNodeToTheEnd(newNode);
    }

    private void addNodeToTheEnd(Node node) {
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            unlink(node);
        }
    }

    private void unlink(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }
}

