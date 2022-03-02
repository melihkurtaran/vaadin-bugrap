package org.tatu.bugrap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.menubar.MenuBar;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	private ProjectVersion selectedVersion;
	private List<ProjectVersion> versions;
	private Button buttonReportBug;
	private Button buttonReqFeature;
	private Button buttonMngProject;
	private ReportForm formSingle;
	private ReportFormMultiple formMultiple;
	private MenuBar StatusBar = new MenuBar();
	private List<String> selectedStatuses = new ArrayList<>();

	private static Report selectedReport;


	public BugrapViewImpl(BugrapPresenter presenter) {
		this.presenter = presenter;
		presenter.setView(this);
		versions = new ArrayList<ProjectVersion>();

		setSizeFull();
		grid = new Grid<>(Report.class);
		grid.getDataCommunicator().setPagingEnabled(true);
		grid.setColumns("priority","type","summary","assigned","version");
		grid.getColumnByKey( "assigned").setHeader("Assigned to");
		grid.setSelectionMode(Grid.SelectionMode.MULTI);

		grid.addItemDoubleClickListener(dblClick -> {
			selectedReport = dblClick.getItem();
			UI.getCurrent().navigate(SeparateEditView.class);
		});


		// right now only seconds difference -> will be updated to support mins/hours/days ago
		grid.addColumn(report -> Math.abs(report.getTimestamp().getTime() - report.getReportedTimestamp().getTime())).setHeader(("Reported"));

		//Starts as ordered by priority column
		List<GridSortOrder<Report>> order = new ArrayList<GridSortOrder<Report>>();
		order.add(new GridSortOrder<Report>(grid.getColumns().get(0), SortDirection.DESCENDING));
		grid.setColumnReorderingAllowed(true);
		grid.sort(order);

		grid.addSelectionListener(selectionEvent -> {
			if(selectionEvent.getAllSelectedItems().size()==1) {
				closeMultipleEditor();
				formSingle.setVisible(true);
				selectedReport = selectionEvent.getFirstSelectedItem().get();
				formSingle.setSummary(selectedReport.getSummary());
				formSingle.setDescription(selectedReport.getDescription());
				formSingle.setReport(selectedReport);
			}
			else if(selectionEvent.getAllSelectedItems().size()==0) {
				closeSingleEditor();
				closeMultipleEditor();
			}
			else if(selectionEvent.getAllSelectedItems().size()>1) {
				formMultiple.setTitle(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " items selected");
				formMultiple.setReports(selectionEvent.getAllSelectedItems());
				closeSingleEditor();
				formMultiple.setVisible(true);
			}
		}) ;

		//version Selection for the grid
		versionSelection = new ComboBox<>("");

		projectSelection = new ComboBox<>();
		projectSelection.setWidth("50%");
		projectSelection.setPlaceholder("Select a project");
		projectSelection.addValueChangeListener(event -> {
			selectedProject = event.getValue();
			ProjectVersion v = new ProjectVersion();
			versions.clear();
			v.setVersion("All Versions");
			versions.add(v);
			versions.addAll(presenter.requestProjectVersionsByProject(selectedProject));
			versionSelection.setItems(versions);
			versionSelection.setValue(v); //to start as all versions selected
			grid.setItems(presenter.requestReportsByProject(selectedProject));
			dataView.refreshAll();
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
			selectedVersion = version.getValue();
			if (version.getValue().getVersion().equals("All Versions"))
				grid.setItems(presenter.requestReportsByProject(selectedProject));
			else
				grid.setItems(query -> presenter.requestReportsByVersionAndProject(version.getValue(),selectedProject, query));
		});

		//Status Bar
		MenuItem options = StatusBar.addItem("Custom...");
		SubMenu subItems = options.getSubMenu();

		ComponentEventListener<ClickEvent<MenuItem>> listener = event -> {

			if (event.getSource().isChecked())
				selectedStatuses.add(event.getSource().getText());
			else
				selectedStatuses.remove(event.getSource().getText());
			Notification.show(Arrays.toString(selectedStatuses.toArray()));
			grid.setItems(query -> presenter.requestReportsByStatus(selectedStatuses,selectedVersion,selectedProject, query));

		};

		for (Report.Status status : Report.Status.values()) {
			MenuItem item = subItems.addItem(status.toString());
			item.setCheckable(true);
			item.setChecked(true);
			item.addClickListener(listener);
			selectedStatuses.add(status.name());
		}



		EditorForSingleReport();
		EditorMultipleReport();
		formSingle.setVisible(false);
		formMultiple.setVisible(false);

		countLabel = new Span();
		HorizontalLayout horizontalLayout = new HorizontalLayout(buttonReportBug,buttonReqFeature,buttonMngProject,filter);
		horizontalLayout.setWidthFull();
		filter.getStyle().set("margin-left","auto");
		add(horizontalLayout);
		add(new HorizontalLayout(new Paragraph("Reports for"),versionSelection));
		add(StatusBar);
		add(getContent());
		this.setFlexGrow(1, grid);


	}

	private void closeSingleEditor() {
		formSingle.setReport(null);
		formSingle.setVisible(false);
	}

	private void closeMultipleEditor() {
		formMultiple.setVisible(false);
	}

	private Component getContent() {
		VerticalLayout content = new VerticalLayout(grid,formSingle,formMultiple,countLabel);
		content.setFlexGrow(4,grid);
		content.setFlexGrow(2,formSingle);
		content.setFlexGrow(1,formMultiple);
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

		formSingle = new ReportForm(Collections.EMPTY_LIST, versions);
		formSingle.setWidthFull();
		formSingle.setMaxHeight("50%");

		formSingle.addListener(ReportForm.SaveEvent.class, this::saveReport);
		formSingle.addListener(ReportForm.CloseEvent.class, closeEvent -> closeSingleEditor());
	}

	public void EditorMultipleReport()
	{
		//this will create a split panel to edit multiple report
		formMultiple = new ReportFormMultiple(Collections.EMPTY_LIST, versions);
		formSingle.setWidthFull();
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
		closeSingleEditor();
	}

	public static Report getSelectedReport() {
		return selectedReport;
	}

}
