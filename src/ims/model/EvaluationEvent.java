package ims.model;

/**
 * EvaluationEvent - stores status/reward/penalty actions created during periodic evaluations.
 * Demonstrates an additional entity that can be processed uniformly via BaseModel.
 */
public class EvaluationEvent extends BaseModel {

    public enum EventType { STATUS, POSITIVE_FEEDBACK, NEGATIVE_FEEDBACK }
    public enum ActionType { NONE, REWARD, PENALTY }

    private String entityType;
    private int entityRefId;
    private String entityName;
    private EventType eventType;
    private ActionType actionType;
    private String message;
    private String eventDate;

    public EvaluationEvent(int id, String entityType, int entityRefId, String entityName,
                           EventType eventType, ActionType actionType,
                           String message, String eventDate) {
        super(id);
        this.entityType = entityType;
        this.entityRefId = entityRefId;
        this.entityName = entityName;
        this.eventType = eventType;
        this.actionType = actionType;
        this.message = message;
        this.eventDate = eventDate;
    }

    public String getEntityType() { return entityType; }
    public int getEntityRefId() { return entityRefId; }
    public String getEntityName() { return entityName; }
    public EventType getEventType() { return eventType; }
    public ActionType getActionType() { return actionType; }
    public String getMessage() { return message; }
    public String getEventDate() { return eventDate; }

    @Override
    public String toFileLine() {
        return getId() + "|" + entityType + "|" + entityRefId + "|" + entityName + "|"
                + eventType.name() + "|" + actionType.name() + "|" + message + "|" + eventDate;
    }

    public static EvaluationEvent fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new EvaluationEvent(
                Integer.parseInt(p[0].trim()),
                p[1].trim(),
                Integer.parseInt(p[2].trim()),
                p[3].trim(),
                EventType.valueOf(p[4].trim()),
                ActionType.valueOf(p[5].trim()),
                p[6].trim(),
                p[7].trim()
        );
    }

    @Override
    public void printDetails() {
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.printf("  │  Event ID        : %-26d│%n", getId());
        System.out.printf("  │  Entity          : %-26s│%n", entityType + " #" + entityRefId);
        System.out.printf("  │  Name            : %-26s│%n", shorten(entityName, 26));
        System.out.printf("  │  Event Type      : %-26s│%n", eventType.name());
        System.out.printf("  │  Action          : %-26s│%n", actionType.name());
        System.out.printf("  │  Date            : %-26s│%n", eventDate);
        System.out.printf("  │  Message         : %-26s│%n", shorten(message, 26));
        System.out.println("  └──────────────────────────────────────────────┘");
    }

    @Override
    public String getSummary() {
        return String.format("  [%3d] %-10s %-18s %-18s %s",
                getId(), actionType.name(), entityType + "#" + entityRefId,
                entityName, message);
    }

    private String shorten(String s, int max) {
        if (s == null || s.isEmpty()) return "-";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}
