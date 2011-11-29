<#import "/spring.ftl" as spring />


<h2>Whirlwind Operation</h2>

<table class="dl">
    <tr>
        <td>Label</td>
        <td>${operation.label?html}</td>
    </tr>
    <#if operation.parameters?has_content>
        <tr>
            <td>Args</td>
            <td>
                <ol>
                    <#list operation.parameters as parameter>
                        <li>${parameter?html}</li>
                    </#list>
                </ol>
            </td>
        </tr>
    </#if>
</table>
