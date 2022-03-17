package org.tatu.bugrap.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.bugrap.domain.entities.Project;

@Tag("distribution-bar")
public class DistributionBar extends HorizontalLayout {

    private BugrapPresenter presenter;
    private Project selectedProject;
    private long numOfClosedReports = 0;
    private long numOfAssignedReports = 0;
    private long numOfUnassignedReports = 0;

    private DistributionBarPart bluePart;
    private DistributionBarPart greenPart;
    private DistributionBarPart orangePart;

    public DistributionBar(BugrapPresenter presenter){
        this.presenter = presenter;
        this.setClassName("distribution-bar");
        this.setSpacing(false);
        this.setWidth("95%");

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

        this.removeAll();

        numOfClosedReports = presenter.getNumberOfReports(selectedProject,"blue");
        numOfAssignedReports = presenter.getNumberOfReports(selectedProject,"green");
        numOfUnassignedReports = presenter.getNumberOfReports(selectedProject,"orange");

        bluePart = new DistributionBarPart("#4B5BD6",numOfClosedReports);
        greenPart = new DistributionBarPart("#0A8BAE",numOfAssignedReports);
        orangePart = new DistributionBarPart("#DF9135",numOfUnassignedReports);

        this.add(bluePart.label,bluePart,greenPart.label,greenPart,orangePart.label,orangePart);

    }

    @Tag("distribution-bar-part")
    private class DistributionBarPart extends LitTemplate {
        private Label label;

        public DistributionBarPart(String color, long size){
            this.getStyle().set("background-color",color).set("width",String.valueOf(size*30)+"px");
            this.addClassName("distribution-bar-part");
            label = new Label(" " + String.valueOf(size));
            label.addClassName("distribution-bar-part-number");

            if(color == "#4B5BD6") { this.getStyle().set("border-radius","5px 0 0 5px"); }
            else if(color == "#DF9135") { this.getStyle().set("border-radius","0 5px 5px 0"); }
        }

    }
}





