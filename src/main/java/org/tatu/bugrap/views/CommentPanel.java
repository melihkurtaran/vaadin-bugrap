package org.tatu.bugrap.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Date;

public class CommentPanel extends VerticalLayout {

    private Binder<Comment> binder = new BeanValidationBinder<>(Comment.class);
    private RichTextEditor commentArea = new RichTextEditor();

    private Button commentBtn = new Button("Comment");
    private Button cancelBtn = new Button("Cancel");
    private Report report;

    public CommentPanel(Report report){

        this.report = report;
        commentBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelBtn.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);

        add(commentArea);
        HorizontalLayout btnLayout = new HorizontalLayout(commentBtn,cancelBtn);
        add(btnLayout);

        commentBtn.addClickListener(buttonClickEvent -> saveComment());


    }

    private void saveComment()
    {
        Comment comment = new Comment();
        comment.setComment(commentArea.asHtml().getValue());
        comment.setTimestamp(new Date());
        comment.setReport(report);
        comment.setType(Comment.Type.COMMENT);
        commentArea.clear();
        fireEvent(new CommentPanel.SaveEvent(this,comment));
    }

    // Events
    public static abstract class CommentPanelEvent extends ComponentEvent<CommentPanel> {
        private Comment comment;

        protected CommentPanelEvent(CommentPanel source, Comment comment) {
            super(source, false);
            this.comment = comment;
        }

        public Comment getComment() {
            return comment;
        }
    }

    public static class SaveEvent extends CommentPanel.CommentPanelEvent {
        SaveEvent(CommentPanel source, Comment comment) {
            super(source, comment);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
