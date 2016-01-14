jQuery ($) ->

  $table = $('.container table')
  bookmarkListUrl = $table.data('list')

  loadBookmarkTable = ->
    $.get bookmarkListUrl, (bookmarks) ->  # make Ajax GET request
      $.each bookmarks, (index, bookmarkKey) ->
        row = $('<tr/>').append $('<td/>').text(bookmarkKey)
        $table.append row
        loadBookmarkDetails row

  bookmarkDetailsUrl = (bookmarkKey) ->
    $table.data('details').replace "Nil", bookmarkKey

  loadBookmarkDetails = (tableRow) ->
    bookmarkKey = tableRow.text()

    $.get bookmarkDetailsUrl(bookmarkKey), (bookmark) ->
      tableRow.append $('<td/>').text(bookmark.username)
      tableRow.append $('<td/>').text(bookmark.url)
      tableRow.append $('<td/>').text(bookmark.title)
      tableRow.append $('<td/>').text(bookmark.description)

  loadBookmarkTable()