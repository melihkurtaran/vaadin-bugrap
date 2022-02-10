package org.tatu.bugrap.views;

import com.vaadin.flow.component.notification.Notification;
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

@Route("")
public class BugrapViewImpl extends VerticalLayout implements BugrapView, AfterNavigationObserver {

	private Grid<Report> grid;
	private BugrapPresenter presenter;
	private TextField filter;
	private GridLazyDataView<Report> dataView;
	private Span countLabel;
	private int count;

	public BugrapViewImpl(BugrapPresenter presenter) {
		this.presenter = presenter;
		presenter.setView(this);

		setSizeFull();
		grid = new Grid<>(Report.class);
		grid.getDataCommunicator().setPagingEnabled(true);
		grid.setColumns("priority","type","summary","assigned");
		grid.setHeight("500px");
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(selectionEvent -> {
			if(selectionEvent.getAllSelectedItems().size()==1)
				Notification.show(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " item selected");
			else if(selectionEvent.getAllSelectedItems().size()>1)
				Notification.show(String.valueOf(selectionEvent.getAllSelectedItems().size()) + " items selected");
		}) ;

		filter = new TextField("Filter");
		filter.setValueChangeMode(ValueChangeMode.TIMEOUT);
		filter.setValueChangeTimeout(2000);
		filter.addValueChangeListener(event -> {
			dataView = grid.setItems(query -> presenter.requestReports(event.getValue(), query));
			dataView.setItemCountEstimate(count);
			setCount(count);
		});

		countLabel = new Span();
		
		add(filter, grid, countLabel);
		this.setFlexGrow(1, grid);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		count = presenter.requestReportCount();
		dataView = grid.setItems(query -> presenter.requestReports("", query));
		dataView.setItemCountEstimate(count);
	}

	@Override
	public void setCount(int count) {
		long items = dataView != null ? dataView.getItems().count() : count;
		countLabel.setText(String.format("Count: %s / %s", items, count));
	}
}
