<#include "_common.ftlh">

<@apppage tab="deploy">
    <div class="field is-grouped">
        <div class="control">
            <form action="apps/${app.name()}/deploy" method="post">
                <button class="button is-primary" type="submit">Deploy</button>
            </form>
        </div>
    </div>
    <iframe id="log-frame" src="apps/${app.name()}/logs/deploy.log" class="box" style="width: 100%; height: 100%">
    </iframe>
    <script>
        window.setInterval("reloadLog();", 2000);

        function reloadLog() {
            document.getElementById("log-frame").contentWindow.location.reload();
        }
    </script>
</@apppage>