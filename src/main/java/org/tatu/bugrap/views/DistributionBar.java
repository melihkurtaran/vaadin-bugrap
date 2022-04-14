package org.tatu.bugrap.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import org.vaadin.bugrap.domain.entities.Project;

@Tag("distribution-bar")
@JsModule("./distribution-bar.ts")
public class DistributionBar extends LitTemplate {
    // see frontend/distribution-bar.ts

    private final BugrapPresenter presenter;
    private Project selectedProject;

    public DistributionBar(BugrapPresenter presenter){
        this.presenter = presenter;
        this.setClassName("distribution-bar");
        if(selectedProject != null) {
            updateBar();
        }
        updateBar();
    }

    public void setProject(Project selectedProject) {
        this.selectedProject = selectedProject;
        updateBar();
    }

    public void updateBar() {
        long numOfClosedReports = presenter.getNumberOfReports(selectedProject, "blue");
        long numOfAssignedReports = presenter.getNumberOfReports(selectedProject, "green");
        long numOfUnassignedReports = presenter.getNumberOfReports(selectedProject, "orange");

        getElement().setProperty("numClosed", numOfClosedReports);
        getElement().setProperty("numAssigned", numOfAssignedReports);
        getElement().setProperty("numUnassigned", numOfUnassignedReports);

        getElement().setProperty("sizeClosed", numOfClosedReports *30);
        getElement().setProperty("sizeAssigned", numOfAssignedReports *30);
        getElement().setProperty("sizeUnassigned", numOfUnassignedReports *30);

    }
}





