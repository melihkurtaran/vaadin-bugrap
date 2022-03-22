package org.tatu.bugrap.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ReportForm extends VerticalLayout {

    protected Binder<Report> binder = new BeanValidationBinder<>(Report.class);

    private H3 summary = new H3("");
    protected TextArea description = new TextArea("Description");
    private Button openBtn = new Button("Open");
    protected HorizontalLayout sumLayout;
    protected ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    protected ComboBox<Report.Type> type = new ComboBox<>("Type");
    protected ComboBox<Report.Status> status = new ComboBox<>("Status");
    protected ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    protected ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");

    protected Button save = new Button("Save Changes");
    protected Button revert = new Button("Revert", new Icon(VaadinIcon.ROTATE_LEFT));
    private Report report;
    private Reporter author;
    private Date date = new Date();
    protected VerticalLayout reportInfo;


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

        assigned.setPlaceholder("Not Assigned");
        version.setPlaceholder("No Version");


        HorizontalLayout buttons = new HorizontalLayout(createButtonLayout());

        FlexLayout flexLayout = new FlexLayout(priority, type, status,assigned, version, buttons);
        for (int i=0;i<flexLayout.getComponentCount();i++)
            flexLayout.getComponentAt(i).getElement().getStyle().set("margin","10px");

        flexLayout.addClassName("responsive-layout");
        flexLayout.getStyle().set("overflow","auto");
        flexLayout.setAlignItems(Alignment.BASELINE);

        sumLayout = new HorizontalLayout(summary,openBtn);
        sumLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        description.setWidthFull();
        description.setMaxLength(1500);

        if(author == null)
        {
            author = new Reporter();
            author.setName("unknown");
        }

        reportInfo = new VerticalLayout();
        reportInfo.setSizeFull();
        reportInfo.setAlignItems(Alignment.END);

        Avatar avatarAuthor = new Avatar(author.getName());

        avatarAuthor.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatarAuthor.setColorIndex(ThreadLocalRandom.current().nextInt(1, 8));
        VerticalLayout userInf = new VerticalLayout(new Paragraph(author.getName()),new Paragraph( new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(date)));

        userInf.setPadding(false);
        reportInfo.add(new HorizontalLayout(avatarAuthor, userInf));



        openBtn.addClickShortcut(Key.ENTER,KeyModifier.CONTROL);

        openBtn.getElement().getStyle().set("margin-left", "auto");
        buttons.getElement().getStyle().clear().set("margin-left", "auto");
        sumLayout.setWidthFull();
        flexLayout.setWidthFull();
        flexLayout.setHeightFull();

        this.setWidthFull();
        HorizontalLayout descLayout = new HorizontalLayout(description,reportInfo);
        descLayout.setWidthFull();
        HorizontalLayout layout2 = new HorizontalLayout(flexLayout);
        layout2.setWidthFull();
        add( sumLayout,layout2, descLayout);

    }

    public ReportForm() {

    }

    public void setSummary(String s)
    {
        summary.removeAll();
        summary.add(s);
    }

    public void setTime(Date date)
    {
        this.date = date;
    }

    public void setAuthor(Reporter author){
        this.author = author;
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
