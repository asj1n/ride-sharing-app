package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Home")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class HomeView extends Composite<VerticalLayout> {

    public HomeView() {
        getContent().setWidth("100%");
        getContent().add(new H2("Welcome to the RSA User Interface!"));
        createAppInstructions();
    }

    private void createAppInstructions() {
        getContent().add(
                new H3("How to Use the App"),
                new Hr(),
                new Paragraph("Start with the Users tab to:"),
                new UnorderedList(
                        new ListItem("Register a new user."),
                        new ListItem("View all registered users (stored persistently in users.ser)."),
                        new ListItem("Add a car to a selected user."),
                        new ListItem("Delete a car from a selected user."),
                        new ListItem("Update a selected user's preferred match type for new Rides."),
                        new ListItem("Add DRIVER or PASSENGER reviews for a selected user.")
                ),
                new Hr(),
                new Paragraph("Next, go to the Rides tab to:"),
                new UnorderedList(
                        new ListItem("Select a registered user."),
                        new ListItem("Select a Car for the user (required for DRIVER Rides)."),
                        new ListItem("Create a new Ride request by specifying From/To coordinates, cost, and role. For DRIVER Rides, select a Car. A rideId will be generated."),
                        new ListItem("Update a Ride by selecting its rideId and providing the user's current location. This returns possible RideMatches."),
                        new ListItem("View all RideMatches for that rideId and select one to accept it. RideMatches are sorted by the user’s preferred type. Accepting one clears the rest."),
                        new ListItem("Conclude the ride and add reviews for both users involved in the accepted RideMatch. This updates their average review scores.")
                )
        );
    }
}
