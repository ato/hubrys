<#include "_common.ftlh">

<@apppage tab="config">

    <form method="post">
        <div class="field">
            <div class="control">
                <textarea name="config" class="textarea" rows="15">${app.config()}</textarea>
            </div>
        </div>

        <div class="field is-grouped">
            <div class="control">
                <button class="button is-primary" type="submit">Save</button>
            </div>
        </div>
    </form>
</@apppage>