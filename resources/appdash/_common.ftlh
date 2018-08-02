<#macro page title>
    <!doctype html>
    <head>
        <meta charset="utf-8">
        <base href="/">
        <title>${title!"Appdash"}</title>
        <link rel="stylesheet" href="webjars/bulma/0.7.1/css/bulma.min.css">
    </head>
    <body>
        <div class="container section">
            <div class="columns">
                <div class="column is-2 is-sidebar-menu is-hidden-mobile">
                    <aside class="menu">
                        <p class="menu-label">
                            Apps
                        </p>
                        <ul class="menu-list">
                <#list apps as appName>
                    <li><a href="apps/${appName}"
                        <#if app?? && app.name() == appName>class="is-active"</#if>>
                        ${appName}</a></li>
                </#list>
                        </ul>
                    </aside>
                </div>
                <div class="column">
                    <#assign flash = request.flash()!"">
                    <#if flash?has_content>
                        <div class="notification is-info" style="white-space: pre-wrap">${flash}</div>
                    </#if>

                    <#nested>
                </div>
            </div>
        </div>
    </body>
</#macro>

<#macro apppage tab>
    <@page title="${tab?capitalize} - ${app.name()?capitalize}">
        <div class="tabs">
            <ul>
                <li<#if tab == "overview"> class="is-active"</#if>><a href="apps/${app.name()}">Overview</a></li>
                <li<#if tab == "config"> class="is-active"</#if>><a href="apps/${app.name()}/config">Config</a></li>
                <li<#if tab == "logs"> class="is-active"</#if>><a href="apps/${app.name()}/logs">Logs</a></li>
            </ul>
        </div>

        <#nested>
    </@page>
</#macro>