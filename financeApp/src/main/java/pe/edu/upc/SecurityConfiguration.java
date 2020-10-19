package pe.edu.upc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import pe.edu.upc.service.JpaUserDetailService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private JpaUserDetailService userDetailService;

	@Bean
	public PasswordEncoder pEncoder() {
		return new BCryptPasswordEncoder();
	};
		
	@Bean
	public AuthenticationSuccessHandler mAuthSuccHadler() {
		return new MyAuthSuccHadler();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/index.html","/css/**","/js/**").permitAll()
			.anyRequest().authenticated().and()
			.formLogin()
				.loginPage("/login")
				.successHandler(mAuthSuccHadler())
				//.loginProcessingUrl("/perform_login")
				//.defaultSuccessUrl("/")
				.failureUrl("/login?error")
			.permitAll()
			.and().logout().logoutSuccessUrl("/login?logout")
			.permitAll()
			.and()	.exceptionHandling().accessDeniedPage("/denied")
			;
	}
	
	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception {
		build.userDetailsService(userDetailService).passwordEncoder(pEncoder());
	}
}
