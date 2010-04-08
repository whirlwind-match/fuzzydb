<#import "/spring.ftl" as spring />


<h2>Whirlwind Operation</h2>

<table class="dl">
    <tr>
        <td>Label</td>
        <td>${operation.label?html}</td>
    </tr>
<#if 0>
    <tr>
        <td>Location</td>
        <td><code>${operation.sqlAsFormattedHtml}</code></td>
    </tr>
    <#if operation.parameters?has_content>
        <tr>
            <td>Parameters</td>
            <td>
                <ol>
                    <#list operation.parameters as parameter>
                        <li>${parameter?html}</li>
                    </#list>
                </ol>
            </td>
        </tr>
    </#if>
    <#if operation.mappedParameterKeys?has_content>
        <tr>
            <td>Mapped Parameters</td>
            <td>
                <table>
                    <#list operation.mappedParameter?keys as key>
                        <tr>
                            <td class="label">${key?html}</td>
                            <td>${operation.mappedParameters[key]?html}</td>
                        </tr>
                    </#list>
                </table>
            </td>
        </tr>
    </#if>
</#if>
</table>
