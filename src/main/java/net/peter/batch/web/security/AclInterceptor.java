package net.peter.batch.web.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AclInterceptor extends HandlerInterceptorAdapter {

	public static final String LOGIN_FLAG = "loginFlag";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		if (Boolean.TRUE.equals(request.getSession().getAttribute(LOGIN_FLAG))) {
			return true;
		}
		response.sendRedirect(request.getContextPath() + "/login/");
		return false;
	}
}