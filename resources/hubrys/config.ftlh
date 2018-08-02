<#include "_common.ftlh">

<@apppage tab="config">

    <div class="field">
        <div class="control">
            <textarea class="textarea" rows="15">${app.config()}</textarea>
        </div>
    </div>

    <div class="field is-grouped">
        <div class="control">
            <button class="button is-primary">Save</button>
        </div>
    </div>
</@apppage>