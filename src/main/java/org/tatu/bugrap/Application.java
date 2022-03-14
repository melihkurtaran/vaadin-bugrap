package org.tatu.bugrap;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.tatu.bugrap.views.BugrapPresenter;
import org.vaadin.bugrap.domain.spring.DBTools;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@Theme(value = "bugrap")
@PWA(name = "Bugrap", shortName = "Bugrap", offlineResources = { "images/logo.png" })
@NpmPackage(value = "line-awesome", version = "1.3.0")
@ComponentScan({"org.vaadin.bugrap.domain.spring","org.tatu.bugrap"})
@Viewport("width=device-width, initial-scale=1")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

	@Autowired
	private DBTools dbTools;

	@PostConstruct
	protected void onInit() {
		dbTools.clear();
		dbTools.create();

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
