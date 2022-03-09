package org.tatu.bugrap.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.tatu.bugrap.security.SecurityService;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Bugrap Home")
@Route(value = "")
@PermitAll
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
	private Button statusOpen = new Button("Open");
	private List<String> selectedStatuses = new ArrayList<>();
	private SecurityService securityService;
	private UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	private static Report selectedReport;


	public BugrapViewImpl(BugrapPresenter presenter, SecurityService securityService) {
		this.presenter = presenter;
		this.securityService = securityService;
		presenter.setView(this);
		versions = new ArrayList<>();

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


		// this will be updated to support mins/hours/days ago
		grid.addColumn(report -> new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(report.getReportedTimestamp())).setHeader(("Reported"));

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
			grid.setItems(query -> presenter.requestReports(selectedStatuses,selectedVersion,selectedProject,query));
			dataView.refreshAll();
		});

		RouterLink username = new RouterLink(userDetails.getUsername(),BugrapViewImpl.class);

		Icon logOutIcon = new Icon(VaadinIcon.POWER_OFF);
		logOutIcon.addClickListener(e -> securityService.logout());
		logOutIcon.addClickShortcut(Key.ESCAPE);
		Icon userIcon = new Icon("lumo", "user");

		userIcon.setColor("#414FBC");
		logOutIcon.setColor("#414FBC");

		HorizontalLayout userLayout = new HorizontalLayout(userIcon,username, logOutIcon);
		username.getStyle().set("margin-right","10px");
		userLayout.setSpacing(false);
		HorizontalLayout level1 = new HorizontalLayout(projectSelection,userLayout);
		level1.setWidthFull();
		level1.setAlignItems(Alignment.END);
		level1.setAlignSelf(Alignment.CENTER,userLayout);
		projectSelection.getStyle().set("margin-right","auto");

		add(level1);

		//buttons
		buttonReportBug = new Button(" Report a bug",new Icon(VaadinIcon.BUG));
		buttonReqFeature = new Button("Request a feature",new Icon(VaadinIcon.LIGHTBULB));
		buttonMngProject = new Button("Manage Project",new Icon(VaadinIcon.COG));


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
			grid.setItems(query -> presenter.requestReports(selectedStatuses,version.getValue(),selectedProject, query));
		});

		//Status Bar
		MenuItem options = StatusBar.addItem("Custom...");
		SubMenu subItems = options.getSubMenu();

		ComponentEventListener<ClickEvent<MenuItem>> listener = event -> {

			if (event.getSource().isChecked())
				selectedStatuses.add(event.getSource().getText());
			else
				selectedStatuses.remove(event.getSource().getText());

			refreshGridByStatus();
		};

		for (Report.Status status : Report.Status.values()) {
			MenuItem item = subItems.addItem(status.toString());
			item.setCheckable(true);
			item.setChecked(true);
			item.addClickListener(listener);
		}


		// Open Button for Status
		statusOpen.getStyle().set("color","#4B5BD6");
		AtomicInteger counter = new AtomicInteger();
		statusOpen.addClickListener(buttonClickEvent -> {
			counter.addAndGet(1);
			if(counter.get() % 2 != 0) {
				statusOpen.addClassName("button-selected");
				statusOpen.getStyle().set("color","white");
				selectedStatuses.clear();
				for (MenuItem item : options.getSubMenu().getItems()) {
					item.setChecked(false);
				}
				selectedStatuses.add("Open");
				options.getSubMenu().getItems().get(0).setChecked(true);
			}else{
				statusOpen.removeClassName("button-selected");
				statusOpen.getStyle().set("color","#414FBC");
				options.getSubMenu().getItems().get(0).setChecked(false);
				selectedStatuses.remove("Open");
			}
			refreshGridByStatus();
		});


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
		add(new HorizontalLayout(statusOpen,StatusBar));
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
			grid.setItems(query ->presenter.requestReports(selectedStatuses,selectedVersion,selectedProject,query));
		else
			grid.setItems(query -> presenter.requestReports("", query));
	}

	public void saveReport(ReportForm.SaveEvent event){
		presenter.saveReport(event.getReport());
		updateList();
		closeSingleEditor();
	}

	public void refreshGridByStatus(){
		if(selectedProject == null)
			Notification.show("Please choose a project first!");
		else if(selectedVersion == null)
			Notification.show("Please choose a version first!");
		else
			dataView = grid.setItems(query -> presenter.requestReports(selectedStatuses,selectedVersion,selectedProject, query));

	}

	public static Report getSelectedReport() {
		return selectedReport;
	}

}
