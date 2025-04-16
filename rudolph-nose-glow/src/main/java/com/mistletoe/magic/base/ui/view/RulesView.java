package com.mistletoe.magic.base.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

@Route(value = "rules", layout = MainLayout.class)
@PageTitle("Rules")
public class RulesView extends Div {
    private final Grid<String> ruleList;

    public RulesView() {
        setSizeFull();

        // Left side with grid
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

        H3 title = new H3("Rules");
        title.getStyle()
            .set("color", "var(--lumo-base-color)")
            .set("font-size", "var(--lumo-font-size-l)")
            .set("font-weight", "500")
            .set("margin", "0");

        // Create icons
        Icon duplicateIcon = VaadinIcon.COPY.create();
        Icon deleteIcon = VaadinIcon.TRASH.create();
        Icon addIcon = VaadinIcon.PLUS.create();
        Icon menuIcon = VaadinIcon.MENU.create();

        // Style all icons
        for (Icon icon : new Icon[]{duplicateIcon, deleteIcon, addIcon, menuIcon}) {
            icon.setSize("24px");
            icon.getStyle()
                .set("color", "var(--lumo-base-color)")
                .set("margin-left", "var(--lumo-space-s)")
                .set("cursor", "pointer");
        }

        // Create icons container
        HorizontalLayout icons = new HorizontalLayout(duplicateIcon, deleteIcon, addIcon, menuIcon);
        icons.setSpacing(false);
        icons.setAlignItems(Alignment.CENTER);
        icons.getStyle().set("margin-left", "auto");

        header.add(title, icons);
        leftSide.add(header);

        // Initialize grid
        ruleList = new Grid<>();
        ruleList.addColumn(String::toString).setHeader("Rules");
        ruleList.setItems("Irrigation flow total", "Irrigation tank low", "KPI: Flow per m2", 
                         "Salinity < 3", "Salinity > 25", "Salinity 20 < 25");
        ruleList.setHeightFull();
        ruleList.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        leftSide.add(ruleList);

        // Rule Details Form
        TextField ruleName = new TextField("Rule name*");
        Checkbox enabled = new Checkbox("Enabled");
        
        // Condition Section
        ComboBox<String> assetDropdown = new ComboBox<>("Asset");
        assetDropdown.setItems("Irrigation 3", "Irrigation 2");
        
        ComboBox<String> attributeDropdown = new ComboBox<>("Attribute");
        attributeDropdown.setItems("Tank level");
        
        ComboBox<String> operatorDropdown = new ComboBox<>("Operator");
        operatorDropdown.setItems("Less than", "Greater than", "Equal to");
        
        TextField valueField = new TextField("Value");
        
        VerticalLayout conditionLayout = new VerticalLayout(assetDropdown, attributeDropdown, operatorDropdown, valueField);
        conditionLayout.setPadding(false);

        // Action Section
        ComboBox<String> recipientDropdown = new ComboBox<>("Recipients");
        recipientDropdown.setItems("Custom attribute");
        
        TextField emailField = new TextField();
        emailField.setPlaceholder("test@test12345.com");
        
        VerticalLayout actionLayout = new VerticalLayout(recipientDropdown, emailField);
        actionLayout.setPadding(false);

        // Layout
        VerticalLayout formLayout = new VerticalLayout(ruleName, enabled, new Div("When..."), conditionLayout, new Div("Then..."), actionLayout);
        formLayout.setPadding(true);
        formLayout.setWidth("100%");
        
        HorizontalLayout mainLayout = new HorizontalLayout(leftSide, formLayout);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);

        add(mainLayout);
    }
}
