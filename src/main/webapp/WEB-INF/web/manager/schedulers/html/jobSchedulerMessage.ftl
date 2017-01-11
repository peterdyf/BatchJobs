<#import "/spring.ftl" as spring />
<#escape x as x?html>

<div id="message">
	<#if message??>
			${message}
	</#if>
</div><!-- message -->
</#escape>