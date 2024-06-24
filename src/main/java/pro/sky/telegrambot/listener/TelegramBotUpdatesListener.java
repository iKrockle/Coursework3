package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    private final NotificationTaskRepository notificationTaskRepository;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            Long chatId=update.message().chat().id();
            String messageText;
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher;
            logger.info("Processing update: {}", update);
            if (update.message().text().equals("/start")){
                messageText = "Добро пожаловать в бот по курсовой Skypro";
                telegramBot.execute(new SendMessage(chatId, messageText));
            }
            else{
                matcher = pattern.matcher(update.message().text());
                if (matcher.matches()) {
                    String date = matcher.group(1);
                    String notificationMessage = matcher.group(3);

                    messageText = add(new NotificationTask(chatId
                            ,LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                            ,notificationMessage));
                    telegramBot.execute(new SendMessage(chatId, messageText));
                }
                else{
                    messageText = "сообщение не формата 'DD.MM.YYYY HH24:mi notification text'";
                    telegramBot.execute(new SendMessage(chatId, messageText));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public String add(NotificationTask notificationTask){
        List<NotificationTask> notificationList = notificationTaskRepository
                .findByChatIdAndNotificationDateAndNotificationMessage(notificationTask.getChatId()
                                                  ,notificationTask.getNotificationDate()
                                                  ,notificationTask.getNotificationMessage());
        logger.info("Was invoked method for create notification task");
        if (notificationList.isEmpty()) {
            notificationTaskRepository.save(notificationTask);
            return "Напоминание успешно добавлено";
        }else {
            logger.error("Student name:{} age:{} already exist",notificationTask.getChatId()
                        ,notificationTask.getNotificationDate());
            return "Такое напоминание уже существует";
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        List<NotificationTask> notificationList = notificationTaskRepository.getNotificationsForRun();
        for(NotificationTask curTask:notificationList){
            telegramBot.execute(new SendMessage(curTask.getChatId(), curTask.getNotificationMessage()));
        }
    }
}
