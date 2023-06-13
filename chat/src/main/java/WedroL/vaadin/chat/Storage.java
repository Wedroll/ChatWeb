package WedroL.vaadin.chat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
    @Getter
    private Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>(); // очередь хранения сообщений чата
    private ComponentEventBus eventBus = new ComponentEventBus(new Div()); // уведомление об состоянии хранилища чата

    @Getter
    @AllArgsConstructor
    public static class ChatMessage {
        private String name;
        private String message;
    }

    public static class ChatEvent extends ComponentEvent<Div> { // уведомление о состоянии чата
        public ChatEvent() {
            super(new Div(), false);
        }
    }

    public void addRecord(String user, String message) { // добавление нового сообщения в очередь
        messages.add(new ChatMessage(user, message));
        eventBus.fireEvent(new ChatEvent());// отправка события пользователям
    }

    public void addRecordJoined(String user) {//Запись о присоединении пользователя
        messages.add(new ChatMessage("", user));
        eventBus.fireEvent(new ChatEvent());
    }

    public Registration attachListener(ComponentEventListener<ChatEvent> messageListener) { //Регистрация пользователя
        return eventBus.addListener(ChatEvent.class, messageListener);
    }

    public int size() { // запрос о кол-ве сообщений
        return messages.size();
    }
}
