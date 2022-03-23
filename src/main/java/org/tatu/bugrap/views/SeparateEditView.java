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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.annotation.security.PermitAll;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@PageTitle("Edit Report")
@Route(value = "Edit")
@PermitAll
public class SeparateEditView extends VerticalLayout implements AfterNavigationObserver
{
    private H3 projectName = new H3("");
    private H3 projectVersion = new H3("");
    private H3 summary = new H3("");
    private Report report;
    private Reporter author;
    private Date date = new Date();

    private Binder<Report> binder = new BeanValidationBinder<>(Report.class);

    private TextArea description = new TextArea("Description");
    private ComboBox<Report.Priority> priority = new ComboBox<>("Priority");
    private ComboBox<Report.Type> type = new ComboBox<>("Type");
    private ComboBox<Report.Status> status = new ComboBox<>("Status");
    private ComboBox<ProjectVersion> version = new ComboBox<>("Version");
    private ComboBox<Reporter> assigned = new ComboBox<>("Assigned to");
    private Button save = new Button("Save Changes");
    private Button revert = new Button("Revert", new Icon(VaadinIcon.ROTATE_LEFT));
    private BugrapPresenter bugrapPresenter;

    private List<Comment> commentList;
    private VerticalLayout commentLayout = new VerticalLayout();
    private CommentPanel commentPanel;

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

        if(report != null) {
            projectName.setText(report.getProject().getName());
            summary.setText(report.getSummary());
            description.setValue(report.getDescription());
            priority.setValue(report.getPriority());
            status.setValue(report.getStatus());
            type.setValue(report.getType());
            author = report.getAuthor();
            date = report.getReportedTimestamp();

            version.setItems(bugrapPresenter.requestProjectVersionsByProject(report.getProject()));
            version.setItemLabelGenerator(ProjectVersion::getVersion);

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
        binder.readBean(report);
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


        if(author == null)
        {
            author = new Reporter();
            author.setName("unknown");
        }
        VerticalLayout reportInfo = new VerticalLayout();
        reportInfo.setSizeFull();
        reportInfo.setAlignItems(Alignment.END);
        Avatar avatarAuthor = new Avatar(author.getName());

        avatarAuthor.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatarAuthor.setColorIndex(ThreadLocalRandom.current().nextInt(1, 8));
        VerticalLayout userInf = new VerticalLayout(new Paragraph(author.getName()),new Paragraph( new java.text.SimpleDateFormat("MM/dd/yyyy h:mm").format(date)));

        userInf.setPadding(false);
        reportInfo.add(new HorizontalLayout(avatarAuthor, userInf));


        HorizontalLayout buttons = new HorizontalLayout(save,revert);
        FlexLayout flexLayout = new FlexLayout(priority, type, status,assigned, version, buttons);
        for (int i=0;i<flexLayout.getComponentCount();i++)
            flexLayout.getComponentAt(i).getElement().getStyle().set("margin","10px");
        flexLayout.addClassName("responsive-layout");
        flexLayout.getStyle().set("overflow","auto");
        flexLayout.setAlignItems(Alignment.BASELINE);
        flexLayout.setWidthFull();


        buttons.getStyle().set("margin-left","auto");
        HorizontalLayout descLayout = new HorizontalLayout(description,reportInfo);
        descLayout.setWidthFull();
        buttons.getElement().getStyle().clear().set("margin-left", "auto");
        description.setWidthFull();
        description.setMaxLength(1500);

        add(flexLayout, descLayout);

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

    private Component getCommentLayout(Comment comment) {
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


        // attachment link
        if(comment.getAttachment() != null) {
            File file = new File(comment.getAttachmentName());
            try {
                FileOutputStream f_out = new FileOutputStream(file);
                f_out.write(comment.getAttachment());
            } catch (Exception e) {
                e.printStackTrace();
            }
            StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
            Anchor link = new Anchor(streamResource, (String.format("%s (%d KB)", file.getName(),
                    (int) file.length() / 1024) ));
            Icon fileIcon = new Icon(VaadinIcon.FILE_O);
            link.add(fileIcon);
            link.getElement().setAttribute("download", true);
            commentInfo.add(link);
        }

        comLayout.add(commentInfo);
        return comLayout;
    }


    private InputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return stream;
    }


    private void validateAndSave() {
        report.setTimestamp(new Date());
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

