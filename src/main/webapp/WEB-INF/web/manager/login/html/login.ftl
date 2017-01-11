<#import "/spring.ftl" as spring />
<#escape x as x?html>

<div id="login">
	
	<#assign login_url><@spring.url relativeUrl="${servletPath}/login/login"/></#assign>

	<form id="loginForm" action="${login_url}" method="POST">
		<#if login??>
			<@spring.bind path="login" />
			<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
		</#if>

		<#if message??>
			${message}
		</#if>
		<ol>
			<li>
				<label for="cron">Name</label>
				<@spring.formInput  "login.name" />
			</li>
			<li>
				<label for="cron">Password</label>
				<@spring.formPasswordInput  "login.pwd" />
				<input id="launch" name="submit" type="submit" value="Login"  />
			</li>
		</ol>
		
	</form>
</div><!-- scheduler -->
</#escape>