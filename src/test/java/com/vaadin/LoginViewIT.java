package com.vaadin;

import org.junit.Assert;
import org.junit.Test;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;


public class LoginViewIT extends AbstractViewTest {

    @Test
    public void loginAndSelect() {
        ButtonElement button = $(ButtonElement.class).first();

        button.click();

        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.selectByText("Project 2");

        // Check if the the value selected
        Assert.assertFalse(combobox.isSelected());

    }

}
