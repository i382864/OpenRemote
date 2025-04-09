package com.mistletoe.magic.base.ui.view;

import java.util.Random;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;  // Import the Label component
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.storedobject.chart.*;
import com.storedobject.chart.*;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "")
public class MapView extends VerticalLayout {
    public MapView() {
        setSizeFull();

        // Create a new Label to display text
        Label textLabel = new Label("This is a simple text on the map view");
        // Add the text label to the layout
        add(textLabel);

        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();
        Data xValues = new Data(), yValues = new Data();
        for (int x = 0; x < 40; x++) {
        xValues.add(x);
        yValues.add(random.nextDouble());
        }
        xValues.setName("X Values");
        yValues.setName("Random Values");

        // Line chart is initialized with the generated XY values
        LineChart lineChart = new LineChart(xValues, yValues);
        lineChart.setName("40 Random Values");

        // Line chart needs a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        lineChart.plotOn(rc);
    
        
        // Add to the chart display area with a simple title
        soChart.add(lineChart, new Title("Sample Line Chart"));

        // Set the component for the view
        add(soChart);
    }
}
