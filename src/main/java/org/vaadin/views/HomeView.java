package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
//import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Home")
@Route("")
@Menu(order = 0)
public class HomeView extends Composite<VerticalLayout> {

    public HomeView() {
        H3 h3 = new H3();
        getContent().setWidth("100%");
        getContent().setHeight("min-content");
        h3.setText("Welcome to the RSA User Interface!");
        h3.setWidth("max-content");
        getContent().add(h3);
    }
}
