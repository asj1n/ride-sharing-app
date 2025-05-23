package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import org.vaadin.rsa.ride.Ride;
import org.vaadin.rsa.ride.RideRole;
import org.vaadin.rsa.user.Car;
import org.vaadin.rsa.user.User;
import org.vaadin.rsa.user.UserStars;

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
    ListDataProvider<Long> matchedRidesDataProvider = new ListDataProvider<>(new ArrayList<>());
    ListDataProvider<RideMatch> rideMatchDataProvider = new ListDataProvider<>(new ArrayList<>());

    User selectedUser;
    Car selectedCar;
    RideMatch selectedRideMatch;

    public RidesView() throws RideSharingAppException {
        getContent().setWidth("100%");
        createUsersGrid();
        getContent().add(new Hr());
        createUserCarsGrid();
        getContent().add(new Hr());
        createAddAndUpdateRideForm();
        getContent().add(new Hr());
        createRideMatchesGrid();
        getContent().add(new Hr());
        createConcludeRideForm();
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

        getContent().add(new H6("REGISTERED CARS FOR USER"));
        getContent().add(usersCarsGrid);
    }

    private void createAddAndUpdateRideForm() {
        // Add Ride Form

        FormLayout leftFormLayout = new FormLayout();
        leftFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

        NumberField fromLatitudeField = new NumberField("From latitude");
        fromLatitudeField.setRequiredIndicatorVisible(true);
        fromLatitudeField.setMin(-90);
        fromLatitudeField.setMax(90);

        NumberField fromLongitudeField = new NumberField("From longitude");
        fromLongitudeField.setRequiredIndicatorVisible(true);
        fromLongitudeField.setMin(-180);
        fromLongitudeField.setMax(180);

        NumberField toLatitudeField = new NumberField("To latitude");
        toLatitudeField.setRequiredIndicatorVisible(true);
        toLatitudeField.setMin(-90);
        toLatitudeField.setMax(90);

        NumberField toLongitudeField = new NumberField("To longitude");
        toLongitudeField.setRequiredIndicatorVisible(true);
        toLongitudeField.setMin(-180);
        toLongitudeField.setMax(180);

        NumberField costField = new NumberField("Cost");
        costField.setRequiredIndicatorVisible(true);
        costField.setMin(0);

        Select<String> selectRole = new Select<>();
        selectRole.setLabel("Role");
        selectRole.setItems("DRIVER", "PASSENGER");
        selectRole.setRequiredIndicatorVisible(true);

        Button addRideButton = new Button("ADD RIDE");
        addRideButton.getStyle().set("margin-top", "20px");
        addRideButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addRideButton.addClickListener(clickEvent -> {
            addUserRide(fromLatitudeField.getValue(), fromLongitudeField.getValue(),
                        toLatitudeField.getValue(), toLongitudeField.getValue(),
                        costField.getValue(), RideRole.valueOf(selectRole.getValue()));
        });

        leftFormLayout.add(fromLatitudeField);
        leftFormLayout.add(fromLongitudeField);
        leftFormLayout.add(toLatitudeField);
        leftFormLayout.add(toLongitudeField);
        leftFormLayout.add(costField);
        leftFormLayout.add(selectRole);
        leftFormLayout.add(addRideButton, 2);

        // Update Ride Form

        FormLayout rightFormLayout = new FormLayout();
        rightFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Select<Long> rideSelect = new Select<>();
        rideSelect.setLabel("Select RideId");
        rideSelect.setDataProvider(rideDataProvider);

        NumberField currentLatitudeField = new NumberField("Current latitude");
        currentLatitudeField.setRequiredIndicatorVisible(true);
        currentLatitudeField.setMin(-90);
        currentLatitudeField.setMax(90);

        NumberField currentLongitudeField = new NumberField("Current longitude");
        currentLongitudeField.setRequiredIndicatorVisible(true);
        currentLongitudeField.setMin(-180);
        currentLongitudeField.setMax(180);

        Button updateRideButton = new Button("UPDATE RIDE");
        updateRideButton.getStyle().set("margin-top", "20px");
        updateRideButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_WARNING);
        updateRideButton.addClickListener(clickEvent -> {
            updateUserRide(rideSelect.getValue(), currentLatitudeField.getValue(), currentLongitudeField.getValue());
        });

        rightFormLayout.add(rideSelect);
        rightFormLayout.add(currentLatitudeField);
        rightFormLayout.add(currentLongitudeField);
        rightFormLayout.add(updateRideButton, 3);

        // Vertical Division

        Div verticalHr = new Div();
        verticalHr.getStyle()
                .set("width", "1px")
                .set("background-color", "#454545")
                .set("align-self", "stretch");

        HorizontalLayout horizontalLayout = new HorizontalLayout(leftFormLayout, verticalHr, rightFormLayout);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setWidthFull();
        horizontalLayout.getThemeList().add("spacing-xl");

        getContent().add(new H6("ADD / UPDATE RIDES"));
        getContent().add(horizontalLayout);
    }

    private void createRideMatchesGrid() {
        Grid<RideMatch> rideMatchGrid = new Grid<>(RideMatch.class, false);

        rideMatchGrid.addColumn(RideMatch::getId).setHeader("Ride Match ID").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getCar().getPlate()).setHeader("Car Plate").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getName(RideRole.DRIVER)).setHeader("Driver").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getName(RideRole.PASSENGER)).setHeader("Passenger").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getWhere(RideRole.DRIVER)).setHeader("Driver Location").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getWhere(RideRole.PASSENGER)).setHeader("Passenger Location").setAutoWidth(true);
        rideMatchGrid.addColumn(ride -> ride.getStars(RideRole.DRIVER)).setHeader("Driver Average").setAutoWidth(true);
        rideMatchGrid.addColumn(RideMatch::getCost).setHeader("Cost");

        rideMatchGrid.setEmptyStateText("No rides found");
        rideMatchGrid.addClassName("rides-grid");
        rideMatchGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        rideMatchGrid.setAllRowsVisible(true);
        rideMatchGrid.setDataProvider(rideMatchDataProvider);

        rideMatchGrid.asSingleSelect().addValueChangeListener(event -> {
            selectedRideMatch = event.getValue();
        });

        Button acceptRideMatchButton = new Button("ACCEPT RIDE MATCH");
        acceptRideMatchButton.getStyle().set("width", "100%");
        acceptRideMatchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        acceptRideMatchButton.addClickListener(clickEvent -> {
            acceptRideMatch(selectedRideMatch);
        });

        getContent().add(new H6("POSSIBLE RIDE MATCHES"));
        getContent().add(rideMatchGrid);
        getContent().add(acceptRideMatchButton);
    }

    private void createConcludeRideForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));

        Select<Long> rideSelect = new Select<>();
        rideSelect.setLabel("Select RideId");
        rideSelect.setDataProvider(matchedRidesDataProvider);

        ComboBox<String> starsCombo = new ComboBox<>("Stars");
        starsCombo.setItems("ONE_STAR", "TWO_STARS", "THREE_STARS", "FOUR_STARS", "FIVE_STARS");
        starsCombo.setRequiredIndicatorVisible(true);

        Button concludeRideButton = new Button("CONCLUDE RIDE");
        concludeRideButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        concludeRideButton.addClickListener(clickEvent -> {
            concludeRide(rideSelect.getValue(), UserStars.valueOf(starsCombo.getValue()));
        });

        formLayout.add(rideSelect);
        formLayout.add(starsCombo);
        formLayout.add(concludeRideButton);

        getContent().add(new H6("CONCLUDE RIDE"));
        getContent().add(formLayout);
    }

    private void showNotification(NotificationVariant variant, String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_END);
        getContent().add(notification);
    }

    private void addUserRide(Double fromLatitude, Double fromLongitude,
                             Double toLatitude, Double toLongitude, Double cost, RideRole role) {

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

            if (plate == null && role == RideRole.DRIVER) {
                showNotification(LUMO_ERROR, "Please select a car for a DRIVER ride");
                return;
            }

            if (plate != null && role == RideRole.PASSENGER) {
                showNotification(LUMO_ERROR, "Please do not select a car for a PASSENGER ride");
                return;
            }

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
        Set<RideMatch> rideMatches = manager.updateRide(rideId, currentLocation);
        rideMatchDataProvider.getItems().clear();
        rideMatchDataProvider.getItems().addAll(rideMatches);
        rideMatchDataProvider.refreshAll();

        showNotification(LUMO_SUCCESS, "Successfully updated ride " + rideId);
    }

    private void acceptRideMatch(RideMatch rideMatch) {
        if (selectedRideMatch == null) {
            showNotification(LUMO_WARNING, "Please select a ride match from the table");
            return;
        }

        Ride driverRide = rideMatch.getRide(RideRole.DRIVER);
        Ride passengerRide = rideMatch.getRide(RideRole.PASSENGER);

        manager.acceptMatch(driverRide.getId(), rideMatch.getId());
        manager.acceptMatch(passengerRide.getId(), rideMatch.getId());

        if (rideMatch.getRide(RideRole.DRIVER).isMatched()
         && rideMatch.getRide(RideRole.PASSENGER).isMatched()) {

            matchedRidesDataProvider.getItems().add(driverRide.getId());
            matchedRidesDataProvider.getItems().add(passengerRide.getId());
            matchedRidesDataProvider.refreshAll();

            rideMatchDataProvider.getItems().clear();
            rideMatchDataProvider.refreshAll();

            showNotification(LUMO_SUCCESS, "Successfully accepted ride match " + rideMatch.getId());
        }
    }

    private void concludeRide(Long rideId, UserStars stars) {
        if (rideId == null || stars == null) {
            showNotification(LUMO_ERROR, "Unable to conclude rideId " + rideId);
            return;
        }

        manager.concludeRide(rideId, stars);
        showNotification(LUMO_SUCCESS, "Successfully concluded rideId " + rideId + " with " + stars);

        rideMatchDataProvider.getItems().clear();
        rideMatchDataProvider.refreshAll();

        rideDataProvider.getItems().remove(rideId);
        rideDataProvider.refreshAll();

        matchedRidesDataProvider.getItems().remove(rideId);
        matchedRidesDataProvider.refreshAll();

        userDataProvider.refreshAll();
    }
}