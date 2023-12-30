package com.s_c_m.smart_contect_manager.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class MyConfig
{
	@Autowired
	private UserDetailsService userdetailsservice;
    @Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
    
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userdetailsservice);
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return daoAuthenticationProvider;
	}
	// @Bean
	// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	// {
    //     http
    //             .authorizeHttpRequests(requests -> requests
    //                     .requestMatchers("/admin/**").hasRole("ADMIN")
    //                     .requestMatchers("/user/**").hasRole("USER")
    //                     .requestMatchers("/**").permitAll())
	// 					.formLogin(login -> login.loginPage("/login")) 
    //             .csrf(csrf -> csrf.disable())
    //             .authenticationProvider(authenticationProvider());
	// 	 DefaultSecurityFilterChain build= http.build();
	// 	 return build;
	// }
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/**").permitAll())
						// .formLogin(login -> login.loginPage("/singin").defaultSuccessUrl("/user/dashboard"))
						// .formLogin(
						// 	login -> login.loginPage("/signin")
						// 	// .loginProcessingUrl("/dologin")
						// 	.defaultSuccessUrl("/user/index")

						// ) 
						.formLogin(login -> login
						.loginPage("/signin")
						.defaultSuccessUrl("/") // Default success URL for users with unknown roles
						.successHandler((request, response, authentication) -> {
							// Customize the success URL based on the user's role
							if (authentication.getAuthorities().stream()
									.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
								response.sendRedirect("/admin/index");
							} else if (authentication.getAuthorities().stream()
									.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
								response.sendRedirect("/user/index");
							} else {
								response.sendRedirect("/default");
							}
						})
					)
						.csrf(csrf -> csrf
                .disable());
		 http.authenticationProvider(authenticationProvider());
		 DefaultSecurityFilterChain build= http.build();
		 return build;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager();
	}
	
	
}