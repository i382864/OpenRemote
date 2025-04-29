package com.mistletoe.magic.base.ui.view;
import java.lang.reflect.Field;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Assets.Asset;
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
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Needed for generic JSON parsing
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mistletoe.magic.base.ui.view.AssetsView.AssetItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List; // Good practice to use the interface type

import java.util.Objects;

@Route(value = "assets", layout = MainLayout.class)
@PageTitle("Assets")
@JavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js")
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
public class AssetsView extends HorizontalLayout {
    private final TreeGrid<AssetItem> tree;
    private final TreeDataProvider<AssetItem> dataProvider;
    private final TextField filterField;
    private final VerticalLayout detailsLayout;
    private List<AssetItem> rootItems;

    public AssetsView() {
        
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
        rootItems = getAssetItems();
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
        
        // Right side - Details view
        detailsLayout = new VerticalLayout();
        detailsLayout.setWidthFull();
        detailsLayout.setPadding(true);
        detailsLayout.setSpacing(true);

        // Add both sides to main layout
        add(leftSide, detailsLayout);
        setFlexGrow(1, detailsLayout);
    }

    private void showDetails(AssetItem item) {
        detailsLayout.removeAll();
        detailsLayout.setSpacing(false);
        detailsLayout.setPadding(true);

        // Only show details if item is Chrome
        if ("Chrome".equals(item.getName()) || "Consoles".equals(item.getName())) {
            detailsLayout.add(new Div("Please select an asset on the left"));
            return;
        }

        // Header with icon and name
        HorizontalLayout header = new HorizontalLayout();
        Icon icon = VaadinIcon.DESKTOP.create();
        icon.setSize("16px");
        H2 title = new H2(item.getName());
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "var(--lumo-font-size-m)");
        header.add(icon, title);
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "0.5em");
        
        detailsLayout.add(header);

        // Create main content layout that will contain left and right sections
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);
        mainContent.setPadding(false);

        // Left side - INFO and ATTRIBUTES
        VerticalLayout leftContent = new VerticalLayout();
        leftContent.setSpacing(false);
        leftContent.setPadding(false);
        leftContent.setWidth("50%");

        // INFO Section
        addSection(leftContent, "INFO");
        TextArea notes = new TextArea();
        notes.setLabel("Notes");
        notes.setWidthFull();
        notes.setHeight("200px");
        notes.getStyle()
            .set("background-color", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)");
        notes.setValue(summarizeAssetItem(item));  // <- Add this line

        addFieldWithTimestamp(leftContent, notes);

        // ATTRIBUTES Section
        addSection(leftContent, "ATTRIBUTES");
        createDynamicFields(item, leftContent);

        // TextField assetName = createTextField("Name", item.name, false);
        // addFieldWithTimestamp(leftContent, assetName);

        // TextField assetType = createTextField("Asset Type", item.asset_type != null ? item.asset_type.toString() : "", false);
        // addFieldWithTimestamp(leftContent, assetType);

        // TextField assetAssetId = createTextField("Asset ID", item.asset_id != null ? item.asset_id.toString() : "", false);
        // addFieldWithTimestamp(leftContent, assetAssetId);

        // TextField assetCountry = createTextField("Country", item.country != null ? item.country : "", false);
        // addFieldWithTimestamp(leftContent, assetCountry);

        // TextField assetLocation = createTextField("Location", item.location != null ? item.location : "", false);
        // addFieldWithTimestamp(leftContent, assetLocation);

        // TextArea assetNotes = createTextArea("Notes", item.notes != null ? item.notes : "", false);
        // addFieldWithTimestamp(leftContent, assetNotes);

        // TextField assetCity = createTextField("City", item.city != null ? item.city : "", false);
        // addFieldWithTimestamp(leftContent, assetCity);

        // TextField assetEmail = createTextField("Email", item.email != null ? item.email : "", false);
        // addFieldWithTimestamp(leftContent, assetEmail);

        // TextField assetManufacturer = createTextField("Manufacturer", item.manufacturer != null ? item.manufacturer : "", false);
        // addFieldWithTimestamp(leftContent, assetManufacturer);

        // TextField assetModel = createTextField("Model", item.model != null ? item.model : "", false);
        // addFieldWithTimestamp(leftContent, assetModel);

        // TextField assetRegion = createTextField("Region", item.region != null ? item.region : "", false);
        // addFieldWithTimestamp(leftContent, assetRegion);

        // TextField assetTags = createTextField("Tags", item.tags != null ? item.tags : "", false);
        // addFieldWithTimestamp(leftContent, assetTags);

        // Right side - LOCATION
        VerticalLayout rightContent = new VerticalLayout();
        rightContent.setSpacing(false);
        rightContent.setPadding(false);
        rightContent.setWidth("50%");

        // LOCATION Section
        addSection(rightContent, "LOCATION");
        Div map = new Div();
        map.setId("assetMap"); // Give the map div a specific ID
        map.setHeight("400px");
        map.setWidthFull();
        map.getStyle()
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("margin-bottom", "var(--lumo-space-m)");
        rightContent.add(map);

        // Initialize the map using JavaScript
        map.getElement().executeJs("""
        // Generate random latitude and longitude within a range
        function getRandomCoordinate(min, max) {
            return (Math.random() * (max - min) + min);
        }

        // Randomize the coordinates (latitude between 40 and 60, longitude between -10 and 10)
        const randomLat = getRandomCoordinate(40, 60);  // Random latitude between 40 and 60
        const randomLon = getRandomCoordinate(-10, 10);  // Random longitude between -10 and 10
        
        const map = L.map('assetMap', {
            zoomControl: false  // Disable default zoom control
        }).setView([randomLat, randomLon], 13);  // Use randomized coordinates for initial view

        // Use CartoDB Positron style for a cleaner look
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
        
        // Add custom CSS for zoom controls
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
        `;
        document.head.appendChild(style);
        
        // Update map size when container size changes
        setTimeout(function() {
            map.invalidateSize();
        }, 100);
    """);

        // Add left and right content to main layout
        mainContent.add(leftContent, rightContent);

        // Add the main content to the details layout
        detailsLayout.add(mainContent);
    }

    private TextField createTextField(String label, String value, boolean addChevron) {
        TextField field = new TextField();
        field.setLabel(label);
        field.setValue(value);
        field.setHeight("44px");
        
        // Style the field
        field.getStyle()
            .set("background", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("--vaadin-input-field-border-width", "0")
            .set("--vaadin-text-field-default-width", "100%")
            .set("margin", "0")
            .set("--lumo-text-field-size", "var(--lumo-size-s)")
            // Style the label using CSS custom properties
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
            
            Div wrapper = new Div(field, chevron);
            wrapper.getStyle().set("position", "relative");
            wrapper.setWidthFull();
            
            return field;
        }
        
        return field;
    }

    private TextArea createTextArea(String label, String value, boolean addChevron) {
        TextArea area = new TextArea();
        area.setLabel(label);
        area.setValue(value);
        area.setHeight(label.equals("Console providers") ? "120px" : "44px");
        
        // Style the field
        area.getStyle()
            .set("background", "var(--lumo-contrast-5pct)")
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("--vaadin-input-field-border-width", "0")
            .set("--vaadin-text-field-default-width", "100%")
            .set("margin", "0")
            .set("--lumo-text-field-size", "var(--lumo-size-s)")
            // Style the label using CSS custom properties
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
            
            Div wrapper = new Div(area, chevron);
            wrapper.getStyle().set("position", "relative");
            wrapper.setWidthFull();
            
            return area;
        }
        
        return area;
    }

    private void addSection(VerticalLayout container, String title) {
        H3 header = new H3(title);
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
            .set("margin", "4px 0 var(--lumo-space-m) 0");
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
        //AssetItem consoles = new AssetItem("Consoles");
        // AssetItem greenEquipment = new AssetItem("GreenEquipment Distribution");
        // AssetItem highTech = new AssetItem("High-Tech Greenhouse Distribution");
        // AssetItem simulator = new AssetItem("Simulator");

        // Add child items to Consoles
        //consoles.addChild(new AssetItem("Chrome"));
        //consoles.addChild(new AssetItem("Chrome"));
        // consoles.addChild(new AssetItem("Firefox"));
        // consoles.addChild(new AssetItem("Safari"));

        // Add child items to GreenEquipment Distribution
        // greenEquipment.addChild(new AssetItem("Paprika Perfect BV"));
        // greenEquipment.addChild(new AssetItem("Qoreau Horticulture"));
        // greenEquipment.addChild(new AssetItem("Vegetables & More"));

        // Add child items to High-Tech Greenhouse Distribution
        // highTech.addChild(new AssetItem("Haanen Vegetables BV"));
        // highTech.addChild(new AssetItem("RTD Vegetable Grower"));

        // Add all root items to the list
        //items.add(consoles);
        // items.add(greenEquipment);
        // items.add(highTech);
        // items.add(simulator);

        String apiUrl = "https://s6-api-gateway.onrender.com/assets";
        //String apiUrl = "http://34.29.191.54/asset";
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson JSON mapper
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            // Send request and get response (synchronous/blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Parse JSON into a List of Maps (String key, Object value)
                // This avoids creating a dedicated class like ApiAsset
                List<Map<String, Object>> apiDataList = objectMapper.readValue(responseBody,
                        new TypeReference<List<Map<String, Object>>>() {});

                // Process the data from the API
                for (Map<String, Object> apiData : apiDataList) {
                    // Extract the 'name' field
                    Object nameValue = apiData.get("name");
                    if (nameValue instanceof String) { // Check if 'name' exists and is a String
                        String name = (String) nameValue;
                        if (!name.isEmpty()) {
                            // Create a new AssetItem and add it directly to the main list
                            AssetItem item = new AssetItem(name);
                            //item.id = (List<Integer>) apiData.get("id");
                            item.parent = null; // assuming no parent object is resolved from API
                            item.asset_type = (Boolean) apiData.get("asset_type");
                            item.asset_id = (Integer) apiData.get("asset_id");
                            item.country = (String) apiData.get("country");
                            item.location = (String) apiData.get("location");
                            item.notes = (String) apiData.get("notes");
                            item.city = (String) apiData.get("city");
                            item.email = (String) apiData.get("email");
                            item.manufacturer = (String) apiData.get("manufacturer");
                            item.model = (String) apiData.get("model");
                            item.region = (String) apiData.get("region");
                            item.tags = (String) apiData.get("tags");

                            items.add(item);
                        }
                    } else {
                         System.err.println("API item found without a valid 'name' string: " + apiData);
                    }
                }
                System.out.println("Successfully fetched and added " + (apiDataList != null ? apiDataList.size() : 0) + " items from API.");

            } else {
                // Handle non-200 responses
                System.err.println("Error fetching data from API. Status code: " + response.statusCode());
                // Consider logging response.body() for debugging if needed
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON response from API: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            // Handle network errors or interruption
            System.err.println("Error during API call: " + e.getMessage());
            // Restore interrupt status if needed
             Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("An unexpected error occurred while fetching API data: " + e.getMessage());
        }
        // --- END OF NEW CODE BLOCK ---

        return items;
    }

    public static class AssetItem {
        private final String name;
        public List<Integer> id;
        public AssetItem parent;
        public Boolean asset_type;
        public Integer asset_id;
        public String country;
        public String location;
        public String notes;
        public String city;
        public String email;
        public String manufacturer;
        public String model;
        public String region;
        public String tags;
        public final List<AssetItem> children = new ArrayList<>();

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

    public static String summarizeAssetItem(AssetItem item) {
        StringBuilder summary = new StringBuilder();
    
        summary.append("This asset is called ").append(item.name).append(".");
    
        if (item.asset_type != null) {
            summary.append(" Its type flag is set to ").append(item.asset_type).append(".");
        }
    
        if (item.asset_id != null) {
            summary.append(" It has an ID of ").append(item.asset_id).append(".");
        }
    
        if (item.country != null) {
            summary.append(" It is located in ").append(item.country);
            if (item.city != null) {
                summary.append(", specifically in ").append(item.city);
            }
            if (item.location != null) {
                summary.append(" at ").append(item.location);
            }
            summary.append(".");
        }
    
        if (item.notes != null) {
            summary.append(" Notes: ").append(item.notes).append(".");
        }
    
        if (item.email != null) {
            summary.append(" You can contact the responsible party at ").append(item.email).append(".");
        }
    
        if (item.manufacturer != null || item.model != null) {
            summary.append(" This asset was manufactured by ").append(item.manufacturer != null ? item.manufacturer : "an unknown company");
            if (item.model != null) {
                summary.append(", model ").append(item.model);
            }
            summary.append(".");
        }
    
        if (item.region != null) {
            summary.append(" It belongs to the ").append(item.region).append(" region.");
        }
    
        if (item.tags != null) {
            summary.append(" Tagged with: ").append(item.tags).append(".");
        }
    
        return summary.toString();
    }

    public void createDynamicFields(Object item, VerticalLayout leftContent) {
        // Get all fields from the AssetItem class
        Field[] fields = item.getClass().getDeclaredFields();
    
        // Loop through all fields and create text fields dynamically
        for (Field field : fields) {
            field.setAccessible(true);  // Make private fields accessible
            
            try {
                // Get field name and value
                String fieldName = field.getName();
                Object fieldValue = field.get(item);
    
                // Create text field or text area based on the field type
                String fieldLabel = capitalizeFirstLetter(fieldName);  // Capitalize the field name for display
                String fieldValueString = fieldValue != null ? fieldValue.toString() : "";
    
                if (field.getType().equals(String.class)) {
                    // Create TextField for String fields
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(Boolean.class)) {
                    // Create TextField for Boolean fields
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(Integer.class)) {
                    // Create TextField for Integer fields
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(List.class)) {
                    // Create TextArea for List fields (for example, tags)
                    TextArea textArea = createTextArea(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textArea);
                }
                // Add more cases as needed for other types like Date, Long, etc.
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle any reflection exceptions
            }
        }
    }
    
    // Utility method to capitalize the first letter of a string
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}