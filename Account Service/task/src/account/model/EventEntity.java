package account.model;

import java.time.LocalDateTime;

public class EventEntity {
  private int id;
  private LocalDateTime date;
  private Event action;
  private String subject;
  private String object;
  private String path;

  public EventEntity() {
  }

  public EventEntity(Event action, String subject, String object, String path) {
    this.action = action;
    this.subject = subject;
    this.object = object;
    this.path = path;
    this.date = LocalDateTime.now();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Event getAction() {
    return action;
  }

  public void setAction(Event action) {
    this.action = action;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return "EventEntity{" +
        "id=" + id +
        ", date=" + date +
        ", action=" + action +
        ", subject='" + subject + '\'' +
        ", object='" + object + '\'' +
        ", path='" + path + '\'' +
        '}';
  }
}
