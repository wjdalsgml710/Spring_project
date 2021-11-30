package com.example.sproject.handler;
// 로그인 실패시 처리하는 로직(설정)
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		System.out.println("--com.example.sproject.handler.CustomFailureHandler");
		exception.printStackTrace();
		request.getRequestDispatcher("/login/login").forward(request, response);
	}

}
