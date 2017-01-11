package net.peter.batch.web.security;

import org.springframework.batch.admin.web.resources.BaseMenu;
import org.springframework.stereotype.Component;

@Component
public class LogoutMenu extends BaseMenu {

	public LogoutMenu() {
		super("/login/logout", "Logout", 7);
	}
	
}