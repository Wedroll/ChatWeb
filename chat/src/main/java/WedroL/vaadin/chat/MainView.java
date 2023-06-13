package WedroL.vaadin.chat;


import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;


@Route("")
public class MainView extends VerticalLayout {
    private final Storage storage;
    private Registration registration;

    private Grid<Storage.ChatMessage> grid;
    private VerticalLayout chat;
    private VerticalLayout login;
    private String user = "";

    public MainView(Storage storage) { //Создание экземпляра класс Storage
        this.storage = storage;

        buildLogin();
        buildChat();
    }

    private void buildLogin() { // создание контейнера логинирования

        login = new VerticalLayout() {{
            TextField field = new TextField();
            field.setPlaceholder("Please, introduce yourself");
            add(
                    field,
                    new Button("Login") {{
                        addClickListener(click -> {
                            login.setVisible(false);
                            chat.setVisible(true);
                            user = field.getValue();
                            storage.addRecordJoined(user);
                        });
                        addClickShortcut(Key.ENTER);
                    }}
            );
        }};
        add(login);
    }

    private void buildChat() { // Создание контейнера чата
        chat = new VerticalLayout();
        add(chat);
        chat.setVisible(false);

        grid = new Grid<>(); // grid для отображения сообщений
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
                .setAutoWidth(true);

        TextField field = new TextField();

        chat.add(
                new H3( "Chat"),
                grid,

                new HorizontalLayout() {{
                    add(
                            field,
                            new Button("➡"){{
                                addClickListener(click -> {
                                    storage.addRecord(user, field.getValue());
                                    field.clear();
                                });
                                addClickShortcut(Key.ENTER);
                            }}
                    );
                }}
        );
    }

    public void onMessage(Storage.ChatEvent event) { // вызывается при новом событии ChatEvent, обновление grid, скрол
        if (getUI().isPresent()) {
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.beforeClientResponse(grid, ctx -> grid.scrollToEnd());
            ui.access(() -> grid.getDataProvider().refreshAll());
            ui.getSession().unlock();
        }
    }

    private String renderRow(Storage.ChatMessage message) { // Форматирование текста с помощью MarkDown
        if (message.getName().isEmpty()) {
            return Processor.process(String.format("_User **%s** has just joined the chat!_", message.getMessage()));
        } else {
            String formattedMessage = String.format("**%s**: %s", message.getName(), message.getMessage());
            if (message.getName().equals(user)) {
                formattedMessage = "<span class='current-user'>" + formattedMessage + "</span>";
            }
            return Processor.process(formattedMessage);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}