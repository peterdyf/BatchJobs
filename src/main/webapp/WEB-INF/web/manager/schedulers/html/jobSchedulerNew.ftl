<#import "/spring.ftl" as spring />
<#escape x as x?html>

<div id="scheduler">
	
	<#assign new_url><@spring.url relativeUrl="${servletPath}/schedulers/new"/></#assign>

	<form id="newForm" action="${new_url}" method="POST">
		<#if scheduler??>
			<@spring.bind path="scheduler" />
			<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
		</#if>

		<#if message??>
			${message}
		</#if>
		<ol>
			<li>
				<label for="name">Job Name</label>
				<@spring.bind "options" />
				<@spring.formSingleSelect "scheduler.name", options, " " />
			</li>
			<li>
				<label for="cron">Cron Expression</label>
				<@spring.formInput  "scheduler.cron" />
				<input id="launch" name="submit" type="submit" value="Create"  />
			</li>
		</ol>
		
	</form>
</div><!-- scheduler -->
</#escape>