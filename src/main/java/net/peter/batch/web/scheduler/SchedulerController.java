package net.peter.batch.web.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.peter.batch.exception.InvalidCronExpressionException;
import net.peter.batch.exception.JobInstanceNotExsitsException;
import net.peter.batch.exception.JobSchedulerExsitsException;
import net.peter.batch.exception.JobSchedulerNotExsitsException;
import net.peter.batch.scheduler.QuartzService;
import net.peter.batch.scheduler.SchedulerInfo;

@Controller
@RequestMapping(value = "schedulers")
public class SchedulerController {

	private static final String SCHEDULER_VIEW = "schedulerView";
	private static final String SCHEDULER_MESSAGE = "schedulerMessage";
	private static final String SCHEDULER_NEW = "schedulerNew";
	private static final String SCHEDULERS_VIEW = "schedulersView";

	private static final String PARAM_MESSAGE = "message";

	@Autowired
	private QuartzService quartzService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("schedulers", quartzService.queryAll());
		return SCHEDULERS_VIEW;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String initAdd(ModelMap model, @ModelAttribute("scheduler") SchedulerNewRequest newRequest, Errors errors) {

		List<String> noSchedulerInstanceNames = quartzService.queryWithoutScheduler().stream().sorted().collect(Collectors.toList());
		model.addAttribute("options", noSchedulerInstanceNames);

		return SCHEDULER_NEW;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String add(ModelMap model, @ModelAttribute("scheduler") SchedulerNewRequest newRequest, Errors errors) {

		try {
			quartzService.add(newRequest.getName(), newRequest.getCron());
		} catch (InvalidCronExpressionException e) {
			errors.reject("cron.invalid", new Object[] { newRequest.getName(), newRequest.getCron() }, e.getMessage());
			return initAdd(model, newRequest, errors);
		} catch (JobSchedulerExsitsException e) {
			errors.reject("scheduler.exsits", new Object[] { newRequest.getName() }, e.getMessage());
			return initAdd(model, newRequest, errors);
		} catch (JobInstanceNotExsitsException e) {
			errors.reject("instance.not.exsits", new Object[] { newRequest.getName() }, e.getMessage());
			return initAdd(model, newRequest, errors);
		}

		model.addAttribute(PARAM_MESSAGE, String.format("Success create [%s] with cron[%s]", newRequest.getName(), newRequest.getCron()));
		return SCHEDULER_MESSAGE;
	}

	@RequestMapping(value = "/{jobName}", method = RequestMethod.GET)
	public String details(ModelMap model, @ModelAttribute("jobName") String jobName, @ModelAttribute("cron") SchedulerUpdateRequest cronRequest, Errors errors) {

		try {
			SchedulerInfo scheduler = quartzService.query(jobName);
			model.addAttribute("scheduler", scheduler);
			cronRequest.setCron(scheduler.getCron());
		} catch (JobSchedulerNotExsitsException e) {
			model.addAttribute(PARAM_MESSAGE, e.getMessage());
			return SCHEDULER_MESSAGE;
		}

		return SCHEDULER_VIEW;
	}

	@RequestMapping(value = "/{jobName}", method = RequestMethod.PUT)
	public String update(ModelMap model, @ModelAttribute("jobName") String jobName, @ModelAttribute("cron") SchedulerUpdateRequest cronRequest, Errors errors) {

		try {
			quartzService.modify(jobName, cronRequest.getCron());
		} catch (InvalidCronExpressionException e) {
			errors.reject("cron.invalid", new Object[] { jobName, cronRequest.getCron() }, e.getMessage());
			return details(model, jobName, cronRequest, errors);
		} catch (JobSchedulerNotExsitsException e) {
			model.addAttribute(PARAM_MESSAGE, e.getMessage());
			return SCHEDULER_MESSAGE;
		}

		model.addAttribute(PARAM_MESSAGE, String.format("Success change [%s] cron to [%s]", jobName, cronRequest.getCron()));

		return SCHEDULER_MESSAGE;
	}

	@RequestMapping(value = "/{jobName}", method = RequestMethod.DELETE)
	public String delete(ModelMap model, @ModelAttribute("jobName") String jobName, Errors errors) {

		try {
			quartzService.remove(jobName);
		} catch (JobSchedulerNotExsitsException e) {
			model.addAttribute(PARAM_MESSAGE, e.getMessage());
			return SCHEDULER_MESSAGE;
		}

		model.addAttribute(PARAM_MESSAGE, String.format("Success delete [%s]", jobName));
		return SCHEDULER_MESSAGE;
	}

}