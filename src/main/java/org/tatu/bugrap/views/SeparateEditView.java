package org.tatu.bugrap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@PageTitle("Edit Report")
@Route(value = "Edit")
@PermitAll
public class SeparateEditView extends VerticalLayout implements AfterNavigationObserver
{
    H3 projectName = new H3("");
    H3 projectVersion = new H3("");
    H3 summary = new H3("");
    Report report;
    Binder<Report> binder = new BeanValidationBinder<>(Report.class);

    TextArea description = new TextArea("");
    ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    ComboBox<Report.Type> type = new ComboBox<>("Type");
    ComboBox<Report.Status> status = new ComboBox<>("Status");
    ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");
    Button save = new Button("Save Changes");
    Button revert = new Button("Revert", new Icon(VaadinIcon.ROTATE_LEFT));
    BugrapPresenter bugrapPresenter;

    List<Comment> commentList;
    VerticalLayout commentLayout = new VerticalLayout();
    CommentPanel commentPanel;

    private UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    private String username = userDetails.getUsername();

    public SeparateEditView(BugrapPresenter bugrapPresenter){
        this.bugrapPresenter = bugrapPresenter;
        this.getStyle().set("background-color","white");

        report = BugrapViewImpl.getSelectedReport();

        priority.setItems(Report.Priority.values());
        status.setItems(Report.Status.values());
        type.setItems(Report.Type.values());
        assigned.setItems(bugrapPresenter.requestReporters());
        assigned.setItemLabelGenerator(Reporter::getName);
        version.setItems(bugrapPresenter.requestProjectVersionsByProject(report.getProject()));
        version.setItemLabelGenerator(ProjectVersion::getVersion);

        binder.readBean(report);

        if(report != null) {
            projectName.setText(report.getProject().getName());
            summary.setText(report.getSummary());
            description.setValue(report.getDescription());
            priority.setValue(report.getPriority());
            status.setValue(report.getStatus());
            type.setValue(report.getType());

            if (report.getAssigned() == null)
                assigned.setPlaceholder("Not Assigned");
            else
                assigned.setValue(report.getAssigned());

            if (report.getVersion() == null) {
                version.setPlaceholder("No Version");
                projectVersion.setText("");
            }
            else {
                version.setValue(report.getVersion());
                projectVersion.setText(report.getVersion().toString());
            }
        }

        binder.bindInstanceFields(this);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        revert.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
        revert.addClickShortcut(Key.ESCAPE);
        //Button Actions
        save.addClickListener(buttonClickEvent -> {
            validateAndSave();
            UI.getCurrent().navigate(BugrapViewImpl.class);
        });

        revert.addClickListener(buttonClickEvent -> {
            // route to main page
            UI.getCurrent().navigate(BugrapViewImpl.class);
        });




        HorizontalLayout level1 = new HorizontalLayout(projectName,projectVersion);
        level1.setWidthFull();
        projectVersion.getElement().getStyle().set("margin-left", "auto");
        add(level1);
        add(summary);
        HorizontalLayout buttons = new HorizontalLayout(save,revert);
        HorizontalLayout level2 = new HorizontalLayout( priority,
                type,
                status,
                assigned,
                version,
                buttons
        );
        level2.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        level2.setWidthFull();
        buttons.getStyle().set("margin-left","auto");
        HorizontalLayout descLayout = new HorizontalLayout(description);
        descLayout.setWidth("50%");
        description.setWidthFull();
        add(level2, descLayout);

        commentList = bugrapPresenter.requestCommentsByReport(report);
        commentList.forEach(comment -> commentLayout.add(getCommentLayout(comment)));

        commentPanel = new CommentPanel(report);
        commentPanel.addListener(CommentPanel.SaveEvent.class, this::saveComment);

        add(getContent());
    }

    public void saveComment(CommentPanel.SaveEvent event){

        Comment c = event.getComment();

        c.setAuthor(bugrapPresenter.getUser(username));
        bugrapPresenter.saveComment(event.getComment());
        commentLayout.add(getCommentLayout(c));
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(commentLayout,commentPanel);
        content.setFlexGrow(4,commentLayout);
        content.setFlexGrow(1,commentPanel);
        content.setSizeFull();
        return content;
    }

    private Component getCommentLayout(Comment comment){
        HorizontalLayout comLayout = new HorizontalLayout();
        comLayout.setWidthFull();
        comLayout.getStyle().set("border","1px solid gray").set("border-radius","3px");
        Label commentDetail = new Label();
        commentDetail.add(new Html("<span>" + comment.getComment() + "</span>"));
        commentDetail.getStyle().set("padding","10px");
        comLayout.add(commentDetail);

        VerticalLayout commentInfo = new VerticalLayout();
        commentInfo.setSizeFull();
        commentInfo.setAlignItems(Alignment.END);
        Avatar avatarAuthor = new Avatar(comment.getAuthor().getName());

        avatarAuthor.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatarAuthor.setColorIndex(ThreadLocalRandom.current().nextInt(1, 8));
        VerticalLayout userInf = new VerticalLayout(new Paragraph(comment.getAuthor().getName()),new Paragraph( new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(comment.getTimestamp())));
        userInf.setPadding(false);

        commentInfo.add(new HorizontalLayout(avatarAuthor, userInf));
        commentInfo.add(new Paragraph(comment.getAttachmentName()));
        comLayout.add(commentInfo);

        return comLayout;
    }

    private void validateAndSave() {
        binder.writeBeanIfValid(report);
        bugrapPresenter.saveReport(report);
    }

    // if user tries to access edit page without selection of a report then reroute to home
    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        report = BugrapViewImpl.getSelectedReport();
        if(report == null)
            UI.getCurrent().navigate(BugrapViewImpl.class);
    }


}

