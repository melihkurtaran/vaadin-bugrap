package org.tatu.bugrap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Bugrap Home")
@Route(value = "")
public class BugrapViewImpl extends VerticalLayout implements BugrapView, AfterNavigationObserver {

	private Grid<Report> grid;
	private BugrapPresenter presenter;
	private TextField filter;
	private GridLazyDataView<Report> dataView;
	private Span countLabel;
	private int count;
	private ComboBox<Project> projectSelection;
	private ComboBox<ProjectVersion> versionSelection;
	private Project selectedProject;
	private List<ProjectVersion> versions;
	private Button buttonReportBug;
	private Button buttonReqFeature;
	private Button buttonMngProject;
	private ReportForm form;


	public BugrapViewImpl(BugrapPresenter presenter) {
		this.presenter = presenter;
		presenter.setView(this);
		versions = new ArrayList<ProjectVersion>();

		setSizeFull();
		grid = new Grid<>(Report.class);
		grid.getDataCommunicator().setPagingEnabled(true);
		grid.setColumns("priority","type","summary","assigned","version");
		grid.getColumnByKey( "assigned").setHeader("Assigned to");
		grid.setHeight("500px");
		grid.setSelectionMode(Grid.SelectionMode.MULTI);

		// right now only seconds difference -> will be updated to support mins/hours/days ago
		grid.addColumn(report -> Math.abs(report.getTimestamp().getTime() - report.getReportedTimestamp().getTime())).setHeader(("Reported"));

		//Starts as ordered by priority column
		List<GridSortOrder<Report>> order = new ArrayList<GridSortOrder<Report>>();
		order.add(new GridSortOrder<Report>(grid.getColumns().get(0), SortDirection.DESCENDING));
		grid.setColumnReorderingAllowed(true);
		grid.sort(order);

		grid.addSelectionListener(selectionEvent -> {
			if(selectionEvent.getAllSelectedItems().size()==1) {
				Notification.show(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " item selected");
				form.setVisible(true);
				form.setReport(selectionEvent.getFirstSelectedItem().get());
			}
			else if(selectionEvent.getAllSelectedItems().size()!=1) {
				Notification.show(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " items selected");
				closeEditor();
			}
		}) ;

		//version Selection for the grid
		versionSelection = new ComboBox<>("Reports for");

		projectSelection = new ComboBox<>();
		projectSelection.setWidth("50%");
		projectSelection.setPlaceholder("Select a project");
		projectSelection.addValueChangeListener(event -> {
			selectedProject = event.getValue();
			ProjectVersion v = new ProjectVersion();
			v.setVersion("All versions");
			versions.add(v);
			versions.addAll(presenter.requestProjectVersionsByProject(selectedProject));
			versionSelection.setItems(versions);
			versionSelection.setValue(v); //to start as all versions selected
			grid.setItems(presenter.requestReportsByProject(selectedProject));
		});
		add(projectSelection);

		//buttons
		buttonReportBug = new Button("Report a bug");
		buttonReqFeature = new Button("Request a feature");
		buttonMngProject = new Button("Manage Project");

		filter = new TextField("");
		filter.setPlaceholder("Search..");
		filter.setValueChangeMode(ValueChangeMode.TIMEOUT);
		filter.setValueChangeTimeout(2000);
		filter.addValueChangeListener(event -> {
			dataView = grid.setItems(query -> presenter.requestReports(event.getValue(), query));
			dataView.setItemCountEstimate(count);
			setCount(count);
		});

		// filter reports by version
		versionSelection.addValueChangeListener(version -> {
			dataView = grid.setItems(query -> presenter.requestReportsByVersion(version.getValue(), query));
//			List<Report> reportList = new ArrayList<Report>();
//			dataView.getItems().forEach(report -> {
//				if (report.getVersion().equals(version))
//					reportList.add(report);
//			});
//			grid.setItems(reportList);
		});

		countLabel = new Span();
		HorizontalLayout horizontalLayout = new HorizontalLayout(buttonReportBug,buttonReqFeature,buttonMngProject,filter);
		add(horizontalLayout);
		add(versionSelection);
		EditorForSingleReport();
		closeEditor();
		add(getContent());
		this.setFlexGrow(1, grid);
	}

	private void closeEditor() {
		form.setReport(null);
		form.setVisible(false);
	}

	private Component getContent() {
		VerticalLayout content = new VerticalLayout(grid,form,countLabel);
		content.setFlexGrow(4,grid);
		content.setFlexGrow(1,form);
		content.addClassName("content");
		content.setSizeFull();
		return content;
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		count = presenter.requestReportCount();
		dataView = grid.setItems(query -> presenter.requestReports("", query));
		dataView.setItemCountEstimate(count);

		projectSelection.setItems(presenter.requestProjects());
	}

	@Override
	public void setCount(int count) {
		long items = dataView != null ? dataView.getItems().count() : count;
		countLabel.setText(String.format("Count: %s / %s", items, count));
	}


	public void EditorForSingleReport()
	{
		//this will create a split panel to edit a report

		form = new ReportForm(Collections.EMPTY_LIST, versions);
		form.setWidth("25em");

		form.addListener(ReportForm.SaveEvent.class, this::saveReport);
		form.addListener(ReportForm.CloseEvent.class, closeEvent -> closeEditor());
	}

	public void updateList(){
		if(projectSelection.getValue() != null)
			grid.setItems(presenter.requestReportsByProject(selectedProject));
		else
			grid.setItems(query -> presenter.requestReports("", query));
	}

	public void saveReport(ReportForm.SaveEvent event){
		presenter.saveReport(event.getReport());
		updateList();
		closeEditor();
	}
}
