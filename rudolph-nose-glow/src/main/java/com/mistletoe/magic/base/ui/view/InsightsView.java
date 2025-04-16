package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import java.util.List;

@Route(value = "insights", layout = MainLayout.class)
@PageTitle("Insights")
public class InsightsView extends Div {
    private final VerticalLayout rightSide;
    private final VerticalLayout placeholderLayout;
    private final VerticalLayout dashboardContent;
    private final VerticalLayout dashboardCreationLayout;
    private final HorizontalLayout mainLayout;

    public InsightsView() {
        setSizeFull();

        // Left side with dashboards list
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

        H3 title = new H3("Insights");
        title.getStyle()
            .set("color", "var(--lumo-base-color)")
            .set("font-size", "var(--lumo-font-size-l)")
            .set("font-weight", "500")
            .set("margin", "0");

        // Add plus icon
        Icon addIcon = VaadinIcon.PLUS.create();
        addIcon.setSize("24px");
        addIcon.getStyle()
            .set("color", "var(--lumo-base-color)")
            .set("margin-left", "auto")
            .set("cursor", "pointer");

        header.add(title, addIcon);
        leftSide.add(header);

        // Add "My dashboards" section
        H4 sectionTitle = new H4("My dashboards");
        sectionTitle.getStyle()
            .set("margin", "var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-s)")
            .set("font-size", "var(--lumo-font-size-s)")
            .set("font-weight", "500")
            .set("color", "var(--lumo-secondary-text-color)");
        leftSide.add(sectionTitle);

        // Initialize right side layouts
        rightSide = new VerticalLayout();
        rightSide.setSizeFull();
        rightSide.setPadding(true);
        rightSide.setSpacing(true);

        // Create placeholder layout
        placeholderLayout = new VerticalLayout();
        placeholderLayout.setSizeFull();
        placeholderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        placeholderLayout.setAlignItems(Alignment.CENTER);
        
        H3 placeholderText = new H3("Please select a dashboard on the left");
        placeholderText.getStyle()
            .set("color", "var(--lumo-secondary-text-color)")
            .set("font-weight", "400");
        placeholderLayout.add(placeholderText);

        // Create dashboard content layout
        dashboardContent = createDashboardContent();
        dashboardContent.setVisible(false);

        // Create dashboard creation layout
        dashboardCreationLayout = createDashboardCreationLayout();
        dashboardCreationLayout.setVisible(false);

        rightSide.add(placeholderLayout, dashboardContent, dashboardCreationLayout);

        // Add dashboard items with click listener
        addDashboardItem(leftSide, "Harvesting dashboard", () -> {
            placeholderLayout.setVisible(false);
            dashboardContent.setVisible(true);
            dashboardCreationLayout.setVisible(false);
        });

        // Add click listener to plus icon
        addIcon.addClickListener(e -> {
            placeholderLayout.setVisible(false);
            dashboardContent.setVisible(false);
            dashboardCreationLayout.setVisible(true);
        });

        // Main layout
        mainLayout = new HorizontalLayout(leftSide, rightSide);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);

        add(mainLayout);
    }

    private VerticalLayout createDashboardCreationLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(false);

        // Top bar with name field and buttons
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setSpacing(true);
        topBar.setPadding(false);
        topBar.setAlignItems(Alignment.CENTER);

        TextField nameField = new TextField();
        nameField.setPlaceholder("New Dashboard");
        nameField.setWidth("300px");

        HorizontalLayout buttonGroup = new HorizontalLayout();
        buttonGroup.setSpacing(true);
        
        Icon refreshIcon = VaadinIcon.REFRESH.create();
        refreshIcon.setSize("20px");
        refreshIcon.getStyle().set("cursor", "pointer");
        
        Icon desktopIcon = VaadinIcon.DESKTOP.create();
        desktopIcon.setSize("20px");
        desktopIcon.getStyle().set("cursor", "pointer");
        
        Icon editIcon = VaadinIcon.EDIT.create();
        editIcon.setSize("20px");
        editIcon.getStyle().set("cursor", "pointer");

        Button saveButton = new Button("SAVE");
        saveButton.getStyle()
            .set("background-color", "var(--lumo-contrast-10pct)")
            .set("color", "var(--lumo-body-text-color)")
            .set("font-weight", "500");

        Button viewButton = new Button("VIEW");
        viewButton.getStyle()
            .set("background-color", "var(--lumo-primary-color)")
            .set("color", "var(--lumo-base-color)")
            .set("font-weight", "500");

        buttonGroup.add(refreshIcon, desktopIcon, editIcon, saveButton, viewButton);
        topBar.add(nameField, buttonGroup);

        // Main content area with grid background
        Div gridArea = new Div();
        gridArea.setSizeFull();
        gridArea.getStyle()
            .set("background-image", "linear-gradient(var(--lumo-contrast-10pct) 1px, transparent 1px), linear-gradient(90deg, var(--lumo-contrast-10pct) 1px, transparent 1px)")
            .set("background-size", "20px 20px")
            .set("background-position", "center center")
            .set("border", "1px solid var(--lumo-contrast-20pct)")
            .set("margin-top", "var(--lumo-space-m)")
            .set("flex-grow", "1");

        // Widgets panel on the right
        VerticalLayout widgetsPanel = new VerticalLayout();
        widgetsPanel.setWidth("30%");
        widgetsPanel.setPadding(true);
        widgetsPanel.setSpacing(true);

        // Add tabs for WIDGETS and SETTINGS
        Tab widgetsTab = new Tab("WIDGETS");
        Tab settingsTab = new Tab("SETTINGS");
        Tabs tabs = new Tabs(widgetsTab, settingsTab);
        tabs.setWidthFull();

        // Add widget options
        addWidgetOption(widgetsPanel, "Attribute", VaadinIcon.CONNECT_O);
        addWidgetOption(widgetsPanel, "Gateway", VaadinIcon.DESKTOP);
        addWidgetOption(widgetsPanel, "Gauge", VaadinIcon.DASHBOARD);
        addWidgetOption(widgetsPanel, "Image", VaadinIcon.PICTURE);
        addWidgetOption(widgetsPanel, "KPI", VaadinIcon.RECORDS);
        addWidgetOption(widgetsPanel, "Line Chart", VaadinIcon.TRENDING_UP);
        addWidgetOption(widgetsPanel, "Map", VaadinIcon.MAP_MARKER);
        addWidgetOption(widgetsPanel, "Table", VaadinIcon.GRID_SMALL);

        // Main content with grid and widgets panel
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        
        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.setWidth("30%");
        rightPanel.setSpacing(false);
        rightPanel.setPadding(false);
        rightPanel.add(tabs, widgetsPanel);
        
        contentLayout.add(gridArea, rightPanel);
        contentLayout.setFlexGrow(0.7, gridArea);
        contentLayout.setFlexGrow(0.3, rightPanel);
        
        layout.add(topBar, contentLayout);
        return layout;
    }

    private void addWidgetOption(VerticalLayout container, String name, VaadinIcon iconType) {
        HorizontalLayout option = new HorizontalLayout();
        option.setWidthFull();
        option.setHeight("44px");
        option.setPadding(true);
        option.setSpacing(true);
        option.setAlignItems(Alignment.CENTER);
        option.getStyle()
            .set("cursor", "pointer")
            .set("border-radius", "var(--lumo-border-radius-m)")
            .set("transition", "background-color 0.1s ease-in-out");

        Icon icon = iconType.create();
        icon.setSize("24px");
        
        Div text = new Div();
        text.setText(name);
        text.getStyle().set("font-size", "var(--lumo-font-size-m)");

        option.add(icon, text);
        
        // Add hover effect
        option.getElement().addEventListener("mouseover", 
            e -> option.getStyle().set("background-color", "var(--lumo-contrast-5pct)"));
        option.getElement().addEventListener("mouseout", 
            e -> option.getStyle().set("background-color", "transparent"));

        container.add(option);
    }

    private VerticalLayout createDashboardContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);

        // Charts layout
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        
        // Flow Chart
        Chart flowChart = createFlowChart();
        flowChart.setWidth("50%");
        
        // Speed Gauge
        Chart speedGauge = createSpeedGauge();
        speedGauge.setWidth("50%");

        chartsLayout.add(flowChart, speedGauge);
        
        // Table
        Grid<HarvestRobot> grid = createHarvestTable();
        grid.setHeight("400px");

        content.add(chartsLayout, grid);
        return content;
    }

    private void addDashboardItem(VerticalLayout container, String name, Runnable onClick) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setHeight("44px");
        item.setPadding(true);
        item.setSpacing(true);
        item.setAlignItems(Alignment.CENTER);
        item.getStyle()
            .set("padding", "0 var(--lumo-space-m)")
            .set("cursor", "pointer");

        // Add hover effect
        item.getElement().getStyle()
            .set("transition", "background-color 0.1s ease-in-out")
            .set("border-radius", "0");
        item.getElement().addEventListener("mouseover", 
            e -> item.getStyle().set("background-color", "var(--lumo-contrast-5pct)"));
        item.getElement().addEventListener("mouseout", 
            e -> item.getStyle().set("background-color", "transparent"));

        // Add click listener
        item.addClickListener(e -> onClick.run());

        Div text = new Div();
        text.setText(name);
        text.getStyle()
            .set("color", "var(--lumo-body-text-color)")
            .set("font-size", "var(--lumo-font-size-m)");

        item.add(text);
        container.add(item);
    }

    // Method to create the Flow Chart
    private Chart createFlowChart() {
        Chart chart = new Chart(ChartType.LINE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Total Flow (Last 24 Hours)");

        XAxis x = new XAxis();
        x.setCategories("13:00", "16:00", "19:00", "22:00", "01:00", "04:00", "07:00", "10:00");
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Flow total (l/h)");
        conf.addyAxis(y);

        ListSeries series = new ListSeries("Irrigation 1", 0.2, 0.5, 0.3, 0.6, 0.4, 0.7, 0.8, 1.0);
        conf.addSeries(series);

        return chart;
    }

    // Method to create the Speed Gauge
    private Chart createSpeedGauge() {
        Chart chart = new Chart(ChartType.GAUGE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Harvest Robot Speed");

        ListSeries series = new ListSeries(2); // Speed in km/h
        conf.addSeries(series);

        return chart;
    }

    // Method to create the Harvest Summary Table
    private Grid<HarvestRobot> createHarvestTable() {
        Grid<HarvestRobot> grid = new Grid<>(HarvestRobot.class, false);
        grid.addColumn(HarvestRobot::getAssetName).setHeader("Asset name").setSortable(true);
        grid.addColumn(HarvestRobot::getHarvestedTotal).setHeader("Harvested total");
        grid.addColumn(HarvestRobot::getVegetableType).setHeader("Vegetable type");
        grid.addColumn(HarvestRobot::getSpeed).setHeader("Speed (km/h)");
        grid.addColumn(HarvestRobot::getHarvestedSession).setHeader("Harvested session");

        List<HarvestRobot> robots = List.of(
            new HarvestRobot("Robot 1", 45, "BELL_PEPPER", null, null),
            new HarvestRobot("Robot 2", 45, "BELL_PEPPER", null, null),
            new HarvestRobot("Harvest Robot 1", 45, "TOMATO", 8, 11),
            new HarvestRobot("Harvest", 45, "TOMATO", null, null),
            new HarvestRobot("Harvester", 45, "TOMATO", null, null)
        );

        grid.setItems(robots);
        return grid;
    }

    // Data class for Harvest Robots
    public static class HarvestRobot {
        private String assetName;
        private int harvestedTotal;
        private String vegetableType;
        private Integer speed;
        private Integer harvestedSession;

        public HarvestRobot(String assetName, int harvestedTotal, String vegetableType, Integer speed, Integer harvestedSession) {
            this.assetName = assetName;
            this.harvestedTotal = harvestedTotal;
            this.vegetableType = vegetableType;
            this.speed = speed;
            this.harvestedSession = harvestedSession;
        }

        public String getAssetName() { return assetName; }
        public int getHarvestedTotal() { return harvestedTotal; }
        public String getVegetableType() { return vegetableType; }
        public Integer getSpeed() { return speed; }
        public Integer getHarvestedSession() { return harvestedSession; }
    }
}