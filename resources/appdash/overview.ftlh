<#include "_common.ftlh">

<@apppage tab="overview">

<table class="table">
    <tr>
        <td>Version:</td>
        <td>${app.version()}</td>
    </tr>
</table>

<div class="field is-grouped">
    <div class="control">
        <form action="apps/${app.name()}/restart" method="post">
            <button class="button is-primary" type="submit">Restart</button>
        </form>
    </div>
    <div class="control">
        <form action="apps/${app.name()}/stop" method="post">
            <button class="button" type="submit">Stop</button>
        </form>
    </div>
</div>
</@apppage>