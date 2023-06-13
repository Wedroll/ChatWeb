package WedroL.vaadin.chat;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;

@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET_XHR)
@PWA(name = "WedroL.vaadin.chat", shortName = "App")
public class CustomAppShellConfigurator implements AppShellConfigurator {

}