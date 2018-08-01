<#include "layout.ftlh">
<table>
    <#list apps as app>
        <tr><td><a href="apps/${app}">${app}</a></td></tr>
    </#list>
</table>