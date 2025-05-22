package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;
import org.vaadin.rsa.Manager;
import org.vaadin.rsa.RideSharingAppException;
import org.vaadin.rsa.match.Location;
import org.vaadin.rsa.match.RideMatch;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.Car;
import org.vaadin.rsa.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_WARNING;

@PageTitle("Rides")
@Route("rides")
@Menu(order = 2, icon = LineAwesomeIconUrl.CAR_SOLID)
public class RidesView extends Composite<VerticalLayout> {
    Manager manager = Manager.getInstance();
    List<User> users = manager.getUsers();

    ListDataProvider<User> userDataProvider = new ListDataProvider<>(users);
    ListDataProvider<Car> carDataProvider = new ListDataProvider<>(new ArrayList<>());
    ListDataProvider<Long> rideDataProvider = new ListDataProvider<>(new ArrayList<>());

    User selectedUser;
    Car selectedCar;

    public RidesView() throws RideSharingAppException {
        getContent().setWidth("100%");
        createUsersGrid();
        getContent().add(new Hr());
        createUserCarsGrid();
        getContent().add(new Hr());
        createAddRideForm();
        getContent().add(new Hr());
        createUpdateRideForm();
    }

    private void createUsersGrid() {
        Grid<User> usersGrid = new Grid<>(User.class, false);

        usersGrid.addColumn(User::getNick).setHeader("Nickname");
        usersGrid.addColumn(User::getName).setHeader("Name");
        usersGrid.addColumn(user -> user.getCars().size()).setHeader("Cars");
        usersGrid.addColumn(User::getPreferredMatch).setHeader("Preferred Match");
        usersGrid.addColumn(user -> user.getAverage(RideRole.DRIVER)).setHeader("Driver Average");
        usersGrid.addColumn(user -> user.getAverage(RideRole.PASSENGER)).setHeader("Passenger Average");
        usersGrid.setEmptyStateText("No users found");
        usersGrid.addClassName("users-grid");
        usersGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        usersGrid.setAllRowsVisible(true);
        usersGrid.setDataProvider(userDataProvider);

        usersGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedUser = event.getValue();
            if (selectedUser != null) {
                // Update Car table
                carDataProvider.getItems().clear();
                carDataProvider.getItems().addAll(selectedUser.getCars());
            } else {
                carDataProvider.getItems().clear();
            }

            selectedCar = null;
            carDataProvider.refreshAll();
        });

        getContent().add(new H6("REGISTERED USERS"));
        getContent().add(usersGrid);
    }

    private void createUserCarsGrid() {
        Grid<Car> usersCarsGrid = new Grid<>(Car.class, false);

        usersCarsGrid.addColumn(Car::getPlate).setHeader("Plate");
        usersCarsGrid.addColumn(Car::getMake).setHeader("Make");
        usersCarsGrid.addColumn(Car::getModel).setHeader("Model");
        usersCarsGrid.addColumn(Car::getColor).setHeader("Color");

        usersCarsGrid.setEmptyStateText("No cars found");
        usersCarsGrid.addClassName("users-grid");
        usersCarsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        usersCarsGrid.setAllRowsVisible(true);
        usersCarsGrid.setDataProvider(carDataProvider);

        usersCarsGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedCar = event.getValue();
        });

        getContent().add(new H6("SELECTED USER CARS"));
        getContent().add(usersCarsGrid);
    }

    private void createAddRideForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        // TODO PointOutOfBound in quadtree with max elements in coords Default values
        NumberField fromLatitudeField = new NumberField("Current latitude");
        fromLatitudeField.setRequiredIndicatorVisible(true);
        fromLatitudeField.setMin(-90);
        fromLatitudeField.setMax(90);

        NumberField fromLongitudeField = new NumberField("Current longitude");
        fromLongitudeField.setRequiredIndicatorVisible(true);
        fromLongitudeField.setMin(-180);
        fromLongitudeField.setMax(180);

        NumberField toLatitudeField = new NumberField("Destination latitude");
        toLatitudeField.setRequiredIndicatorVisible(true);
        toLatitudeField.setMin(-90);
        toLatitudeField.setMax(90);

        NumberField toLongitudeField = new NumberField("Destination longitude");
        toLongitudeField.setRequiredIndicatorVisible(true);
        toLongitudeField.setMin(-180);
        toLongitudeField.setMax(180);

        NumberField costField = new NumberField("Cost");
        costField.setRequiredIndicatorVisible(true);
        costField.setMin(0);

        Button addRideButton = new Button("ADD RIDE");
        addRideButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addRideButton.addClickListener(clickEvent -> {
            addUserRide(fromLatitudeField.getValue(), fromLongitudeField.getValue(),
                        toLatitudeField.getValue(), toLongitudeField.getValue(),
                        costField.getValue());
        });

        formLayout.add(fromLatitudeField);
        formLayout.add(fromLongitudeField);
        formLayout.add(toLatitudeField);
        formLayout.add(toLongitudeField);
        formLayout.add(costField);
        formLayout.add(addRideButton);

        getContent().add(new H6("ADD RIDE"));
        getContent().add(formLayout);
    }

    private void createUpdateRideForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));

        Select<Long> rideSelect = new Select<>();
        rideSelect.setLabel("Select RideId");
        rideSelect.setDataProvider(rideDataProvider);

        NumberField currentLatitudeField = new NumberField("Current latitude");
        currentLatitudeField.setRequiredIndicatorVisible(true);

        NumberField currentLongitudeField = new NumberField("Current longitude");
        currentLongitudeField.setRequiredIndicatorVisible(true);

        Button updateRideButton = new Button("UPDATE RIDE");
        updateRideButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_WARNING);
        updateRideButton.addClickListener(clickEvent -> {
            updateUserRide(rideSelect.getValue(), currentLatitudeField.getValue(), currentLongitudeField.getValue());
        });

        formLayout.add(rideSelect);
        formLayout.add(currentLatitudeField);
        formLayout.add(currentLongitudeField);
        formLayout.add(updateRideButton, 3);

        getContent().add(new H6("UPDATE RIDE"));
        getContent().add(formLayout);
    }

    private void showNotification(NotificationVariant variant, String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_END);
        getContent().add(notification);
    }

    private void addUserRide(Double fromLatitude, Double fromLongitude,
                             Double toLatitude, Double toLongitude, Double cost) {

        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (fromLatitude == null || fromLongitude == null
         || toLatitude == null || toLongitude == null
         || cost == null) {
            showNotification(LUMO_ERROR, "Invalid parameters. Please fill all fields");
            return;
        }

        Location from = new Location(fromLatitude, fromLongitude);
        Location to = new Location(toLatitude, toLongitude);

        try {
            String plate = selectedCar != null ? selectedCar.getPlate() : null;

            long rideId = manager.addRide(selectedUser.getNick(), selectedUser.getKey(), from, to, plate, cost.floatValue());
            rideDataProvider.getItems().add(rideId);
            rideDataProvider.refreshAll();

            if (plate != null) {
                showNotification(LUMO_SUCCESS, "Successfully added DRIVER ride " + rideId);
            } else {
                showNotification(LUMO_SUCCESS, "Successfully added PASSENGER ride " + rideId);
            }
        } catch (RideSharingAppException e) {
            showNotification(LUMO_ERROR, e.getMessage());
        }
    }

    private void updateUserRide(Long rideId, Double currentLatitude, Double currentLongitude) {
        if (selectedUser == null) {
            showNotification(LUMO_WARNING, "Please select a registered user from the table");
            return;
        }

        if (rideId == null || currentLatitude == null || currentLongitude == null) {
            showNotification(LUMO_ERROR, "Invalid parameters. Please fill all fields");
            return;
        }

        Location currentLocation = new Location(currentLatitude, currentLongitude);
        Set<RideMatch> rideMatches = manager.updateRide(rideId, currentLocation); // TODO guardar rideMatches
        System.out.println(rideMatches.size());
        showNotification(LUMO_SUCCESS, "Successfully updated ride " + rideId);
    }
}
