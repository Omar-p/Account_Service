package account.security;

import account.exceptions.handlers.CustomizedAccessDeniedExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.sql.DataSource;

import static account.security.ApplicationUserAuthority.EVENT_READ;
import static account.security.ApplicationUserAuthority.PAYMENT_READ;
import static account.security.ApplicationUserRole.*;

@EnableWebSecurity
public class Configuration extends WebSecurityConfigurerAdapter {
  
  
  @Autowired
  private DataSource dataSource;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
        .dataSource(dataSource);
    //.authoritiesByUsernameQuery("select username,authority from authorities where upper(username) = upper(?)");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/api/auth/signup").permitAll()
        .antMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
        .antMatchers(HttpMethod.POST, "/api/auth/**").hasAnyRole(USER.name(), ACCOUNTANT.name(), ADMINISTRATOR.name())
        .antMatchers(HttpMethod.GET, "/api/empl/payment*").hasAuthority(PAYMENT_READ.getAuthority())
        .antMatchers(HttpMethod.POST, "/api/acct/payments").hasAnyRole(ACCOUNTANT.name())
        .antMatchers(HttpMethod.PUT,  "/api/acct/payments").hasAnyRole(ACCOUNTANT.name())
        .antMatchers(HttpMethod.GET,  "/api/security/events/").hasAuthority(EVENT_READ.getAuthority())
        .antMatchers( "/api/admin/**").hasAnyRole(ADMINISTRATOR.name())
        .anyRequest().denyAll()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        .httpBasic().authenticationEntryPoint(getEntryPoint());
  }

  @Bean
  public JdbcUserDetailsManager jdbcUserDetailsManager() {
    return new JdbcUserDetailsManager(dataSource);
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(13);
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new CustomizedAccessDeniedExceptionHandler();
  }

  @Bean
  public AuthenticationEntryPoint getEntryPoint() {
    return new RestAuthenticationEntryPoint();
  }
}
