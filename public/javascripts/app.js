$(function() {
  var updateSchema = function() {
    $.get("/render-schema")
    .done(function (res) {
      $("#schema pre").html(res)
    })
    .fail(function (error) {
      console.error(error)
    })
  }

  var editorTheme = "ace/theme/github"
  var initialQuery = "query HeroAndFriends {\n  hero {\n    name\n    friends {\n      name\n    }\n  }\n}"

  var createEditor = function (id, mode, readOnly) {
    var editor = ace.edit(id);

    editor.setTheme(editorTheme)
    editor.getSession().setOptions({
      mode: "ace/mode/" + mode,
      tabSize: 2,
      useSoftTabs: true
    });
    editor.setDisplayIndentGuides(true)
    editor.setFontSize(17)

    if (readOnly) editor.setReadOnly(readOnly)

    return editor
  }

  updateSchema()

  var queryEditor = createEditor("queryEditor", "text")
  var variablesEditor = createEditor("variablesEditor", "json")
  var responseEditor = createEditor("responseEditor", "json", true)

  queryEditor.setValue(initialQuery, -1)

  var execute = function () {
    var queryParams = {
      query: queryEditor.getValue()
    }

    if (variablesEditor.getValue().trim() !== '')
      queryParams.args = variablesEditor.getValue().trim()

    if ($("#operation").val() && $("#operation").val().trim !== '')
      queryParams.operation = $("#operation").val()

    var url = $("#graphqlUrl").val()


    $.get(url, queryParams)
    .done(function (res) {
      responseEditor.setValue(JSON.stringify(res, null, 2), -1)

      if (res.errors && res.errors.length > 0) {
        $("#responseError").html("")

        for (var i = 0; i < res.errors.length; i++) {
          $("#responseError").append($("<pre>").html(res.errors[i].message))
        }

        $("#errors").collapse('show')
      } else {
        $("#errors").collapse('hide')
      }

      $("#response").collapse('show')
    })
    .fail(function (error) {
      if (error.status === 400 && error.responseJSON && error.responseJSON.syntaxError)
      $("#responseError").html($("<pre>").html(error.responseJSON.syntaxError))

      $("#response").collapse('hide')
      $("#errors").collapse('show')

      console.error(error)
    })
  }

  var showShortcuts = function () {
    $("#hotkeysDialog").modal()
  }

  var toggleVariables = function () {
    $("#variablesSection").collapse('toggle')
  }

  var toggleConfig = function () {
    $("#configSection").collapse('toggle')
  }

  $("#runButton").on("click", execute)
  $(document).bind("keydown", "shift+/", showShortcuts)
  $(document).bind("keydown", "ctrl+return", execute)
  $(document).bind("keydown", "v", toggleVariables)
  $(document).bind("keydown", "c", toggleConfig)
  $(".ace_editor textarea").bind("keydown", "ctrl+return", execute)
})