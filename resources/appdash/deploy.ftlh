<#include "_common.ftlh">

<@apppage tab="logs">
    <div class="field is-grouped">
        <div class="control">
            <form action="apps/${app.name()}/deploy" method="post">
                <button class="button is-primary" type="submit">Deploy</button>
            </form>
        </div>
    </div>
    <iframe id="log-frame" src="apps/${app.name()}/logs/deploy.log" class="box" style="width: 100%; height: 100%">
    </iframe>
</@apppage>