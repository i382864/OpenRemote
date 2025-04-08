package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task Lisadsfasdfast")
@JsModule("./map.js")  // Hier wordt map.js ingeladen
public class MapView extends VerticalLayout {
    public MapView() {
        setSizeFull();
        Div mapDiv = new Div();
        mapDiv.setSizeFull();
        mapDiv.getElement().setAttribute("is", "map-component"); // Verwijzing naar de custom HTML-tag
        add(mapDiv);
    }
}