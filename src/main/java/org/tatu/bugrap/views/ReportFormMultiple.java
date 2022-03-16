package org.tatu.bugrap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportFormMultiple extends VerticalLayout
{
    Binder<Report> binder = new BeanValidationBinder<>(Report.class);
    H3 title = new H3("");
    private Set<Report> reports;

    TextArea description = new TextArea("");
    Button openBtn = new Button("Open");
    ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    ComboBox<Report.Type> type = new ComboBox<>("Type");
    ComboBox<Report.Status> status = new ComboBox<>("Status");
    ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");

    Button save = new Button("Save Changes");
    Button revert = new Button("Revert", new Icon(VaadinIcon.ROTATE_LEFT));

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
        save.addClickListener(buttonClickEvent -> validateAndSave());
        revert.addClickListener(buttonClickEvent -> fireEvent(new ReportFormMultiple.CloseEvent(this)));

        revert.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
        revert.addClickShortcut(Key.ESCAPE);
        HorizontalLayout layout = new HorizontalLayout(save,revert);
        return layout;
    }

    private void validateAndSave() {
        boolean isValid = false;

        if(priority.getValue() == null){
            priority.setErrorMessage("Priority cannot be empty!");
            priority.setInvalid(true);
        }else if(type.getValue() == null){
            type.setErrorMessage("Type cannot be empty!");
            type.setInvalid(true);
        }else if(status.getValue() == null){
            status.setErrorMessage("Status cannot be empty!");
            status.setInvalid(true);
        }else {
            for (Report r : reports) {
                r.setPriority(priority.getValue());
                r.setStatus(status.getValue());
                r.setType(type.getValue());
                r.setAssigned(assigned.getValue());
                r.setVersion(version.getValue());
            }
            fireEvent(new ReportFormMultiple.SaveEvent(this,reports));
        }
    }

    public void setTitle(String s)
    {
        title.removeAll();
        title.add(s);
    }

    public void setReports(Set<Report> allSelectedReports) {
        reports = allSelectedReports;

        Boolean prio_same = true, type_same = true, stat_same = true, assig_same = true, vers_same = true;

        //if all selected reports has the same value then show it
        Report selectedReport = reports.stream().collect(Collectors.toUnmodifiableList()).get(0);
        for (Report r : reports)
        {
            if(prio_same && (selectedReport.getPriority()!=null) && !selectedReport.getPriority().equals(r.getPriority()))
            { prio_same = false; priority.clear();}
            if(type_same && (selectedReport.getType()!=null) && !selectedReport.getType().equals(r.getType()))
            { type_same = false; type.clear();}
            if(stat_same && (selectedReport.getStatus()!=null) && !selectedReport.getStatus().equals(r.getStatus()))
            { stat_same = false; status.clear();}
            if(assig_same && (selectedReport.getAssigned()!=null) && !selectedReport.getAssigned().equals(r.getAssigned()))
            { assig_same = false; assigned.clear();}
            if(vers_same && (selectedReport.getVersion()!=null) && !selectedReport.getVersion().equals(r.getVersion()))
            { vers_same = false; version.clear();}
        }
        if (prio_same && (selectedReport.getPriority()!=null)) { priority.setValue(selectedReport.getPriority()); }
        if (type_same && (selectedReport.getType()!=null)) { type.setValue(selectedReport.getType()); }
        if (stat_same && (selectedReport.getStatus()!=null)) { status.setValue(selectedReport.getStatus()); }
        if (assig_same && (selectedReport.getAssigned()!=null)) { assigned.setValue(selectedReport.getAssigned()); }
        if (vers_same && (selectedReport.getVersion()!=null)) { version.setValue(selectedReport.getVersion()); }

    }


    // Events
    public static abstract class ReportFormMultipleEvent extends ComponentEvent<ReportFormMultiple> {
        private Set<Report> reports;

        protected ReportFormMultipleEvent(ReportFormMultiple source, Set<Report> reports) {
            super(source, false);
            this.reports = reports;
        }

        public Set<Report> getReports() {
            return reports;
        }
    }

    public static class SaveEvent extends ReportFormMultiple.ReportFormMultipleEvent {
        SaveEvent(ReportFormMultiple source, Set<Report> reports) {
            super(source, reports);
        }
    }

    public static class CloseEvent extends ReportFormMultiple.ReportFormMultipleEvent {
        CloseEvent(ReportFormMultiple source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
