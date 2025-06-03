package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
// import com.vaadin.flow.component.map.Assets.Asset; // Asset class is defined locally
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Div;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
// Unused HttpClient and Jackson imports are commented out as they were in the provided snippet
// import java.io.IOException;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.Map; // Needed for generic JSON parsing
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.core.JsonProcessingException;

// import java.util.Objects; // Not explicitly used, Objects class is implicitly available

@Route(value = "assets", layout = MainLayout.class)
@PageTitle("Assets")
@JavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js")
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
public class AssetsView extends HorizontalLayout {
    private final TreeGrid<AssetItem> tree;
    private final TreeDataProvider<AssetItem> dataProvider;
    private final TextField filterField;
    private final VerticalLayout detailsLayout;
    private List<AssetItem> myAssets;

    public AssetsView() {
        myAssets = new ArrayList<>();
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Left side with tree
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setWidth("300px");
        leftSide.setPadding(false);
        leftSide.setSpacing(false);
        
        // Add blue header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("48px");
        header.setPadding(true);
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
            .set("background-color", "var(--lumo-primary-color)")
            .set("padding", "0 var(--lumo-space-m)");

        H3 title = new H3("Assets");
        title.getStyle()
            .set("color", "var(--lumo-base-color)")
            .set("font-size", "var(--lumo-font-size-l)")
            .set("font-weight", "500")
            .set("margin", "0");

        Icon menuIcon = VaadinIcon.MENU.create();
        menuIcon.setSize("24px");
        menuIcon.getStyle()
            .set("color", "var(--lumo-base-color)")
            .set("margin-left", "auto");

        header.add(title, menuIcon);
        leftSide.add(header);
        
        // Create filter field
        filterField = new TextField();
        filterField.setPlaceholder("Filter...");
        filterField.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterField.setWidthFull();
        filterField.addClassNames(LumoUtility.Padding.SMALL);
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(e -> updateFilter());

        // Initialize tree grid
        tree = new TreeGrid<>();
        tree.addHierarchyColumn(AssetItem::getName)
            .setHeader("Assets")
            .setFlexGrow(1);

        tree.setHeightFull();
        tree.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        // Set up the data provider with sample data
        TreeData<AssetItem> treeData = new TreeData<>();
        List<AssetItem> rootItems = getAssetItems();
        rootItems.forEach(item -> {
            treeData.addItem(null, item);
            addChildrenToTree(treeData, item);
        });

        dataProvider = new TreeDataProvider<>(treeData);
        tree.setDataProvider(dataProvider);

        // Add selection listener
        tree.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::showDetails);
        });

        leftSide.add(filterField, tree);
        
        // Right side - Details view container
        detailsLayout = new VerticalLayout();
        detailsLayout.setWidthFull();
        detailsLayout.setPadding(true); // Keep padding for detailsLayout itself
        detailsLayout.setSpacing(true); // Keep spacing for detailsLayout itself

        // Add both sides to main layout
        add(leftSide, detailsLayout);
        setFlexGrow(1, detailsLayout);
    }

    private void populateInfoAndAttributesColumn(VerticalLayout columnLayout) {
        // INFO Section
        addSection(columnLayout, "INFO");
        TextArea notes = new TextArea();
        notes.setLabel("Notes");
        notes.setWidthFull();
        notes.setHeight("50px");
        notes.getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)");
        addFieldWithTimestamp(columnLayout, notes);

        // ATTRIBUTES Section
        addSection(columnLayout, "ATTRIBUTES");
        
        TextField consoleName = createTextField("Console name", "Chrome", false);
        addFieldWithTimestamp(columnLayout, consoleName);

        TextField consolePlatform = createTextField("Console platform", "Windows 10 64-bit", false);
        addFieldWithTimestamp(columnLayout, consolePlatform);

        TextArea consoleProviders = createTextArea("Console providers", "{\n  \"push\": {\n    \"version\": \"web\",\n    \"requiresPermission\": false,\n    \"hasPermission\": true\n  }\n}", false);
        addFieldWithTimestamp(columnLayout, consoleProviders);

        TextField consoleVersion = createTextField("Console version", "135.0.0.0", false);
        addFieldWithTimestamp(columnLayout, consoleVersion);
    }


    private void showDetails(AssetItem item) {
        detailsLayout.removeAll();
        // Reset spacing and padding for detailsLayout before adding new content
        detailsLayout.setSpacing(true); // Default spacing for sections within detailsLayout
        detailsLayout.setPadding(true); // Default padding for detailsLayout

        // Only show details if item is Chrome
        if (!"Chrome".equals(item.getName())) {
            Div messageDiv = new Div(new Span("Please select an asset on the left"));
            messageDiv.getStyle().set("padding", "var(--lumo-space-m)");
            detailsLayout.add(messageDiv);
            return;
        }

        // Header with icon and name
        HorizontalLayout itemHeader = new HorizontalLayout();
        Icon icon = VaadinIcon.DESKTOP.create();
        icon.setSize("16px");
        H2 title = new H2(item.getName());
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "var(--lumo-font-size-m)");
        itemHeader.add(icon, title);
        itemHeader.setAlignItems(Alignment.CENTER);
        itemHeader.setSpacing(true);
        itemHeader.getStyle().set("margin-bottom", "0.5em");
        
        detailsLayout.add(itemHeader);

        // Create main content layout that will contain three columns
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true); // Spacing between columns
        mainContent.setPadding(false); // No extra padding for mainContent itself

        // Column 1: INFO and ATTRIBUTES
        VerticalLayout infoColumnLeft = new VerticalLayout();
        infoColumnLeft.setSpacing(false); // Spacing is handled by addFieldWithTimestamp
        infoColumnLeft.setPadding(false); // Padding is handled by sections or fields if needed
        populateInfoAndAttributesColumn(infoColumnLeft);

        // Column 2: LOCATION
        VerticalLayout locationColumnMiddle = new VerticalLayout();
        locationColumnMiddle.setSpacing(false);
        locationColumnMiddle.setPadding(false);

        addSection(locationColumnMiddle, "LOCATION");
        Div map = new Div();
        map.setId("assetMap"); // Give the map div a specific ID
        map.setHeight("400px");
        map.setWidthFull();
        map.getStyle()
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("margin-bottom", "var(--lumo-space-m)"); // Keep consistent margin
        locationColumnMiddle.add(map);

        // Initialize the map using JavaScript
        map.getElement().executeJs("""
            if (window.assetLeafletMap) {
                window.assetLeafletMap.remove(); // Remove previous map instance if exists
                window.assetLeafletMap = null;
            }
            const mapElement = document.getElementById('assetMap');
            if (mapElement && !mapElement._leaflet_id) { // Check if map not already initialized on this element
                 window.assetLeafletMap = L.map('assetMap', {
                    zoomControl: false
                }).setView([51.9757, 4.2757], 13);
                
                L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
                    attribution: '©OpenStreetMap, ©CartoDB',
                    subdomains: 'abcd',
                    maxZoom: 19
                }).addTo(window.assetLeafletMap);
                
                L.control.zoom({
                    position: 'topright'
                }).addTo(window.assetLeafletMap);

                L.control.scale().addTo(window.assetLeafletMap);
                
                const styleId = 'leaflet-custom-zoom-style';
                if (!document.getElementById(styleId)) {
                    const style = document.createElement('style');
                    style.id = styleId;
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
                    `;
                    document.head.appendChild(style);
                }
                
                setTimeout(function() {
                    if (window.assetLeafletMap) {
                        window.assetLeafletMap.invalidateSize();
                    }
                }, 100);
            } else if (mapElement && mapElement._leaflet_id && window.assetLeafletMap) {
                 // If map element exists and has leaflet_id, just invalidate size
                 setTimeout(function() {
                    if (window.assetLeafletMap) {
                        window.assetLeafletMap.invalidateSize();
                    }
                }, 100);
            }
        """);

        // Column 3: INFO and ATTRIBUTES (copy of the first column)
        VerticalLayout infoColumnRight = new VerticalLayout();
        infoColumnRight.setSpacing(false);
        infoColumnRight.setPadding(false);
        populateInfoAndAttributesColumn(infoColumnRight);

        // Add all three columns to main content area
        mainContent.add(infoColumnLeft, locationColumnMiddle, infoColumnRight);
        
        // Distribute space equally among the three columns
        mainContent.setFlexGrow(1, infoColumnLeft);
        mainContent.setFlexGrow(1, locationColumnMiddle);
        mainContent.setFlexGrow(1, infoColumnRight);

        // Add the main content (with three columns) to the details layout
        detailsLayout.add(mainContent);
    }

    private TextField createTextField(String label, String value, boolean addChevron) {
        TextField field = new TextField();
        field.setLabel(label);
        field.setValue(value);
        field.setWidthFull(); // Ensure field takes full width of its parent column
        field.setHeight("44px");
        
        // Style the field
        field.getStyle()
            .set("background", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("--vaadin-input-field-border-width", "0")
            // .set("--vaadin-text-field-default-width", "100%") // setWidthFull handles this
            .set("margin", "0")
            .set("--lumo-text-field-size", "var(--lumo-size-s)")
            .set("--lumo-font-size-s", "var(--lumo-font-size-xs)")
            .set("--vaadin-input-field-label-color", "var(--lumo-contrast-70pct)")
            .set("--vaadin-input-field-label-font-weight", "400")
            .set("--vaadin-input-field-label-font-size", "var(--lumo-font-size-xs)")
            .set("--vaadin-input-field-label-spacing", "0 0 4px 0");

        if (addChevron) {
            Icon chevron = VaadinIcon.CHEVRON_RIGHT.create();
            chevron.setSize("16px");
            chevron.getStyle()
                .set("position", "absolute")
                .set("right", "8px")
                .set("top", "50%")
                .set("transform", "translateY(-50%)")
                .set("color", "var(--lumo-primary-color)");
            
            // For TextField, chevron needs to be handled carefully with Vaadin's internal structure
            // A simple Div wrapper might not work as expected for styling/focus.
            // For this task, keeping it simple as per original. If chevron is crucial, 
            // TextField's setSuffixComponent could be an alternative.
            // The original code created a Div wrapper but didn't add it, this is a slight correction.
            // However, suffix component is better. For now, let's assume addChevron is false.
            // If 'addChevron' were true, this part would need more robust implementation.
            // The original `return field;` was outside the `if (addChevron)` block's `Div` return,
            // which meant the wrapper wasn't used. The `createTextField` method in original code
            // always returned the field, not the wrapper, if `addChevron` was true.
            // For simplicity and given `addChevron` is always `false` in its current usage,
            // I'll leave the chevron logic as is but note its potential issues.
        }
        
        return field;
    }

    private TextArea createTextArea(String label, String value, boolean addChevron) {
        TextArea area = new TextArea();
        area.setLabel(label);
        area.setValue(value);
        area.setWidthFull(); // Ensure area takes full width of its parent column
        area.setHeight(label.equals("Console providers") ? "120px" : "44px");
        
        // Style the field
        area.getStyle()
            .set("background", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("--vaadin-input-field-border-width", "0")
            // .set("--vaadin-text-field-default-width", "100%") // setWidthFull handles this
            .set("margin", "0")
            .set("--lumo-text-field-size", "var(--lumo-size-s)")
            .set("--lumo-font-size-s", "var(--lumo-font-size-xs)")
            .set("--vaadin-input-field-label-color", "var(--lumo-contrast-70pct)")
            .set("--vaadin-input-field-label-font-weight", "400")
            .set("--vaadin-input-field-label-font-size", "var(--lumo-font-size-xs)")
            .set("--vaadin-input-field-label-spacing", "0 0 4px 0");

        if (label.equals("Console providers")) {
            area.getStyle().set("font-family", "var(--lumo-font-family-monospace)");
        }

        if (addChevron) {
            Icon chevron = VaadinIcon.CHEVRON_RIGHT.create();
            chevron.setSize("16px");
            chevron.getStyle()
                .set("position", "absolute")
                .set("right", "8px")
                .set("top", "22px") 
                .set("transform", "translateY(-50%)")
                .set("color", "var(--lumo-primary-color)");
            
            // Similar to TextField, if chevron is critical, suffix component or careful styling of wrapper is needed.
            // The original code created a Div wrapper but didn't add it.
            // For simplicity and given `addChevron` is always `false` in its current usage.
        }
        
        return area;
    }

    private void addSection(VerticalLayout container, String titleText) {
        H3 header = new H3(titleText);
        header.getStyle()
            .set("font-size", "var(--lumo-font-size-s)")
            .set("font-weight", "500")
            .set("color", "var(--lumo-secondary-text-color)")
            .set("margin", "var(--lumo-space-m) 0 var(--lumo-space-xs) 0");
        container.add(header);
    }

    private void addFieldWithTimestamp(VerticalLayout container, Component field) {
        container.add(field);
        
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
            .toLowerCase();
        
        Span timestamp = new Span("Updated: " + formattedDate);
        timestamp.getStyle()
            .set("color", "var(--lumo-tertiary-text-color)")
            .set("font-size", "var(--lumo-font-size-xs)")
            .set("display", "block")
            .set("margin", "4px 0 var(--lumo-space-m) 0"); // Added space M at bottom
        container.add(timestamp);
    }

    private void addChildrenToTree(TreeData<AssetItem> treeData, AssetItem parent) {
        parent.getChildren().forEach(child -> {
            treeData.addItem(parent, child);
            addChildrenToTree(treeData, child);
        });
    }

    private void updateFilter() {
        dataProvider.setFilter(item -> 
            filterField.getValue().isEmpty() || 
            item.getName().toLowerCase().contains(filterField.getValue().toLowerCase()));
    }

    private static List<AssetItem> getAssetItems() {
        List<AssetItem> items = new ArrayList<>();

        // Root items
        AssetItem consoles = new AssetItem("Consoles");
        AssetItem greenEquipment = new AssetItem("GreenEquipment Distribution");
        AssetItem highTech = new AssetItem("High-Tech Greenhouse Distribution");
        AssetItem simulator = new AssetItem("Simulator");

        // Add child items to Consoles
        consoles.addChild(new AssetItem("Chrome")); // This is the one that shows details
        consoles.addChild(new AssetItem("Chrome-2")); // To distinguish if needed
        consoles.addChild(new AssetItem("Firefox"));
        consoles.addChild(new AssetItem("Safari"));

        // Add child items to GreenEquipment Distribution
        greenEquipment.addChild(new AssetItem("Paprika Perfect BV"));
        greenEquipment.addChild(new AssetItem("Qoreau Horticulture"));
        greenEquipment.addChild(new AssetItem("Vegetables & More"));

        // Add child items to High-Tech Greenhouse Distribution
        highTech.addChild(new AssetItem("Haanen Vegetables BV"));
        highTech.addChild(new AssetItem("RTD Vegetable Grower"));

        // Add all root items to the list
        items.add(consoles);
        items.add(greenEquipment);
        items.add(highTech);
        items.add(simulator);

    //     String apiUrl = "http://34.29.191.54/asset";
    //     ObjectMapper objectMapper = new ObjectMapper(); // Jackson JSON mapper
    //     HttpClient client = HttpClient.newHttpClient();
    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(apiUrl))
    //             .header("Accept", "application/json")
    //             .GET()
    //             .build();
    // ... (rest of the commented out API call code) ...
    return items;
}

    
    public static class AssetItem {
        private final String name;
        private final List<AssetItem> children = new ArrayList<>();

        public AssetItem(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<AssetItem> getChildren() {
            return children;
        }

        public void addChild(AssetItem child) {
            children.add(child);
        }
    }
}