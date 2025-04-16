package com.mistletoe.magic.base.ui.view;

import java.util.Random;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLayout;
import com.storedobject.chart.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@JavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js")
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
@JavaScript("https://cdn.jsdelivr.net/npm/leaflet-fullscreen@1.0.2/dist/Leaflet.fullscreen.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/leaflet-fullscreen@1.0.2/dist/leaflet.fullscreen.css")
public class MapView extends VerticalLayout {
    public MapView() {
        addClassNames(LumoUtility.Background.BASE);
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);

        // Create main content area for map
        Div mapContainer = new Div();
        mapContainer.setId("map");
        mapContainer.setSizeFull();
        mapContainer.getStyle()
            .set("position", "relative")
            .set("margin", "0")
            .set("padding", "0")
            .set("width", "100vw")  // Set width to full viewport width
            .set("height", "100vh"); // Set height to full viewport height

        // Create search box
        TextField searchBox = new TextField();
        searchBox.setPlaceholder("Zoeken");
        searchBox.getStyle()
            .set("position", "absolute")
            .set("top", "16px")
            .set("left", "116px")
            .set("z-index", "1000")
            .set("width", "200px")
            .set("height", "30px")
            .set("background-color", "white")
            .set("border", "1px solid #ccc")
            .set("border-radius", "4px")
            .set("padding", "0 8px")
            .set("color", "#333")
            .set("--lumo-contrast-60pct", "#fff") // Placeholder color
            .set("transition", "border-color 0.2s")
            .set("outline", "none");
        
        // Add focus styles
        searchBox.addFocusListener(e -> 
            searchBox.getStyle().set("border-color", "ffff")
        );
        searchBox.addBlurListener(e -> 
            searchBox.getStyle().set("border-color", "#fff")
        );
        mapContainer.add(searchBox);

        // Create info overlay
        VerticalLayout infoOverlay = new VerticalLayout();
        infoOverlay.setWidth("200px");
        infoOverlay.setPadding(false);
        infoOverlay.setSpacing(false);
        infoOverlay.getStyle()
            .set("background-color", "rgba(255, 255, 255, 0.95)")
            .set("border-radius", "4px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)")
            .set("position", "absolute")
            .set("top", "16px")
            .set("right", "95px") // Position directly to the left of zoom controls
            .set("z-index", "1000")
            .set("padding", "12px");
        
        // Add info content
        addInfoContent(infoOverlay);
        mapContainer.add(infoOverlay);
        
        // Add map container to the main layout
        add(mapContainer);
        setFlexGrow(1, mapContainer);

        // Initialize map using JavaScript
        getElement().executeJs("""
            const map = L.map('map', {
                fullscreenControl: true,
                fullscreenControlOptions: {
                    position: 'topright'
                },
                zoomControl: false  // Disable default zoom control
            }).setView([51.9757, 4.2757], 13);
            
            // Use CartoDB Positron style for a cleaner, more modern look
            L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
                attribution: '©OpenStreetMap, ©CartoDB',
                subdomains: 'abcd',
                maxZoom: 19
            }).addTo(map);
            
            // Add custom positioned zoom control
            L.control.zoom({
                position: 'topright'
            }).addTo(map);

            // Add scale control at the bottom
            L.control.scale().addTo(map);
            
            // Add custom CSS for zoom controls and layout
            const style = document.createElement('style');
            style.textContent = `
                .leaflet-control-zoom {
                    border: none !important;
                    margin-right: 10px !important;
                }
                .leaflet-control-zoom a {
                    background-color: white !important;
                    color: #666 !important;
                    width: 30px !important;
                    height: 30px !important;
                    line-height: 30px !important;
                    border: 1px solid #ccc !important;
                    border-radius: 4px !important;
                    margin-bottom: 5px !important;
                }
                .leaflet-control-zoom a:hover {
                    background-color: #f4f4f4 !important;
                    color: #333 !important;
                }
                .leaflet-control-zoom-in {
                    margin-bottom: 5px !important;
                }
                .leaflet-control-fullscreen {
                    border: none !important;
                    margin-right: 55px !important; // Space between fullscreen and zoom controls
                }
                .leaflet-control-fullscreen a {
                    background-color: white !important;
                    color: #666 !important;
                    width: 30px !important;
                    height: 30px !important;
                    line-height: 30px !important;
                    border: 1px solid #ccc !important;
                    border-radius: 4px !important;
                }
                .leaflet-control-fullscreen a:hover {
                    background-color: #f4f4f4 !important;
                    color: #333 !important;
                }
            `;
            document.head.appendChild(style);
            
            // Update map size when container size changes
            setTimeout(function() {
                map.invalidateSize();
            }, 100);
        """);

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
        mapContainer.add(soChart);
    }

    private void addInfoContent(VerticalLayout container) {
        // Add robot information fields with more compact styling
        addInfoField(container, "Direction", "26");
        addInfoField(container, "Harvested session", "821");
        addInfoField(container, "Harvested total", "45");
        addInfoField(container, "Operation mode", "UNLOADING");
        addInfoField(container, "Speed (km/h)", "2");
        addInfoField(container, "Vegetable type", "TOMATO");
    }

    private void addInfoField(VerticalLayout container, String label, String value) {
        HorizontalLayout field = new HorizontalLayout();
        field.setWidthFull();
        field.setSpacing(false);
        field.setPadding(false);
        field.setMargin(false);
        field.setAlignItems(Alignment.CENTER);
        field.getStyle().set("margin-bottom", "4px");
        
        Div labelDiv = new Div();
        labelDiv.setText(label);
        labelDiv.getStyle()
            .set("color", "var(--lumo-secondary-text-color)")
            .set("font-size", "12px");
        
        Div valueDiv = new Div();
        valueDiv.setText(value);
        valueDiv.getStyle()
            .set("font-weight", "500")
            .set("font-size", "12px")
            .set("margin-left", "auto");
        
        field.add(labelDiv, valueDiv);
        container.add(field);
    }
}
