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

@Route("rules")
@PageTitle("Rules")
public class RulesView extends Div {
    public RulesView() {
        setSizeFull();
        
        // Sidebar - List of rules
        Grid<String> ruleList = new Grid<>();
        ruleList.addColumn(String::toString).setHeader("Rules");
        ruleList.setItems("Irrigation flow total", "Irrigation tank low", "KPI: Flow per m2", "Salinity < 3", "Salinity > 25", "Salinity 20 < 25");
        ruleList.setHeightFull();
        
        Button addRuleButton = new Button("+");
        
        VerticalLayout sidebar = new VerticalLayout(new Div("Rules"), ruleList, addRuleButton);
        sidebar.setWidth("300px");
        sidebar.setHeightFull();

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
        
        HorizontalLayout mainLayout = new HorizontalLayout(sidebar, formLayout);
        mainLayout.setSizeFull();

        add(mainLayout);
    }
}
