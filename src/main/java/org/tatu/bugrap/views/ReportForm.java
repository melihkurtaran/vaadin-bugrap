package org.tatu.bugrap.views;

import com.vaadin.flow.component.*;
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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;


public class ReportForm extends VerticalLayout {

    Binder<Report> binder = new BeanValidationBinder<>(Report.class);

    H3 summary = new H3("");
    TextArea description = new TextArea("");
    Button openBtn = new Button("Open");
    ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    ComboBox<Report.Type> type = new ComboBox<>("Type");
    ComboBox<Report.Status> status = new ComboBox<>("Status");
    ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");

    Button save = new Button("Save Changes");
    Button revert = new Button("Revert");
    private Report report;


    public ReportForm(List<Reporter> reporters,List<ProjectVersion> versions) {

        binder.bindInstanceFields(this);

        priority.setItems(Report.Priority.values());
        status.setItems(Report.Status.values());
        type.setItems(Report.Type.values());
        assigned.setItems(reporters);
        assigned.setItemLabelGenerator(Reporter::getName);
        version.setItems(versions);
        version.setItemLabelGenerator(ProjectVersion::getVersion);

        openBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        openBtn.addClickListener(buttonClickEvent -> {
            UI.getCurrent().navigate(SeparateEditView.class);
        });

        HorizontalLayout buttons = new HorizontalLayout(createButtonLayout());

        HorizontalLayout layout = new HorizontalLayout(summary,openBtn);
        HorizontalLayout layout2 = new HorizontalLayout( priority,
                type,
                status,
                assigned,
                version,
                buttons
        );

        layout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        layout2.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        description.setWidth("50%");
        description.setMaxHeight("50%");

        openBtn.getElement().getStyle().set("margin-left", "auto");
        buttons.getElement().getStyle().set("margin-left", "auto");
        layout.setWidthFull();
        layout2.setWidthFull();
        this.setWidthFull();
        add( layout, layout2, description);

    }

    public ReportForm() {

    }

    public void setSummary(String s)
    {
        summary.removeAll();
        summary.add(s);
    }

    public void setDescription(String s)
    {
        description.setValue(s);
    }

    private Component createButtonLayout(){
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        revert.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);

        revert.addClickShortcut(Key.ESCAPE);

        save.addClickListener(buttonClickEvent -> validateAndSave());
        revert.addClickListener(buttonClickEvent -> fireEvent(new CloseEvent(this)));

        HorizontalLayout layout = new HorizontalLayout(save,revert);
        return layout;
    }

    private void validateAndSave() {
        try {
            binder.writeBean(report);
            fireEvent(new SaveEvent(this,report));
        } catch (ValidationException e){
            e.printStackTrace();
        }
    }

    public void setReport(Report report){
        this.report = report;
        binder.readBean(report);
    }

    // Events
    public static abstract class ReportFormEvent extends ComponentEvent<ReportForm> {
        private Report report;

        protected ReportFormEvent(ReportForm source, Report report) {
            super(source, false);
            this.report = report;
        }

        public Report getReport() {
            return report;
        }
    }

    public static class SaveEvent extends ReportFormEvent {
        SaveEvent(ReportForm source, Report report) {
            super(source, report);
        }
    }

    public static class DeleteEvent extends ReportFormEvent {
        DeleteEvent(ReportForm source, Report report) {
            super(source, report);
        }

    }

    public static class CloseEvent extends ReportFormEvent {
        CloseEvent(ReportForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
