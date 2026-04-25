package ims.service;

import ims.model.EvaluationEvent;
import ims.storage.FileStorage;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * EvaluationService - manages evaluation transactions.
 *
 * Uses a Queue to demonstrate a more advanced data structure for processing
 * periodic evaluation events in FIFO order.
 */
public class EvaluationService {

    private static final String FILE = "evaluation_events.txt";

    private final ArrayList<EvaluationEvent> history = new ArrayList<>();
    private final Queue<EvaluationEvent> pendingQueue = new ArrayDeque<>();
    private int nextId = 1;

    public EvaluationService() { load(); }

    public EvaluationEvent queueStatus(String entityType, int entityRefId, String entityName, String message) {
        return enqueue(entityType, entityRefId, entityName,
                EvaluationEvent.EventType.STATUS, EvaluationEvent.ActionType.NONE, message);
    }

    public EvaluationEvent queueReward(String entityType, int entityRefId, String entityName, String message) {
        return enqueue(entityType, entityRefId, entityName,
                EvaluationEvent.EventType.POSITIVE_FEEDBACK, EvaluationEvent.ActionType.REWARD, message);
    }

    public EvaluationEvent queuePenalty(String entityType, int entityRefId, String entityName, String message) {
        return enqueue(entityType, entityRefId, entityName,
                EvaluationEvent.EventType.NEGATIVE_FEEDBACK, EvaluationEvent.ActionType.PENALTY, message);
    }

    private EvaluationEvent enqueue(String entityType, int entityRefId, String entityName,
                                    EvaluationEvent.EventType eventType,
                                    EvaluationEvent.ActionType actionType,
                                    String message) {
        EvaluationEvent event = new EvaluationEvent(
                nextId++, entityType, entityRefId, entityName,
                eventType, actionType, message, LocalDateTime.now().toString()
        );
        pendingQueue.offer(event);
        return event;
    }

    public List<EvaluationEvent> processPending() {
        List<EvaluationEvent> processed = new ArrayList<>();
        while (!pendingQueue.isEmpty()) {
            EvaluationEvent event = pendingQueue.poll();
            history.add(event);
            processed.add(event);
        }
        if (!processed.isEmpty()) save();
        return processed;
    }

    public List<EvaluationEvent> getAll() {
        return new ArrayList<>(history);
    }

    public List<EvaluationEvent> getRecent(int limit) {
        int from = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(from, history.size()));
    }

    public int count() { return history.size(); }
    public int pendingCount() { return pendingQueue.size(); }

    public void save() {
        List<String> lines = history.stream()
                .map(EvaluationEvent::toFileLine)
                .collect(Collectors.toList());
        FileStorage.writeFile(FILE, lines);
    }

    public void load() {
        history.clear();
        nextId = 1;
        for (String line : FileStorage.readFile(FILE)) {
            try {
                EvaluationEvent event = EvaluationEvent.fromFileLine(line);
                history.add(event);
                if (event.getId() >= nextId) nextId = event.getId() + 1;
            } catch (Exception e) {
                System.err.println("[EvaluationService] Skipping bad line: " + e.getMessage());
            }
        }
    }
}
