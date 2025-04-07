package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;
import java.util.ArrayList;

@Layout
public final class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final VerticalLayout sidebarContent;
    private final Span taskListLabel;
    private final TreeGrid<AssetsView.AssetItem> assetTreeGrid;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);

        // Sidebar content (defaults to Task List)
        sidebarContent = new VerticalLayout();
        taskListLabel = new Span("Task List");
        sidebarContent.add(taskListLabel);

        // TreeGrid for assets (used only on Assets page)
        assetTreeGrid = new TreeGrid<>();
        assetTreeGrid.addHierarchyColumn(AssetsView.AssetItem::getName).setHeader("Assets");

        // Add elements to the layout
        addToNavbar(createMenu());
        addToDrawer(createHeader(), new Scroller(createSideNav()), sidebarContent, createUserMenu());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String route = event.getLocation().getPath();
        sidebarContent.removeAll(); // Clear previous content

        getUI().ifPresent(ui -> {
            if ("assets".equals(route)) {
                sidebarContent.add(assetTreeGrid); // Show TreeGrid on Assets page
            } else {
                sidebarContent.add(taskListLabel); // Default TaskList on other pages
            }
        });
    }

    private Div createHeader() {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames("text-primary", "icon-size-large");

        var appName = new Span("OpenRemote");
        appName.addClassNames("font-semibold", "font-size-large");

        var header = new Div(appLogo, appName);
        header.addClassNames("display-flex", "padding-medium", "gap-medium", "align-items-center");
        return header;
    }

    private Component createTreeGrid() {
        TreeGrid<AssetsView.AssetItem> treeGrid = new TreeGrid<>();
        treeGrid.addHierarchyColumn(AssetsView.AssetItem::getName).setHeader("Assets");

        // Root items
        AssetsView.AssetItem consoles = new AssetsView.AssetItem("Consoles");
        AssetsView.AssetItem greenEquipment = new AssetsView.AssetItem("GreenEquipment Distribution");
        AssetsView.AssetItem greenhouse = new AssetsView.AssetItem("High-Tech Greenhouse Distribution");
        AssetsView.AssetItem simulator = new AssetsView.AssetItem("Simulator");

        // Data structure
        List<AssetsView.AssetItem> rootItems = new ArrayList<>();
        rootItems.add(consoles);
        rootItems.add(greenEquipment);
        rootItems.add(greenhouse);
        rootItems.add(simulator);

        treeGrid.setItems(rootItems, AssetsView.AssetItem::getChildren);
        treeGrid.setHeightFull();
        treeGrid.setWidthFull();

        return treeGrid;
    }

    private Component createSideNav() {
        var nav = new SideNav();
        nav.addClassNames("margin-horizontal-medium");
    
        // Add menu items
        // Placeholder logic - replace with dynamic menu items
        nav.addItem(new SideNavItem("Home"));
        nav.addItem(new SideNavItem("Dashboard"));
    
        // Safely get the route
        String route = getUI().map(ui -> ui.getInternals().getActiveViewLocation().getPath()).orElse("");
    
        if ("assets".equals(route)) {
            return createTreeGrid(); // Show TreeGrid on Assets page
        } else {
            return new Span("Task List"); // Default TaskList on other pages
        }
    }

    private Component createUserMenu() {
        var avatar = new Avatar("Sarvin Satchithanantham");
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames("margin-right-small");
        avatar.setColorIndex(5);

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames("margin-medium");

        var userMenuItem = userMenu.addItem(avatar);
        userMenuItem.add("Sarvin Satchithanantham");
        userMenuItem.getSubMenu().addItem("View Profile");
        userMenuItem.getSubMenu().addItem("Manage Settings");
        userMenuItem.getSubMenu().addItem("Logout");

        return userMenu;
    }

    private Component createMenu() {
        Tab mapTab = new Tab(new RouterLink("Map", MapView.class));
        Tab assetsTab = new Tab(new RouterLink("Assets", AssetsView.class));
        Tab rulesTab = new Tab(new RouterLink("Rules", RulesView.class));
        Tab insightsTab = new Tab(new RouterLink("Insights", InsightsView.class));

        Tabs menuTabs = new Tabs(mapTab, assetsTab, rulesTab, insightsTab);
        menuTabs.addClassNames("padding-medium");

        Icon bellIcon = VaadinIcon.BELL.create();
        bellIcon.setSize("30px");
        bellIcon.getStyle().set("cursor", "pointer");

        Button notificationButton = new Button(bellIcon, click -> {
            Notification.show("No new notifications", 3000, Notification.Position.TOP_CENTER);
        });
        notificationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout menuLayout = new HorizontalLayout(menuTabs, notificationButton);
        menuLayout.setWidthFull();
        menuLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        return menuLayout;
    }
}