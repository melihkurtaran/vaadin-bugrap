package org.tatu.bugrap.views;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.ProjectRepository;
import org.vaadin.bugrap.domain.spring.ProjectVersionRepository;
import org.vaadin.bugrap.domain.spring.ReportRepository;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.RouteScope;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

@Component
@RouteScope
public class BugrapPresenter {

	private ReportRepository reportRepository;
	private ProjectRepository projectRepository;
	private ProjectVersionRepository projectVersionRepository;
	private ReporterRepository reporterRepository;
	private BugrapView view;

	public BugrapPresenter(ReportRepository reportRepository, ProjectRepository projectRepository,
						   ProjectVersionRepository projectVersionRepository, ReporterRepository reporterRepository) {

		this.reportRepository = reportRepository;
		this.projectRepository = projectRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.reportRepository = reportRepository;
	}

	//requesting reports without using projects
	public Stream<Report> requestReports(String filter, Query<Report, ?> query) {
		Report report = new Report();
		report.setSummary(filter);
		Example<Report> example = Example.of(report,
				ExampleMatcher.matchingAny().withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase()));

		return reportRepository.findAll(example, PageRequest.of(query.getPage(), query.getPageSize())).stream();
	}

	//requesting reports with using its versions and the project
	public Stream<Report> requestReportsByVersionAndProject(ProjectVersion version,Project p, Query<Report, ?> query) {
		return reportRepository.findAll(PageRequest.of(query.getPage(),
				query.getPageSize())).stream().filter(r -> {
					if(r.getVersion() != null && r.getProject() != null
							&& r.getVersion().getVersion().equals(version.getVersion())
							&& r.getProject().getName().equals(p.getName())) {
						return true;
					}else
						return false;
		});
	}

	public int requestReportCount() {
		int count = (int) reportRepository.count();
		view.setCount(count);
		return count; 
	}

	public Stream<Project> requestProjects() {
		return projectRepository.findAll().stream();
	}

	public Stream<Reporter> requestReporters() {
		return reporterRepository.findAll().stream();
	}

	public List<Report> requestReportsByProject(Project p) {
		return reportRepository.findAllByProject(p);
	}

	public List<ProjectVersion> requestProjectVersionsByProject(Project p) {
		return projectVersionRepository.findAllByProject(p);
	}

	
	public void setView(BugrapView view) {
		this.view = view;
	}

	public void saveReport(Report report){
		if(report == null){
			System.err.println("Report is null.");
			return;
		}
		reportRepository.save(report);

	}
}
