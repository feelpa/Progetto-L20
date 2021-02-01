package it.unipv.ingsw.l20PollingSystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/about", "/register/**", "/forgot-password/**", "/confirm-reset/**", "/reset-password/**",
                        "/CSS/**", "/JS/**", "/img/**").permitAll()
                .antMatchers("/user/{username}/**").access("@webSecurity.checkUsername(#username)")
                .antMatchers("/poll/{id}").access("@webSecurity.checkPollAuth(#id)")
                .antMatchers("/edit/poll/{id}/**").access("@webSecurity.isCreator(#id)")
                .anyRequest().access("hasRole('ROLE_USER')")

                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/user")
                    .failureUrl("/login?error=true")

                .and()
                .logout()
                    .permitAll()
                    .logoutSuccessUrl("/")
                ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder())
                ;
    }
}
