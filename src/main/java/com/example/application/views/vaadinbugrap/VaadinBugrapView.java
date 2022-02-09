package com.example.application.views.vaadinbugrap;

import com.example.application.views.about.AboutView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.security.RolesAllowed;

@PageTitle("Vaadin Bugrap")
@Route(value = "vaadin")
@RouteAlias(value = "")
@RolesAllowed("user")
public class VaadinBugrapView extends VerticalLayout{

    public VaadinBugrapView() {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H2("Melih's Bugrap Project will be here soon!"));
        add(new Paragraph("Wait for it! \uD83D\uDE0E"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new RouterLink("About", AboutView.class));
        add(new RouterLink("Vaadin", VaadinBugrapView.class));
    }

}
