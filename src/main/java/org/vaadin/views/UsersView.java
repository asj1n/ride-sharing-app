package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;
import org.vaadin.rsa.Manager;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.user.User;

@PageTitle("Users")
@Route("users")
@Menu(order = 1)
public class UsersView extends Composite<VerticalLayout> {
    Manager manager = Manager.getInstance();
    User user; // last registered user

    public UsersView() throws RideSharingAppException {
        H6 h6 = new H6();
        FormLayout registerForm = new FormLayout();
        TextField nicknameField = new TextField();
        TextField nameField = new TextField();
        Button registerButton = new Button();
        Hr hr = new Hr();

        getContent().setWidth("100%");
        getContent().setHeight("min-content");
        h6.setText("REGISTER");
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
            user = registerUser(nicknameField.getValue(), nameField.getValue());
        });

        getContent().add(h6);
        getContent().add(registerForm);
        registerForm.add(nicknameField);
        registerForm.add(nameField);
        registerForm.add(registerButton);
        getContent().add(hr);
    }


    private User registerUser(String nickname, String name) {
        if (!StringUtils.hasText(nickname) || !StringUtils.hasText(name)) {
            return null;
        }

        try {
            return manager.register(nickname, name);
        } catch (RideSharingAppException e) {
            throw new RuntimeException(e);
        }
    }
}
