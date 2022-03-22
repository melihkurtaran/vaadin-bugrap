package org.tatu.bugrap.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;
import java.util.stream.Stream;

public class AddReport extends ReportForm{

    private TextField summary = new TextField("Summary");
    private ComboBox<Project> project = new ComboBox<>("Project");

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

        save.addClickListener(event -> addReport());
        revert.addClickListener(event -> fireEvent(new CloseEvent(this)));
    }

    public void addReport(){
        try {
            var report = new Report();
            binder.writeBean(report);
            fireEvent(new SaveEvent(this,report));

        }catch (ValidationException e){
            e.printStackTrace();
        }
    }

    public void setType(Report.Type t){
        type.setValue(t);
        type.setReadOnly(true);
    }

}
