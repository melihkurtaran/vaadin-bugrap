package org.tatu.bugrap.views;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.notification.Notification;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.vaadin.bugrap.domain.entities.*;
import org.vaadin.bugrap.domain.spring.*;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.RouteScope;

@Component
@RouteScope
public class BugrapPresenter {

	private ReportRepository reportRepository;
	private ProjectRepository projectRepository;
	private ProjectVersionRepository projectVersionRepository;
	private ReporterRepository reporterRepository;
	private CommentRepository commentRepository;
	private BugrapView view;

	public BugrapPresenter(ReportRepository reportRepository, ProjectRepository projectRepository,
						   ProjectVersionRepository projectVersionRepository, ReporterRepository reporterRepository
							, CommentRepository commentRepository) {

		this.reportRepository = reportRepository;
		this.projectRepository = projectRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.reportRepository = reportRepository;
		this.commentRepository = commentRepository;
	}

	//requesting reports without using projects
	public Stream<Report> requestReports(String filter, Query<Report, ?> query) {
		Report report = new Report();
		report.setSummary(filter);
		Example<Report> example = Example.of(report,
				ExampleMatcher.matchingAny().withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase()));

		return reportRepository.findAll(example, PageRequest.of(query.getPage(), query.getPageSize())).stream();
	}

	//requesting reports with using its statuses, versions and the project
	public Stream<Report> requestReports(List<String> statuses, ProjectVersion version,Project p, Query<Report, ?> query) {

		if (version == null) {
			version = new ProjectVersion();
			version.setVersion("All Versions");
		}
		ProjectVersion finalVersion = version;

		return reportRepository.findAll(PageRequest.of(query.getPage(),
				query.getPageSize())).stream().filter(r -> {
			if( r.getProject() != null && p != null
					&& ( finalVersion.getVersion().equals("All Versions") || (r.getVersion() != null && r.getVersion().getVersion().equals(finalVersion.getVersion())))
					&& r.getProject().getName().equals(p.getName())
					&& ( r.getStatus() == null || statuses.contains(r.getStatus().toString()))){
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

	public List<Comment> requestCommentsByReport(Report report) {
		return commentRepository.findAllByReport(report);
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

	public void saveComment(Comment comment){
		commentRepository.save(comment);
	}
}
