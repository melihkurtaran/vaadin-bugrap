package org.tatu.bugrap.views;

import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.spring.ProjectRepository;
import org.vaadin.bugrap.domain.spring.ReportRepository;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.RouteScope;

@Component
@RouteScope
public class BugrapPresenter {

	private ReportRepository reportRepository;
	private ProjectRepository projectRepository;
	private BugrapView view;

	public BugrapPresenter(ReportRepository reportRepository, ProjectRepository projectRepository) {

		this.reportRepository = reportRepository;
		this.projectRepository = projectRepository;
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

	public Stream<Project> requestProjects() {
		return projectRepository.findAll().stream();
	}

	
	public void setView(BugrapView view) {
		this.view = view;
	}
}
