<#macro page>

<base href="/">
<link rel="stylesheet" href="webjars/bulma/0.7.1/css/bulma.min.css">

<div class="container">
    <div class="columns">
        <div class="column is-2 is-sidebar-menu is-hidden-mobile">
            <aside class="menu">
                <ul class="menu-list">
                    <li><a class="is-active">bamboo</a></li>
                </ul>
            </aside>
        </div>
        <div class="column">
        <#nested>
        </div>
    </div>
</div>


</#macro>