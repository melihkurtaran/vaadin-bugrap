package org.tatu.bugrap.views;

import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.spring.ReportRepository;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.RouteScope;

@Component
@RouteScope
public class BugrapPresenter {

	private ReportRepository reportRepository;
	private BugrapView view;

	public BugrapPresenter(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	public Stream<Report> requestReports(String filter, Query<Report, ?> query) {
		Report report = new Report();
		report.setSummary(filter);
		Example<Report> example = Example.of(report,
				ExampleMatcher.matchingAny().withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase()));

		return reportRepository.findAll(example, PageRequest.of(query.getPage(), query.getPageSize())).stream();
	}

	public int requestReportCount() {
		int count = (int) reportRepository.count();
		view.setCount(count);
		return count; 
	}
	
	public void setView(BugrapView view) {
		this.view = view;
	}
}
