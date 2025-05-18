package org.vaadin.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Rides")
@Route("rides")
@Menu(order = 2)
public class RidesView extends Composite<VerticalLayout> {

    public RidesView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
