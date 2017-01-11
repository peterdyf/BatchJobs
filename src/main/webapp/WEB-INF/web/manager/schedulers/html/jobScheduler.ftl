<#import "/spring.ftl" as spring />
<#escape x as x?html>

<div id="scheduler">
	
	<#assign update_url><@spring.url relativeUrl="${servletPath}/schedulers/${scheduler.name}"/></#assign>
	<form id="updateForm" action="${update_url}" method="POST">
		<input type="hidden" name="_method" value="PUT"/>
		<#if cron??>
			<@spring.bind path="cron" />
			<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
		</#if>

		<#if message??>
			${message}
		</#if>
		<ol>
			<li>
				<label for="cron">Cron Expression</label>
				<@spring.formInput  "cron.cron" />
				<label for="update">Job name=${scheduler.name}</label><input id="launch" type="submit" value="Update" name="update" />
			</li>
		</ol>
		
	</form>
	
	<#assign delete_url><@spring.url relativeUrl="${servletPath}/schedulers/${scheduler.name}"/></#assign>
	<form id="deleteForm" action="${delete_url}" method="POST">
		<input type="hidden" name="_method" value="DELETE"/>
		<input id="launch" type="submit" value="Delete" name="delete" />
	</form>
</div><!-- scheduler -->
</#escape>