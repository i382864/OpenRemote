package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "assets", layout = MainLayout.class) // Hier linken we de pagina aan MainLayout
@PageTitle("Assets")
public class AssetsView extends VerticalLayout {

    public AssetsView() {
        // Zoek/filter invoerveld
        TextField filterField = new TextField();
        filterField.setPlaceholder("Filter...");

        // TreeGrid voor de assets
        TreeGrid<AssetItem> treeGrid = new TreeGrid<>();
        treeGrid.addHierarchyColumn(AssetItem::getName).setHeader("Assets");

        // Root items
        AssetItem consoles = new AssetItem("Consoles");
        AssetItem greenEquipment = new AssetItem("GreenEquipment Distribution");
        AssetItem greenhouse = new AssetItem("High-Tech Greenhouse Distribution");
        AssetItem simulator = new AssetItem("Simulator");

        // Data structuur
        List<AssetItem> rootItems = new ArrayList<>();
        rootItems.add(consoles);
        rootItems.add(greenEquipment);
        rootItems.add(greenhouse);
        rootItems.add(simulator);

        // Stel de TreeGrid items in
        treeGrid.setItems(rootItems, AssetItem::getChildren);

        // Layout opbouw
        add(filterField, treeGrid);
        treeGrid.setHeightFull(); // Make it take full available height
        treeGrid.setWidthFull();  // Make it take full available width
        setSizeFull(); // Ensure the whole layout fills the parent container
    }

    // Custom class voor asset items
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