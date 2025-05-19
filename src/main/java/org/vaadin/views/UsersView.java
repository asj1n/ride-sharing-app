package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.util.StringUtils;
import org.vaadin.rsa.Manager;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.user.User;

import java.util.List;

@PageTitle("Users")
@Route("users")
@Menu(order = 1)
public class UsersView extends Composite<VerticalLayout> {
    Manager manager = Manager.getInstance();
    List<User> users = manager.getUsers();
    ListDataProvider<User> userDataProvider = new ListDataProvider<>(users);

    public UsersView() throws RideSharingAppException {
        H6 h6 = new H6();
        FormLayout registerForm = new FormLayout();
        TextField nicknameField = new TextField();
        TextField nameField = new TextField();
        Button registerButton = new Button();
        Hr hr = new Hr();

        getContent().setWidth("100%");
        getContent().setHeight("min-content");
        h6.setText("REGISTER USER");
        h6.setWidth("max-content");
        registerForm.setWidth("100%");
        registerForm.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("250px", 2),
                new ResponsiveStep("500px", 3));
        nicknameField.setLabel("Nickname");
        nicknameField.setWidth("min-content");
        nameField.setLabel("Name");
        nameField.setWidth("min-content");
        registerButton.setText("Register User");
        registerButton.setWidth("min-content");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        registerButton.addClickListener(clickEvent -> {
            registerUser(nicknameField.getValue(), nameField.getValue());
        });

        getContent().add(h6);
        getContent().add(registerForm);
        registerForm.add(nicknameField);
        registerForm.add(nameField);
        registerForm.add(registerButton);
        getContent().add(hr);

        H6 usersGridHeader = new H6();
        Grid<User> grid = new Grid<>(User.class, false);
        grid.addColumn(User::getNick).setHeader("Nickname");
        grid.addColumn(User::getName).setHeader("Name");
        grid.addColumn(User::getPreferredMatch).setHeader("Preferred Match");
        grid.getStyle().set("max-height", "300px");
        grid.getStyle().set("overflow", "auto");
        grid.setEmptyStateText("No users found");
        grid.addClassName("users-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.getElement().getStyle().set("--vaadin-grid-header-font-weight", "bold");
        grid.getElement().getStyle().set("--vaadin-grid-header-color", "var(--lumo-primary-color)");
        grid.setAllRowsVisible(true);
        grid.setDataProvider(userDataProvider);

        getContent().add(usersGridHeader);
        getContent().add(grid);
    }


    private User registerUser(String nickname, String name) {
        if (!StringUtils.hasText(nickname) || !StringUtils.hasText(name)) {
            return null;
        }

        try {
            User user = manager.register(nickname, name);
            if (user != null) {
                users.add(user);
                userDataProvider.refreshAll();
                sendSuccessNotification("Successfully registered user " + nickname);
            } else {
                sendFailureNotification("Invalid Nickname or User is already registered");
            }

            return user;
        } catch (RideSharingAppException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSuccessNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
        getContent().add(notification);
    }

    private void sendFailureNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);
        getContent().add(notification);
    }
}
