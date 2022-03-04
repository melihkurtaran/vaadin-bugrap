package org.tatu.bugrap.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.AfterNavigationHandler;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.annotation.security.PermitAll;
import java.util.Collections;


@PageTitle("Edit Report")
@Route(value = "Edit")
@PermitAll
public class SeparateEditView extends VerticalLayout implements AfterNavigationObserver
{
    H3 projectName = new H3("");
    H3 projectVersion = new H3("");
    H3 summary = new H3("");
    Report report;
    Binder<Report> binder = new BeanValidationBinder<>(Report.class);

    TextArea description = new TextArea("");
    ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    ComboBox<Report.Type> type = new ComboBox<>("Type");
    ComboBox<Report.Status> status = new ComboBox<>("Status");
    ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");
    Button save = new Button("Save Changes");
    Button revert = new Button("Revert");
    BugrapPresenter bugrapPresenter;

    public SeparateEditView(BugrapPresenter bugrapPresenter){
        this.bugrapPresenter = bugrapPresenter;

        priority.setItems(Report.Priority.values());
        status.setItems(Report.Status.values());
        type.setItems(Report.Type.values());
        assigned.setItems(Collections.EMPTY_LIST);
        assigned.setItemLabelGenerator(Reporter::getName);
        version.setItems(Collections.EMPTY_LIST);
        version.setItemLabelGenerator(ProjectVersion::getVersion);

        report = BugrapViewImpl.getSelectedReport();
        binder.readBean(report);

        projectName.setText(report.getProject().getName());
        summary.setText(report.getSummary());
        description.setValue(report.getDescription());
        priority.setValue(report.getPriority());
        status.setValue(report.getStatus());
        type.setValue(report.getType());


        binder.bindInstanceFields(this);

        if (report.getAssigned() == null)
            assigned.setPlaceholder("Not Assigned");
        else
            assigned.setValue(report.getAssigned());

        if (report.getVersion() == null)
            version.setPlaceholder("No Version");
        else
            version.setValue(report.getVersion());

        if(report.getVersion() == null)
            projectVersion.setText("");
        else
            projectVersion.setText(report.getVersion().toString());


        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        revert.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
        revert.addClickShortcut(Key.ESCAPE);
        //Button Actions
        save.addClickListener(buttonClickEvent -> {
            validateAndSave();
            UI.getCurrent().navigate(BugrapViewImpl.class);
        });

        revert.addClickListener(buttonClickEvent -> {
            // route to main page
            UI.getCurrent().navigate(BugrapViewImpl.class);
        });




        HorizontalLayout level1 = new HorizontalLayout(projectName,projectVersion);
        level1.setWidthFull();
        projectVersion.getElement().getStyle().set("margin-left", "auto");
        add(level1);
        add(summary);
        HorizontalLayout buttons = new HorizontalLayout(save,revert);
        HorizontalLayout level2 = new HorizontalLayout( priority,
                type,
                status,
                assigned,
                version,
                buttons
        );
        level2.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        level2.setWidthFull();
        buttons.getStyle().set("margin-left","auto");
        HorizontalLayout descLayout = new HorizontalLayout(description);
        descLayout.setWidth("50%");
        description.setWidthFull();
        add(level2, descLayout);


    }

    private void validateAndSave() {
        binder.writeBeanIfValid(report);
        bugrapPresenter.saveReport(report);
    }

    // if user tries to access edit page without selection of a report then reroute to home
    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        report = BugrapViewImpl.getSelectedReport();
        if(report == null)
            UI.getCurrent().navigate(BugrapViewImpl.class);
    }


}

