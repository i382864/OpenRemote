package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.server.StreamResource;

@Layout
public final class MainLayout extends AppLayout implements BeforeEnterObserver {

    public MainLayout() {
        addToNavbar(createMenu());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // No drawer logic needed anymore
    }

    private Component createMenu() {
        // Create logo
        StreamResource logoResource = new StreamResource("openremote-logo.svg",
            () -> getClass().getResourceAsStream("/images/openremote-logo.svg"));
        Image logo = new Image(logoResource, "OpenRemote");
        logo.setHeight("24px");
        logo.getStyle()
            .set("margin-right", "var(--lumo-space-m)")
            .set("margin-left", "var(--lumo-space-m)");

        Tab mapTab = new Tab(new RouterLink("Map", MapView.class));
        Tab assetsTab = new Tab(new RouterLink("Assets", AssetsView.class));
        Tab rulesTab = new Tab(new RouterLink("Rules", RulesView.class));
        Tab insightsTab = new Tab(new RouterLink("Insights", InsightsView.class));

        Tabs menuTabs = new Tabs(mapTab, assetsTab, rulesTab, insightsTab);
        menuTabs.getStyle().set("margin-right", "auto");

        Icon bellIcon = VaadinIcon.BELL.create();
        bellIcon.setSize("30px");
        bellIcon.getStyle().set("cursor", "pointer");

        Button notificationButton = new Button(bellIcon, click -> {
            Notification.show("No new notifications", 3000, Notification.Position.TOP_CENTER);
        });
        notificationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Create a layout for logo and tabs
        HorizontalLayout leftSide = new HorizontalLayout(logo, menuTabs);
        leftSide.setSpacing(false);
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout menuLayout = new HorizontalLayout(leftSide, notificationButton);
        menuLayout.setWidthFull();
        menuLayout.setSpacing(true);
        menuLayout.setPadding(false);
        menuLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        menuLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        return menuLayout;
    }
}