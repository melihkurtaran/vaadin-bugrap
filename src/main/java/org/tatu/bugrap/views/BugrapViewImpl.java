package org.tatu.bugrap.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
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
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.annotation.security.PermitAll;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	private ComboBox<ProjectVersion> versionSelection = new ComboBox<>("");
	private Project selectedProject;
	private ProjectVersion selectedVersion;
	private List<ProjectVersion> versions = new ArrayList<ProjectVersion>();
	private List<Reporter> reporters;
	private Button buttonReportBug;
	private Button buttonReqFeature;
	private Button buttonMngProject;
	private ReportForm formSingle;
	private ReportFormMultiple formMultiple;
	private Button btnAsgnMe = new Button("Only Me");
	private Button btnAsgnEveryone = new Button("Everyone");
	private MenuBar StatusBar = new MenuBar();
	private Button statusOpen = new Button("Open");
	private Button statusAll = new Button("All kinds");
	private boolean openStatusSelected = false;
	private boolean allStatusSelected = true;
	private boolean assignMeSelected = false;
	private boolean assignEveryoneSelected = true;
	private String filterValue = "";
	ProjectVersion allVersions = new ProjectVersion();
	private Reporter assignee = null;
	private List<String> selectedStatuses = new ArrayList<>();
	private SecurityService securityService;
	private UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	private AddReport AddReportPanel;

	private static Report selectedReport = null;

	private DistributionBar distributionBar;


	public BugrapViewImpl(BugrapPresenter presenter, SecurityService securityService) {
		this.presenter = presenter;
		this.securityService = securityService;

		distributionBar = new DistributionBar(presenter);
		distributionBar.setVisible(false);

		// If the user is not in repository then add it
		if (presenter.getUser(userDetails.getUsername()) == null)
			presenter.createUser(userDetails.getUsername(),userDetails.getUsername() + " @gmail.com","",false);
		reporters = presenter.requestReporters();

		presenter.setView(this);

		this.getStyle().set("background-color","white");
		this.getStyle().set("background-color","white");

		setSizeFull();
		grid = new Grid<>(Report.class);
		grid.getDataCommunicator().setPagingEnabled(true);
		initializeGrid();
		grid.setSelectionMode(Grid.SelectionMode.MULTI);


		grid.addItemDoubleClickListener(dblClick -> {
			selectedReport = dblClick.getItem();
			UI.getCurrent().navigate(SeparateEditView.class);
		});


		grid.addSelectionListener(selectionEvent -> {
			closeAddPanel();
			if(selectionEvent.getAllSelectedItems().size()==1) {
				closeMultipleEditor();
				formSingle.setVisible(true);
				selectedReport = selectionEvent.getFirstSelectedItem().get();
				formSingle.setSummary(selectedReport.getSummary());
				formSingle.setDescription(selectedReport.getDescription());
				formSingle.setAuthor(selectedReport.getAuthor());
				formSingle.setTime(selectedReport.getReportedTimestamp());
				formSingle.setReport(selectedReport);
			}
			else if(selectionEvent.getAllSelectedItems().size()==0) {
				closeSingleEditor();
				closeMultipleEditor();
				selectedReport = null;
			}
			else if(selectionEvent.getAllSelectedItems().size()>1) {
				formMultiple.setTitle(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " items selected");
				formMultiple.setReports(selectionEvent.getAllSelectedItems());
				closeSingleEditor();
				formMultiple.setVisible(true);
			}
		}) ;
		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		selectedProject = presenter.requestProjects().collect(Collectors.toList()).get(0); //default selected project is the first one

		projectSelection = new ComboBox<>();
		projectSelection.setWidth("50%");
		projectSelection.setPlaceholder("Select a project");
		projectSelection.addValueChangeListener(event -> {
			selectedProject = event.getValue();
			distributionBar.setProject(selectedProject);
			distributionBar.setVisible(true);
			versions.clear();
			allVersions.setVersion("All Versions");
			allVersions.setProject(selectedProject);
			versions.add(allVersions);
			versions.addAll(presenter.requestProjectVersionsByProject(selectedProject));
			versionSelection.setItems(versions);
			versionSelection.setValue(allVersions); //to start as all versions selected
			grid.setItems(query -> presenter.requestReports(filterValue,selectedStatuses,selectedVersion,selectedProject,assignee,query));
			count = presenter.requestReportCountByProject(selectedProject);
			dataView.setItemCountEstimate(count);
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
			filterValue = event.getValue();
			dataView = grid.setItems(query -> presenter.requestReports(filterValue, selectedStatuses, selectedVersion, selectedProject, assignee, query));
			dataView.setItemCountEstimate(count);
			setCount(count);
		});

		// filter reports by version
		versionSelection.addValueChangeListener(version -> {

			if (version.getValue() != null && version.getValue().getVersion().equals("All Versions")) {
				grid.removeAllColumns();
				initializeGrid();
			}
			else if(version.getOldValue() != null && version.getOldValue().getVersion().equals("All Versions")){
				grid.removeColumnByKey("version");
				sortGrid();
			}
			selectedVersion = version.getValue();
			grid.setItems(query -> presenter.requestReports(filterValue,selectedStatuses,version.getValue(),selectedProject,assignee, query));
		});

		// Only Me Button for Assignees
		btnAsgnMe.getStyle().set("color","#4B5BD6");
		btnAsgnMe.addClickListener(buttonClickEvent -> {
			if(assignEveryoneSelected) {
				btnAsgnMe.addClassName("button-selected");
				btnAsgnMe.getStyle().set("color","white");
				assignee = presenter.getUser(userDetails.getUsername());
				if(assignEveryoneSelected) {
					buttonDeselect(btnAsgnEveryone);
					assignEveryoneSelected = false;
				}
				assignMeSelected = true;
			}
			refreshGridByButton();
		});

		// Everyone Button for Assignees
		btnAsgnEveryone.addClassName("button-selected");
		btnAsgnEveryone.getStyle().set("color","white");
		btnAsgnEveryone.addClickListener(buttonClickEvent -> {
			if(assignMeSelected) {
				btnAsgnEveryone.addClassName("button-selected");
				btnAsgnEveryone.getStyle().set("color","white");
				assignee = null;
				if(assignMeSelected) {
					buttonDeselect(btnAsgnMe);
					assignMeSelected = false;
				}
				assignEveryoneSelected = true;
			}
			refreshGridByButton();
		});


		//Status Bar
		MenuItem options = StatusBar.addItem("Custom...");
		SubMenu subItems = options.getSubMenu();

		ComponentEventListener<ClickEvent<MenuItem>> listener = event -> {

			if (event.getSource().isChecked())
				selectedStatuses.add(event.getSource().getText());
			else
				selectedStatuses.remove(event.getSource().getText());

			//deselect all other status buttons
			buttonDeselect(statusAll);
			buttonDeselect(statusOpen);
			allStatusSelected = false;
			openStatusSelected = false;


			refreshGridByButton();
		};





		for (Report.Status status : Report.Status.values()) {
			MenuItem item = subItems.addItem(status.toString());
			item.setCheckable(true);
			item.setChecked(true);
			item.addClickListener(listener);
			selectedStatuses.add(status.toString());
		}


		// Open Button for Status
		statusOpen.getStyle().set("color","#4B5BD6");
		statusOpen.addClickListener(buttonClickEvent -> {
			if(!openStatusSelected) {
				statusOpen.addClassName("button-selected");
				statusOpen.getStyle().set("color","white");
				selectedStatuses.clear();
				for (MenuItem item : options.getSubMenu().getItems()) {
					item.setChecked(false);
				}
				selectedStatuses.add("Open");
				if(allStatusSelected) {
					buttonDeselect(statusAll);
					allStatusSelected = false;
				}
				options.getSubMenu().getItems().get(0).setChecked(true);
				openStatusSelected = true;
			}else{
				buttonDeselect(statusOpen);
				options.getSubMenu().getItems().get(0).setChecked(false);
				selectedStatuses.remove("Open");
				openStatusSelected = false;
			}
			refreshGridByButton();
		});
		// All kinds Button for Status
		statusAll.setWidthFull();
		statusAll.addClassName("button-selected");
		statusAll.getStyle().set("color","white");
		statusAll.addClickListener(buttonClickEvent -> {
			if(!allStatusSelected) {
				statusAll.addClassName("button-selected");
				statusAll.getStyle().set("color","white");
				selectedStatuses.clear();
				for (MenuItem item : options.getSubMenu().getItems()) {
					selectedStatuses.add(item.getText());
					item.setChecked(true);
				}
				allStatusSelected = true;
				if(openStatusSelected) {
					buttonDeselect(statusOpen);
					openStatusSelected = false;
				}
			}else{
				buttonDeselect(statusAll);
				selectedStatuses.clear();
				for (MenuItem item : options.getSubMenu().getItems()) {
					item.setChecked(false);
				}
				allStatusSelected = false;
			}
			refreshGridByButton();
		});


		EditorForSingleReport();
		EditorMultipleReport();
		addReportPanel();

		formSingle.setVisible(false);
		formMultiple.setVisible(false);
		AddReportPanel.setVisible(false);

		buttonReportBug.addClickListener(buttonClickEvent -> {
			if(selectedReport == null) {
				AddReportPanel.setType(Report.Type.BUG);
				AddReportPanel.setVisible(true);
			}
		});
		buttonReqFeature.addClickListener(buttonClickEvent -> {
			if(selectedReport == null) {
				AddReportPanel.setType(Report.Type.FEATURE);
				AddReportPanel.setVisible(true);
			}
		});

		countLabel = new Span();
		HorizontalLayout horizontalLayout = new HorizontalLayout(buttonReportBug,buttonReqFeature,buttonMngProject,filter);
		horizontalLayout.setWidthFull();
		filter.getStyle().set("margin-left","auto");
		add(horizontalLayout);
		HorizontalLayout level2 = new HorizontalLayout(new Paragraph("Reports for"),versionSelection, distributionBar);
		level2.setWidthFull();
		add(level2);

		HorizontalLayout assgnBtnLayout = new HorizontalLayout(btnAsgnMe,btnAsgnEveryone);
		assgnBtnLayout.setSpacing(false);
		assgnBtnLayout.getStyle().set("margin-right","50px");
		HorizontalLayout assigneeLayout = new HorizontalLayout(new Label("Assignees"),assgnBtnLayout);
		assigneeLayout.setAlignItems(Alignment.BASELINE);

		HorizontalLayout statusBtnLayout = new HorizontalLayout(statusOpen,statusAll,StatusBar);
		statusBtnLayout.setSpacing(false);
		statusBtnLayout.setAlignItems(Alignment.BASELINE);
		HorizontalLayout statusLayout = new HorizontalLayout(new Label("Status"),statusBtnLayout);
		statusLayout.setAlignItems(Alignment.BASELINE);
		add(new HorizontalLayout(assigneeLayout,statusLayout));


		add(getContent());
		this.setFlexGrow(1, grid);


	}

	private void closeAddPanel(){
		AddReportPanel.setVisible(false);
	}

	private void closeSingleEditor() {
		formSingle.setReport(null);
		formSingle.setVisible(false);
	}

	private void closeMultipleEditor() {
		formMultiple.setVisible(false);
	}

	private Component getContent() {
		VerticalLayout content = new VerticalLayout(grid,formSingle,formMultiple,AddReportPanel,countLabel);
		content.setFlexGrow(4,grid);
		content.setFlexGrow(2,formSingle);
		content.setFlexGrow(2,AddReportPanel);
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

		formSingle = new ReportForm(reporters, presenter.requestProjectVersionsByProject(selectedProject));
		formSingle.setWidthFull();
		formSingle.setMaxHeight("50%");

		formSingle.addListener(ReportForm.SaveEvent.class, this::saveReport);
		formSingle.addListener(ReportForm.CloseEvent.class, closeEvent -> closeSingleEditor());

	}

	public void addReportPanel()
	{
		//this will create a split panel to add a bug or feature

		AddReportPanel = new AddReport(presenter.requestProjects(), reporters, presenter.requestProjectVersionsByProject(selectedProject));
		AddReportPanel.setWidthFull();
		AddReportPanel.setMaxHeight("50%");

		AddReportPanel.addListener(AddReport.SaveEvent.class, this::addReport);
		AddReportPanel.addListener(AddReport.CloseEvent.class, closeEvent -> closeAddPanel());


	}

	public void addReport(AddReport.SaveEvent event){
		event.getReport().setAuthor(presenter.getUser(userDetails.getUsername()));
		presenter.saveReport(event.getReport());
		updateList();
		closeAddPanel();
		distributionBar.updateBar();
		count = presenter.requestReportCount();
		dataView.setItemCountEstimate(count);
	}

	public void EditorMultipleReport()
	{
		//this will create a split panel to edit multiple report
		formMultiple = new ReportFormMultiple(reporters, presenter.requestProjectVersionsByProject(selectedProject));

		formMultiple.addListener(ReportFormMultiple.SaveEvent.class, this::saveReports);
		formMultiple.addListener(ReportFormMultiple.CloseEvent.class, closeEvent -> closeMultipleEditor());
	}

	public void updateList(){
		if(projectSelection.getValue() != null)
			grid.setItems(query ->presenter.requestReports(filterValue,selectedStatuses,selectedVersion,selectedProject,assignee,query));
		else
			grid.setItems(query -> presenter.requestReports("", query));
		sortGrid();
	}

	public void saveReport(ReportForm.SaveEvent event){
		event.getReport().setTimestamp(new Date());
		presenter.saveReport(event.getReport());
		updateList();
		closeSingleEditor();
		distributionBar.updateBar();
	}

	public void saveReports(ReportFormMultiple.SaveEvent event){
		for(Report r : event.getReports())
			r.setTimestamp(new Date());
		presenter.saveReports(event.getReports());
		updateList();
		closeMultipleEditor();
		distributionBar.updateBar();
	}

	public void refreshGridByButton(){
		if(selectedProject == null)
			Notification.show("Please choose a project first!");
		else if(selectedVersion == null)
			Notification.show("Please choose a version first!");
		else
			dataView = grid.setItems(query -> presenter.requestReports(filterValue,selectedStatuses,selectedVersion,selectedProject,assignee, query));

	}

	public void buttonDeselect(Button b)
	{
		b.removeClassName("button-selected");
		b.getStyle().set("color","#414FBC");
	}

	public void initializeGrid()
	{
		grid.setColumns("version","priority","type","summary","assigned");
		grid.getColumnByKey( "assigned").setHeader("Assigned to");
		// these will be updated to support mins/hours/days ago
		grid.addColumn(report -> new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(report.getTimestamp())).setHeader(("Last Modified"));
		grid.addColumn(report -> new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(report.getReportedTimestamp())).setHeader(("Reported"));
		sortGrid();
	}

	public void sortGrid(){
		//Starts as ordered by priority column
		List<GridSortOrder<Report>> order = new ArrayList<GridSortOrder<Report>>();
		order.add(new GridSortOrder<Report>(grid.getColumnByKey("priority"), SortDirection.DESCENDING));
		grid.setColumnReorderingAllowed(true);
		grid.sort(order);

	}

	public static Report getSelectedReport() {
		return selectedReport;
	}

}
