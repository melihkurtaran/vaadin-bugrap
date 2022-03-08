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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**");
        super.configure(web);
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService(){


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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return new InMemoryUserDetailsManager(user,user2,user3);
    }
}
