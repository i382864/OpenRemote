package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;

import java.util.List;

@Route("insights")
@PageTitle("Insights")
public class InsightsView extends VerticalLayout {

    public InsightsView() {
        addClassNames(LumoUtility.Padding.LARGE);
        
        // === CHARTS LAYOUT ===
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        
        // Flow Chart
        Chart flowChart = createFlowChart();
        
        // Speed Gauge
        Chart speedGauge = createSpeedGauge();

        chartsLayout.add(flowChart, speedGauge);
        
        // === TABLE ===
        Grid<HarvestRobot> grid = createHarvestTable();

        // Add components to the view
        add(chartsLayout, grid);
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