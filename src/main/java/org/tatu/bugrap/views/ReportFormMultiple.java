package org.tatu.bugrap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;

public class ReportFormMultiple extends VerticalLayout
{
    Binder<Report> binder = new BeanValidationBinder<>(Report.class);
    H3 title = new H3("");
    private List<Report> reports;

    TextArea description = new TextArea("");
    Button openBtn = new Button("Open");
    ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    ComboBox<Report.Type> type = new ComboBox<>("Type");
    ComboBox<Report.Status> status = new ComboBox<>("Status");
    ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");

    Button save = new Button("Save Changes");
    Button revert = new Button("Revert");

    public ReportFormMultiple(List<Reporter> reporters, List<ProjectVersion> versions) {
        binder.bindInstanceFields(this);
        priority.setItems(Report.Priority.values());
        status.setItems(Report.Status.values());
        type.setItems(Report.Type.values());
        assigned.setItems(reporters);
        assigned.setItemLabelGenerator(Reporter::getName);
        version.setItems(versions);
        version.setItemLabelGenerator(ProjectVersion::getVersion);

        add(title);
        HorizontalLayout buttons = new HorizontalLayout(createButtonLayout());
        HorizontalLayout layout = new HorizontalLayout( priority,
                type,
                status,
                assigned,
                version,
                buttons
        );
        layout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        layout.setWidthFull();
        buttons.getElement().getStyle().set("margin-left", "auto");
        add(layout);
    }

    private Component createButtonLayout(){
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        revert.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
        revert.addClickShortcut(Key.ESCAPE);
        HorizontalLayout layout = new HorizontalLayout(save,revert);
        return layout;
    }

    public void setTitle(String s)
    {
        title.removeAll();
        title.add(s);
    }

}
