package net.peter.batch.web.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

	private static final String LOGIN_PAGE = "loginPage";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String init(ModelMap model, @ModelAttribute("login") LoginRequest login, Errors errors) {
		return LOGIN_PAGE;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
	public String login(ModelMap model, @ModelAttribute("login") LoginRequest login, HttpServletRequest request, Errors errors) {

		if ("weblogic".equals(login.getName()) && "welcome1".equals(login.getPwd())) {
			request.getSession().setAttribute(AclInterceptor.LOGIN_FLAG, true);
			return "redirect:/";
		}
		errors.reject("login.invalid", new Object[] {}, "Invalid Login");
		return init(model, login, errors);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model, @ModelAttribute("login") LoginRequest login, HttpServletRequest request, Errors errors) {
		request.getSession().setAttribute(AclInterceptor.LOGIN_FLAG, null);
		return init(model, login, errors);
	}

}