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

@Route(value = "test", layout = MainLayout.class)
@PageTitle("test")
@JavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js")
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
public class TestPage extends HorizontalLayout {
    private final TreeGrid<AssetItem> tree;
    private final TreeDataProvider<AssetItem> dataProvider;
    private final TextField filterField;
    private final VerticalLayout detailsLayout;
    private List<AssetItem> rootItems;

    public TestPage() {
        
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
            .setHeader("Rules")
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
        // addSection(leftContent, "When");
        // TextArea notes = new TextArea();
        // notes.setLabel("Notes");
        // notes.setWidthFull();
        // notes.setHeight("200px");
        // notes.getStyle()
        //     .set("background-color", "var(--lumo-contrast-5pct)")
        //     .set("border-radius", "var(--lumo-border-radius-m)");
        // notes.setValue(summarizeAssetItem(item));  // <- Add this line

        //addFieldWithTimestamp(leftContent, notes);

        // ATTRIBUTES Section
        addSection(leftContent, "ATTRIBUTES");
        createDynamicFields(item, leftContent);

        // Right side - LOCATION
        VerticalLayout rightContent = new VerticalLayout();
        rightContent.setSpacing(false);
        rightContent.setPadding(false);
        rightContent.setWidth("50%");

        // LOCATION Section
        addSection(rightContent, "Then");
        createThenFields(item, rightContent);

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
        // consoles.addChild(new AssetItem("Chrome"));
        // consoles.addChild(new AssetItem("Chrome"));
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

        String apiUrl = "https://s6-api-gateway.onrender.com/rules";
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
                    Object nameValue = apiData.get("asset_name");
                    if (nameValue instanceof String) { // Check if 'name' exists and is a String
                        String name = (String) nameValue;
                        if (!name.isEmpty()) {
                            // Create a new AssetItem and add it directly to the main list
                            AssetItem item = new AssetItem(name);
                            item.asset_id = (Integer) apiData.get("asset_id");
                            item.asset_type = (Boolean) apiData.get("asset_type");
                            item.when_attribute = (String) apiData.get("when_attribute");
                            item.when_operator = (String) apiData.get("when_operator");
                            item.when_value = (String) apiData.get("when_value");
                            item.then_attribute = (String) apiData.get("then_attribute");
                            item.then_value = (String) apiData.get("then_value");

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
        public Integer asset_id;  // Nullable Integer
        public Boolean asset_type; // Nullable Boolean
        public String when_attribute; // Nullable String
        public String when_operator; // Nullable String
        public String when_value; // Nullable String
        public String then_attribute; // Nullable String
        public String then_value; // Nullable String
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
    
        summary.append("This rule is related to asset: ").append(item.name).append(".");

        if (item.asset_type != null) {
            summary.append(" The asset type is ").append(item.asset_type).append(".");
        }
    
        if (item.asset_id != null) {
            summary.append(" Asset ID is ").append(item.asset_id).append(".");
        }
    
        if (item.when_attribute != null && item.when_operator != null && item.when_value != null) {
            summary.append(" When ").append(item.when_attribute)
                   .append(" ").append(item.when_operator)
                   .append(" ").append(item.when_value).append(",");
        }
    
        return summary.toString();
    }

    public static class Rule {
        // All properties are nullable and public
        public Integer asset_id;  // Nullable Integer
        public String asset_name; // Nullable String
        public Boolean asset_type; // Nullable Boolean
        public String when_attribute; // Nullable String
        public String when_operator; // Nullable String
        public String when_value; // Nullable String
        public String then_attribute; // Nullable String
        public String then_value; // Nullable String
    
        // Constructor
        public Rule(String asset_name) {
            this.asset_name = asset_name;
        }

        public String getName() {
            return asset_name;
        }
    }

    public void createDynamicFields(Object item, VerticalLayout leftContent) {
        // Get all fields from the item's class
        Field[] fields = item.getClass().getDeclaredFields();
    
        // Loop through all fields and create text fields dynamically
        for (Field field : fields) {
            field.setAccessible(true);  // Make private fields accessible
    
            String fieldName = field.getName();
    
            // Skip 'then_attribute' and 'then_value'
            if ("then_attribute".equals(fieldName) || "then_value".equals(fieldName) 
            || "asset_id".equals(fieldName) || "name".equals(fieldName) || "asset_type".equals(fieldName) || "children".equals(fieldName)

            ) {
                continue;
            }
    
            try {
                // Get field value
                Object fieldValue = field.get(item);
    
                // Create text field or text area based on the field type
                String fieldLabel = capitalizeFirstLetter(fieldName);  // Capitalize the field name for display
                String fieldValueString = fieldValue != null ? fieldValue.toString() : "";
    
                if (field.getType().equals(String.class)) {
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(Boolean.class)) {
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(Integer.class)) {
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                } else if (field.getType().equals(List.class)) {
                    TextArea textArea = createTextArea(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textArea);
                }
    
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle any reflection exceptions
            }
        }
    }
    
    public void createThenFields(Object item, VerticalLayout leftContent) {
        // Get all fields from the item's class
        Field[] fields = item.getClass().getDeclaredFields();
    
        // Loop through all fields and create components for "then" fields only
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
    
            // Only include 'then_attribute' and 'then_value'
            if (!"then_attribute".equals(fieldName) && !"then_value".equals(fieldName)) {
                continue;
            }
    
            try {
                Object fieldValue = field.get(item);
                String fieldLabel = capitalizeFirstLetter(fieldName);
                String fieldValueString = fieldValue != null ? fieldValue.toString() : "";
    
                if (field.getType().equals(String.class)) {
                    TextField textField = createTextField(fieldLabel, fieldValueString, false);
                    addFieldWithTimestamp(leftContent, textField);
                }
    
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle reflection errors
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