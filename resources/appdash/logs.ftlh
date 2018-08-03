<#include "_common.ftlh">

<@apppage tab="logs">
    <iframe id="log-frame" src="apps/${app.name()}/logs/stdio.log" class="box" style="width: 100%; height: 100%">
    </iframe>
    <script>
        function scrollToBottom() {
            var frame = document.getElementById("log-frame");
            frame.contentWindow.scrollTo(0, frame.scrollHeight);
        }
        body.addEventListener("load", scrollToBottom, false);
    </script>
</@apppage>