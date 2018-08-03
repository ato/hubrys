<#macro page title="Appdash">
    <!doctype html>
    <head>
        <meta charset="utf-8">
        <base href="/">
        <title>${title}</title>
        <link rel="stylesheet" href="webjars/bulma/0.7.1/css/bulma.min.css">
    </head>
    <body>
        <div class="container">
            <nav class="navbar" role="navigation" aria-label="main navigation">
                <div class="navbar-brand">
                    <a class="navbar-item" href=".">
                        <strong>AppDash</strong>
                    </a>
                </div>
                <div class="navbar-menu">
                    <div class="navbar-end">
                        <!--
                        <div class="navbar-item">
                            <div class="field is-grouped">
                                <p class="control">
                                <form action="logout" method="post">
                                    <button class="button" type="submit">Logout</button>
                                </form>
                                </p>
                            </div>
                        </div>
                        -->
                    </div>
                </div>
            </nav>
            <div class="">
                <div class="columns">
                    <div class="column is-2 is-sidebar-menu is-hidden-mobile">
                        <aside class="menu">
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
        </div>
    </body>
</#macro>

<#macro apppage tab>
    <@page title="${tab?capitalize} - ${app.name()?capitalize}">
        <div class="tabs">
            <ul>
                <li<#if tab == "overview"> class="is-active"</#if>><a href="apps/${app.name()}">Overview</a></li>
                <li<#if tab == "config"> class="is-active"</#if>><a href="apps/${app.name()}/config">Config</a></li>
                <li<#if tab == "deploy"> class="is-active"</#if>><a href="apps/${app.name()}/deploy">Deploy</a></li>
                <li<#if tab == "logs"> class="is-active"</#if>><a href="apps/${app.name()}/logs">Logs</a></li>
            </ul>
        </div>

        <#nested>
    </@page>
</#macro>