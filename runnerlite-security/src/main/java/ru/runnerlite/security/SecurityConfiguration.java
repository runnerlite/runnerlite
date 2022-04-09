package ru.runnerlite.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    public void authConfig(AuthenticationManagerBuilder auth,
                           PasswordEncoder passwordEncoder,
                           UserAuthService userAuthService) throws Exception {

        //для тестирования
        auth.inMemoryAuthentication()
                .withUser("test_admin")
                .password(passwordEncoder.encode("qwerty123")) //шифрование пароля в оперативной памяти
                .roles("ADMIN")
                .and()
                .withUser("test_user")
                .password(passwordEncoder.encode("123qwerty"))
                .roles("USER");
        auth.userDetailsService(userAuthService);
    }


    @Configuration
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private CookieCsrfTokenRepository cookieCsrfTokenRepository() {
            CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
            csrfTokenRepository.setCookieHttpOnly(false);
            csrfTokenRepository.setCookiePath("/");
            return csrfTokenRepository;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    //TODO Заполнить маппинг страниц доступных пользователям с ролями
                    //.antMatchers("/admin/**").hasRole("ADMIN")
                    //antMatchers("/").hasRole("USER")
                    .anyRequest().authenticated()//TODO authenticated()

                    //TODO Добавить актуальную страницу авторизации
                    /*
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    */

                    //TODO Добавить страницу выхода
                    /*
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    */

                    .and()
                    .httpBasic()
                    .authenticationEntryPoint((req, resp, exception) -> {
                        resp.setContentType("application/json");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.setCharacterEncoding("UTF-8");
                        resp.getWriter().println("{ \"error\": \"" + exception.getMessage() + "\" }");
                    })
                    .and()
                    .csrf()
                    .csrfTokenRepository(cookieCsrfTokenRepository());
        }
    }
}
