package org.tatu.bugrap.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.tatu.bugrap.views.LoginView;
import org.vaadin.bugrap.domain.PasswordHash;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurityConfigurerAdapter {

    private ReporterRepository reporterRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Autowired
    public SecurityConfig(ReporterRepository reporterRepository) {
        this.reporterRepository = reporterRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**");
        super.configure(web);
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService(){

        List<Reporter> reporters = reporterRepository.findAll();
        List<UserDetails> users = new ArrayList<>();

        for(Reporter reporter : reporters)
        {
           UserDetails user = null;
           try {
            user = User.withUsername(reporter.getName()).password(reporter.getPassword()).roles("USER").build();
            }catch (Exception e) {
               e.printStackTrace();
           }
           users.add(user);
        }

        UserDetails user = null, user2 = null, user3 = null;

        try {
            user = User.withUsername("user")
                    .password(PasswordHash.createHash("123456"))
                    .roles("USER")
                    .build();
            user2 = User.withUsername("melih")
                    .password(PasswordHash.createHash("1"))
                    .roles("USER")
                    .build();
            user3 = User.withUsername("tanja")
                    .password(PasswordHash.createHash("1"))
                    .roles("USER")
                    .build();
            users.add(user);
            users.add(user2);
            users.add(user3);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return new InMemoryUserDetailsManager(users);
    }
}
