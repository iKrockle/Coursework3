package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long chatId;
    private LocalDateTime notificationDate;
    private String notificationMessage;

    public NotificationTask(){};

    public NotificationTask(Long chat, LocalDateTime notification_date, String notificationMessage) {
        this.chatId = chat;
        this.notificationDate = notification_date;
        this.notificationMessage = notificationMessage;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(notificationDate, that.notificationDate) && Objects.equals(notificationMessage, that.notificationMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, notificationDate);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", chat_id=" + chatId +
                ", notification_date=" + notificationDate +
                ", notification_message='" + notificationMessage + '\'' +
                '}';
    }
}
