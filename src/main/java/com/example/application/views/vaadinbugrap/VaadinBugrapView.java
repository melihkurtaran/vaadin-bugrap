package com.example.application.views.vaadinbugrap;

import com.example.application.views.about.AboutView;
import com.example.application.views.login.LoginView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import javax.annotation.security.RolesAllowed;

@PageTitle("Vaadin Bugrap")
@Route(value = "vaadin")
@RouteAlias(value = "")
@RolesAllowed("user")
public class VaadinBugrapView extends VerticalLayout{

    public VaadinBugrapView() {
        setSpacing(false);

        HorizontalLayout MenuBar = new HorizontalLayout();
        MenuBar.setWidth("20%");
        MenuBar.add(new RouterLink("About", AboutView.class));
        MenuBar.add(new RouterLink("Vaadin", VaadinBugrapView.class));
        add(MenuBar);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H2("Melih's Bugrap Project will be here soon!"));
        add(new Paragraph("Wait for it! \uD83D\uDE0E"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");


    }
    /*
    @Override
    public void beforeEnter(BeforeEnterEvent event)
    {
        if(  VaadinSession.getCurrent().getAttribute("userLoggedIn") == null) {
            VaadinSession.getCurrent().setAttribute("intendedPath",event.getLocation().getPath());
            event.rerouteTo(LoginView.class);
        }
    }

     */

}
