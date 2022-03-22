package org.tatu.bugrap.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class AddReport extends ReportForm{

    private TextField summary = new TextField("Summary");
    private ComboBox<Project> project = new ComboBox<>("Project");
    protected Button addReportBtn = new Button("Add Report");
    Report report = new Report();

    public AddReport(Stream<Project> projects, List<Reporter> reporters, List<ProjectVersion> versions){
        super(reporters,versions);
        binder.bindInstanceFields(this);
        project.setItems(projects);
        reportInfo.setVisible(false);
        sumLayout.setVisible(false);
        description.setWidth("50%");
        HorizontalLayout horizontalLayout = new HorizontalLayout(project,summary);
        horizontalLayout.setWidthFull();
        horizontalLayout.setFlexGrow(1,project);
        horizontalLayout.setFlexGrow(3,summary);
        this.addComponentAsFirst(horizontalLayout);
        type.setValue(Report.Type.BUG);
        type.setReadOnly(true);
        save.setVisible(false);
        addReportBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addReportBtn.addClickListener(event -> addReport());
        HorizontalLayout layout = new HorizontalLayout(addReportBtn);
        layout.setWidthFull();
        addReportBtn.getStyle().set("margin-left","auto");
        this.addComponentAsFirst(layout);
        revert.addClickListener(event -> fireEvent(new CloseEvent(this)));
        binder.readBean(report);
    }

    public void addReport(){
        try {
            binder.writeBean(report);
            report.setProject(project.getValue());
            report.setSummary(summary.getValue());
            report.setTimestamp(new Date());
            report.setReportedTimestamp(new Date());
            if(project.getValue() == null) {
                project.setErrorMessage("Project cannot be empty!");
                project.setInvalid(true);
            }else if(summary.getValue().length() < 5) {
                summary.setErrorMessage("Summary should be at least 5 characters!");
                summary.setInvalid(true);
            }else if(priority.getValue() == null){
                priority.setErrorMessage("Priority cannot be empty!");
                priority.setInvalid(true);
            }else if(type.getValue() == null){
                type.setErrorMessage("Type cannot be empty!");
                type.setInvalid(true);
            }else if(status.getValue() == null){
                status.setErrorMessage("Status cannot be empty!");
                status.setInvalid(true);
            }else {
                fireEvent(new SaveEvent(this, report));
                project.clear();
                summary.clear();
                priority.clear();
                status.clear();
                assigned.clear();
                version.clear();
            }

        }catch (ValidationException e){
            e.printStackTrace();
        }
    }

    public void setType(Report.Type t){
        type.setValue(t);
        type.setReadOnly(true);
    }

    // Events
    public static abstract class AddReportEvent extends ComponentEvent<AddReport> {
        private Report report;

        protected AddReportEvent(AddReport source, Report report) {
            super(source, false);
            this.report = report;
        }

        public Report getReport() {
            return report;
        }
    }

    public static class SaveEvent extends AddReport.AddReportEvent {
        SaveEvent(AddReport source, Report report) {
            super(source, report);
        }
    }

    public static class CloseEvent extends AddReport.AddReportEvent {
        CloseEvent(AddReport source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
