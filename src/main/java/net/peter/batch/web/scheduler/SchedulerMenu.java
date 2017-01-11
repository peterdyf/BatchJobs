package net.peter.batch.web.scheduler;

import org.springframework.batch.admin.web.resources.BaseMenu;
import org.springframework.stereotype.Component;

@Component
public class SchedulerMenu extends BaseMenu {

	public SchedulerMenu() {
		super("/schedulers/", "Schedulers", 6);
	}
	
}