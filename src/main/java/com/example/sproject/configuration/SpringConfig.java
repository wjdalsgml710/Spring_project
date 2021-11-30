package com.example.sproject.configuration;
// 스프링 시큐리티 사용 설정 (필터단에서 사용하는 것을 설정하는 곳, 서버 시작될때 전체적으로 실행은 됨)
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.sproject.handler.ExceptionHandlerFilter;
import com.example.sproject.model.login.Member;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringConfig extends WebSecurityConfigurerAdapter {
	public SpringConfig() {
		System.out.println("Method SpringConfig in Class SpringConfig");
	}
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	private UserDetailsService userDetailsService;
	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//세션 관리
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
	
	//자동로그인관련
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		System.out.println("-- Bean persistentTokenRepository");
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		System.out.println("dataSource: " + dataSource);
		return repo;
	}
	
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/**/css/**", "/**/js/**", "/**/img/**", "/**/lib/**", "/upload/**", "/summernoteImage/**", "/resource/**");
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        //URL 권한 체크 (권한없는 곳에서는 권한이 없다고 뜸, member_authorites, authorites 테이블 사용)
		http.authorizeRequests()
	        .antMatchers("/admin/**").hasRole("ADMIN") //admin을 가지는 것만 /admin/**에 들어갈수있다
	        .antMatchers("/**").authenticated();
			// 나머지 주소는 로그인한 사람들만 접근할 수 있도록
		//로그인 관련
		http.formLogin()
	        .loginPage("/login/login")
//	        .defaultSuccessUrl("/login")
	        .permitAll()
	        .usernameParameter(Member.usernameParameter)
	        .passwordParameter(Member.passwordParameter)
			.failureHandler(authenticationFailureHandler)
			.successHandler(authenticationSuccessHandler);
		
		http.logout()
	        .logoutRequestMatcher(new AntPathRequestMatcher("/login/logout"))
	        .logoutSuccessUrl("/login/login")
	        .invalidateHttpSession(true)
			.deleteCookies("remember-me", "JSESSIONID"); // 자동로그인 쿠키 삭제
		
		http.exceptionHandling()
	        .accessDeniedPage("/login/denied");
		
		//csrf 예외 처리 // 스프링 시큐리티 : 포스트 요청시마다 csrf 토큰 값을 요구해서 csrf 공격 방지 csrf토큰 값은 웹페이지 요청시마다 임의의 값으로 받는다
		http.csrf().ignoringAntMatchers("/**/test/**");
		
		//세션 관리 (동일한 아이디 로그인 한명밖에 못하게)
		http.sessionManagement()
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false) //동일한 아이디 로그인 한명으로 설정 -> false : 먼저 로그인한 사람 로그아웃 / true : 나중에 로그인한사람 로그인 못함
				.expiredUrl("/duplicated-login")
				.sessionRegistry(sessionRegistry);
		
		/* 로그인 상태 유지 기능 설정 */
		http.rememberMe() // 자동로그인만들때
		  .key("twkey")
		  .tokenRepository(persistentTokenRepository())
		  .tokenValiditySeconds(604800) //1주일
		  .userDetailsService(userDetailsService);
		
		/* 에러 핸들링 (CookieTheftException 등) */
		http.addFilterAfter(new ExceptionHandlerFilter(), SecurityContextHolderAwareRequestFilter.class);
	}
}
