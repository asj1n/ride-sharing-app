package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import org.springframework.util.StringUtils;
import org.vaadin.rsa.Manager;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.match.PreferredMatch;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.Car;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.UserStars;

import java.util.List;

import static com.vaadin.flow.component.notification.NotificationVariant.*;

@PageTitle("Users")
@Route("users")
@Menu(order = 1)
public class UsersView extends Composite<VerticalLayout> {
    Manager manager = Manager.getInstance();
    List<User> users = manager.getUsers();
    ListDataProvider<User> userDataProvider = new ListDataProvider<>(users);
    User selectedUser;

    public UsersView() throws RideSharingAppException {
        getContent().setWidth("100%");
        createRegisterUserForm();
        getContent().add(new Hr());
        createUsersGrid();
        getContent().add(new Hr());
        createAddCarForm();
        getContent().add(new Hr());
        createDeleteCarForm();
        getContent().add(new Hr());
        createUpdatePreferredMatchForm();
        getContent().add(new Hr());
        createAddUserStarsForm();
    }

    private void createRegisterUserForm() {
        FormLayout registerForm = new FormLayout();
        registerForm.setResponsiveSteps(new ResponsiveStep("0", 3));

        TextField nicknameField = new TextField("Nickname");
        nicknameField.setRequiredIndicatorVisible(true);

        TextField nameField = new TextField("Name");
        nameField.setRequiredIndicatorVisible(true);

        Button registerButton = new Button("REGISTER USER");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickListener(clickEvent -> {
            registerUser(nicknameField.getValue(), nameField.getValue());
        });

        registerForm.add(nicknameField);
        registerForm.add(nameField);
        registerForm.add(registerButton);

        getContent().add(new H6("REGISTER A USER"));
        getContent().add(registerForm);
    }

    private void createUsersGrid() {
        Grid<User> userGrid = new Grid<>(User.class, false);

        userGrid.addColumn(User::getNick).setHeader("Nickname");
        userGrid.addColumn(User::getName).setHeader("Name");
        userGrid.addColumn(user -> user.getCars().size()).setHeader("Cars");
        userGrid.addColumn(User::getPreferredMatch).setHeader("Preferred Match");
        userGrid.addColumn(user -> user.getAverage(RideRole.DRIVER)).setHeader("Driver Average");
        userGrid.addColumn(user -> user.getAverage(RideRole.PASSENGER)).setHeader("Passenger Average");
        userGrid.setEmptyStateText("No users found");
        userGrid.addClassName("users-grid");
        userGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        userGrid.setAllRowsVisible(true);
        userGrid.setDataProvider(userDataProvider);

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedUser = event.getValue();
        });

        getContent().add(new H6("REGISTERED USERS"));
        getContent().add(userGrid);
    }

    private void createAddCarForm() {
        TextField plateField = new TextField("Plate");
        plateField.setRequiredIndicatorVisible(true);

        TextField makeField = new TextField("Make");
        makeField.setRequiredIndicatorVisible(true);

        TextField modelField = new TextField("Model");
        modelField.setRequiredIndicatorVisible(true);

        ComboBox<String> colorCombo = new ComboBox<>("Color");
        colorCombo.setItems("Red", "Green", "Blue", "Black", "White", "Silver");
        colorCombo.setRequiredIndicatorVisible(true);

        Button addCarButton = new Button("ADD CAR");
        addCarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_WARNING);
        addCarButton.addClickListener(clickEvent -> {
            addUserCar(plateField.getValue(), makeField.getValue(), modelField.getValue(), colorCombo.getValue());
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 5));

        formLayout.add(plateField, makeField, modelField, colorCombo, addCarButton);

        getContent().add(new H6("ADD CAR FOR A SELECTED USER"));
        getContent().add(formLayout);
    }

    private void createDeleteCarForm() {
        TextField plateField = new TextField("Plate");
        plateField.setRequiredIndicatorVisible(true);

        Button deleteCarButton = new Button("DELETE CAR");
        deleteCarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteCarButton.addClickListener(clickEvent -> {
            deleteUserCar(plateField.getValue());
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        formLayout.add(plateField, deleteCarButton);

        getContent().add(new H6("DELETE CAR FOR A SELECTED USER"));
        getContent().add(formLayout);
    }

    private void createUpdatePreferredMatchForm() {
        ComboBox<String> preferredMatchCombo = new ComboBox<>("Preferred Match");
        preferredMatchCombo.setItems("BETTER", "CHEAPER", "CLOSER");
        preferredMatchCombo.setRequiredIndicatorVisible(true);

        Button preferredMatchButton = new Button("UPDATE PREFERRED MATCH");
        preferredMatchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        preferredMatchButton.addClickListener(clickEvent -> {
            updateUserPreferredMatch(preferredMatchCombo.getValue());
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 2));

        formLayout.add(preferredMatchCombo, preferredMatchButton);

        getContent().add(new H6("UPDATE SELECTED USER'S PREFERRED MATCH"));
        getContent().add(formLayout);
    }

    private void createAddUserStarsForm() {
        ComboBox<String> roleCombo = new ComboBox<>("Role");
        roleCombo.setItems("DRIVER", "PASSENGER");
        roleCombo.setRequiredIndicatorVisible(true);

        ComboBox<String> starsCombo = new ComboBox<>("Stars");
        starsCombo.setItems("ONE_STAR", "TWO_STARS", "THREE_STARS", "FOUR_STARS", "FIVE_STARS");
        starsCombo.setRequiredIndicatorVisible(true);

        Button addStarsButton = new Button("ADD USER STARS");
        addStarsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addStarsButton.addClickListener(clickEvent -> {
            addUserStars(roleCombo.getValue(), starsCombo.getValue());
        });

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 3));

        formLayout.add(roleCombo, starsCombo, addStarsButton);

        getContent().add(new H6("ADD A REVIEW TO A SELECTED USER"));
        getContent().add(formLayout);
    }

    private void showNotification(NotificationVariant variant, String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_END);
        getContent().add(notification);
    }

    private void registerUser(String nickname, String name) {
        if (!StringUtils.hasText(nickname) || !StringUtils.hasText(name)) {
            showNotification(LUMO_ERROR, "Please enter a valid nickname or a valid name for the user");
            return;
        }

        try {
            User user = manager.register(nickname, name);
            if (user != null) {
                users.add(user);
                userDataProvider.refreshAll();
                showNotification(LUMO_SUCCESS, "Successfully registered user " + nickname);
            } else {
                showNotification(LUMO_ERROR, "Invalid nickname or user is already registered");
            }
        } catch (RideSharingAppException e) {
            showNotification(LUMO_ERROR, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void addUserCar(String plate, String make, String model, String color) {
        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (!StringUtils.hasText(plate) || !StringUtils.hasText(make) || !StringUtils.hasText(model) || !StringUtils.hasText(color)) {
            showNotification(LUMO_ERROR, "Failed to add car to user, please fill all fields");
            return;
        }

        if (selectedUser.getCar(plate) != null) {
            showNotification(LUMO_ERROR, "Car is already registered");
            return;
        }

        selectedUser.addCar(new Car(plate, make, model, color));
        userDataProvider.refreshAll();
        showNotification(LUMO_SUCCESS, "Successfully added Car " + plate + " for User " + selectedUser.getNick());
    }

    private void deleteUserCar(String plate) {
        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (!StringUtils.hasText(plate)) {
            showNotification(LUMO_ERROR, "Failed to delete car for user " + selectedUser.getNick() + ", please indicate a plate");
            return;
        }

        if (selectedUser.getCar(plate) == null) {
            showNotification(LUMO_ERROR,
                    "User " + selectedUser.getNick() + " does not have a car registered with plate " + plate);
            return;
        }

        try {
            manager.deleteCar(selectedUser.getNick(), selectedUser.getKey(), plate);
            userDataProvider.refreshAll();
            showNotification(LUMO_SUCCESS,
                    "Successfully deleted Car " + plate + " for user " + selectedUser.getNick());
        } catch (RideSharingAppException e) {
            showNotification(LUMO_ERROR, e.getMessage());
        }
    }

    private void updateUserPreferredMatch(String preferredMatch) {
        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (!StringUtils.hasText(preferredMatch)) {
            showNotification(LUMO_ERROR, "Failed to update preferredMatch match for user " + selectedUser.getNick() + ", please select a valid option");
            return;
        }

        try {
            manager.setPreferredMatch(selectedUser.getNick(), selectedUser.getKey(), PreferredMatch.valueOf(preferredMatch));
            userDataProvider.refreshAll();
        } catch (RideSharingAppException e) {
            showNotification(LUMO_ERROR, e.getMessage());
        }
    }

    private void addUserStars(String role, String stars) {
        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (!StringUtils.hasText(role) || !StringUtils.hasText(stars)) {
            showNotification(LUMO_ERROR, "Failed to add stars to user " + selectedUser.getNick() + ", please select a valid option");
            return;
        }

        selectedUser.addStars(UserStars.valueOf(stars), RideRole.valueOf(role));
        userDataProvider.refreshAll();
        showNotification(LUMO_SUCCESS, "Successfully added stars for user " + selectedUser.getNick());
    }
}
