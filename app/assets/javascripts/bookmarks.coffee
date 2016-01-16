jQuery ($) ->

  $table = $('.container table')
  bookmarkListUrl = $table.data('list')

  loadBookmarkTable = ->
    $.get bookmarkListUrl, (bookmarks) ->  # make Ajax GET request
      $.each bookmarks, (index, bookmarkKey) ->
        row = $('<tr/>').append $('<td/>').text(bookmarkKey)
        row.attr 'contenteditable', true
        $table.append row
        loadBookmarkDetails row

  bookmarkDetailsUrl = (bookmarkKey) ->
    $table.data('details').replace "Nil", bookmarkKey

  loadBookmarkDetails = (tableRow) ->
    bookmarkKey = tableRow.text()
    $.get bookmarkDetailsUrl(bookmarkKey), (bookmark) ->
      tableRow.append $('<td/>').text(bookmark.url)
      tableRow.append $('<td/>').text(bookmark.username)
      tableRow.append $('<td/>').text(bookmark.title)
      tableRow.append $('<td/>').text(bookmark.description)
      tableRow.append $('<td/>').text(bookmark.favicon)
      tableRow.append $('<td/>')

  loadBookmarkTable()

  saveRow = ($row) ->
    [key, url, username, title, description, favicon] = $row.children().map -> $(this).text()
    bookmark =
      key: key
      url: url
      username: username
      title: title
      description: description
      favicon: favicon
    jqxhr = $.ajax
      type: "PUT"
      url: bookmarkDetailsUrl(key)
      contentType: "application/json"
      data: JSON.stringify bookmark
    jqxhr.done (response) ->
      $label = $('<span/>').addClass('label label-success')
      $row.children().last().append $label.text(response)
      $label.delay(3000).fadeOut()
    jqxhr.fail (data) ->
      $label = $('<span/>').addClass('label label-important')
      message = data.responseText || data.statusText
      $row.children().last().append $label.text(message)

  $table.on 'focusout', 'tr', () ->
    saveRow $(this)