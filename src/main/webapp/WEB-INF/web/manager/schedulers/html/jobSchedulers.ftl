<#import "/spring.ftl" as spring />
<div id="schedulers">
	
	<#if schedulers?? && schedulers?size!=0>
		
			<h2>Job Schedulers</h2>
			
			<table title="Jobs Schedulers" class="bordered-table">
				<tr>
					<th>Name</th>
					<th>Cron</th>
					<th>Next Fire Time</th>
				</tr>
				<#list schedulers as scheduler>
					<#if scheduler_index % 2 == 0>
						<#assign rowClass="name-sublevel1-even"/>
					<#else>
						<#assign rowClass="name-sublevel1-odd"/>
					</#if>
					<tr class="${rowClass}">
						<#assign job_url><@spring.url relativeUrl="${servletPath}/jobs/${scheduler.name}"/></#assign>
						<td><a href="${job_url}">${scheduler.name}</a></td>
						<#assign cron_url><@spring.url relativeUrl="${servletPath}/schedulers/${scheduler.name}"/></#assign>
						<td><a href="${cron_url}">${scheduler.cron}</a></td>
						<td>${scheduler.nextTime?string["yyyy/dd/MM HH:mm:ss"]}</td>
					</tr>
				</#list>
			</table>
	<#else>
		<p>There are no schedulers registered.</p>
	</#if>
	<#assign new_url><@spring.url relativeUrl="${servletPath}/schedulers/new"/></#assign>
	<form id="newForm" action="${new_url}" method="GET">
		<input id="launch" type="submit" value="Create" name="create" />
	</form>

</div><!-- schedulers -->