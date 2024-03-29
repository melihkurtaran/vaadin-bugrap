package org.tatu.bugrap.views;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
	private DBTools dbTools;
	private BugrapView view;

	public BugrapPresenter(ReportRepository reportRepository, ProjectRepository projectRepository,
						   ProjectVersionRepository projectVersionRepository, ReporterRepository reporterRepository
							, CommentRepository commentRepository, DBTools dbTools) {

		this.reporterRepository = reporterRepository;
		this.projectRepository = projectRepository;
		this.projectVersionRepository = projectVersionRepository;
		this.reportRepository = reportRepository;
		this.commentRepository = commentRepository;
		this.dbTools = dbTools;
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
	public Stream<Report> requestReports(String filter,List<String> statuses, ProjectVersion version,Project p,Reporter assignee, Query<Report, ?> query) {

		if (version == null) {
			version = new ProjectVersion();
			version.setVersion("All Versions");
		}
		ProjectVersion finalVersion = version;

		Report report = new Report();
		report.setSummary(filter);
		Example<Report> example = Example.of(report,
				ExampleMatcher.matchingAny().withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase()));
		int a = query.getPageSize();
		int b = query.getPage();
		return reportRepository.findAll(example,PageRequest.of(query.getPage(),
				query.getPageSize()*5, Sort.by("priority").descending())).stream().filter(r -> {
			if( r.getProject() != null && p != null
					&& ( finalVersion.getVersion().equals("All Versions") || (r.getVersion() != null && r.getVersion().getVersion().equals(finalVersion.getVersion())))
					&& r.getProject().getName().equals(p.getName())
					&& ((assignee == null) || (assignee.equals(r.getAssigned())))
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

	public int requestReportCountByProject(Project p) {
		int count = (int) (reportRepository.countByProjectAndStatusIsNull(p) + reportRepository.countByProjectAndStatusNotAndStatusIsNotNull(p, Report.Status.OPEN) + reportRepository.countByProjectAndStatus(p, Report.Status.OPEN));
		view.setCount(count);
		return count;
	}

	public Stream<Project> requestProjects() {
		return projectRepository.findAll().stream();
	}

	public List<Comment> requestCommentsByReport(Report report) {
		return commentRepository.findAllByReport(report);
	}

	public List<Reporter> requestReporters() {
		return reporterRepository.findAll();
	}

	public List<Report> requestReportsByProject(Project p) {
		return reportRepository.findAllByProject(p);
	}

	public List<ProjectVersion> requestProjectVersionsByProject(Project p) {
		if (p == null)
			p = BugrapViewImpl.getSelectedReport().getProject();
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

	public void saveReports(Set<Report> reports){
		if(reports == null){
			System.err.println("Report set is null.");
			return;
		}
		reportRepository.saveAll(reports);
	}

	public void saveComment(Comment comment){
		if(comment == null){
			System.err.println("Comment is null.");
			return;
		}
		commentRepository.save(comment);
	}

	public void createUser(String name, String email, String password, boolean admin) {

		Reporter user = new Reporter();
		user.setAdmin(admin);
		user.setName(name);
		user.setEmail(email);

		try {
			user.hashPassword(password);
		} catch (Exception var7) {
			throw new RuntimeException(var7);
		}

		reporterRepository.save(user);
	}
	// find the user with username or email
	public Reporter getUser(String username){
		try{
		return reporterRepository.getByNameOrEmail(username,username);
		}catch (Exception e){
			return null;
		}
	}

	// for the distribution bar
	public long getNumberOfReports(Project selectedProject, String color)
	{
		if (selectedProject == null)
			return 0;
		else if(color.equals("blue")) // blue (number of closed)
			return reportRepository.countByProjectAndStatusNot(selectedProject,Report.Status.OPEN);
		else if(color.equals("green")) // green (number of open)
			return reportRepository.countByProjectAndStatus(selectedProject,Report.Status.OPEN);
		else if(color.equals("orange")) // orange (number of not assigned)
			return reportRepository.countByProjectAndAssignedIsNull(selectedProject);
		else
			return 0;
	}
}
