import models.{Tag, Binder, Bookmark}
val username = "afenner"

val allTags = Tag.findAll //all tags associated with bookmarks
val myTags = Tag.findByUsername(username) //tags associated with user's bookmarks

val binder = Binder.findByName("elementary-math").get
val binderTags = binder.tags
val thruT = binderTags.flatMap(a => allTags.filter(_.name == a)).distinct
val (thruTP,thruTnotP) =thruT.partition(a => a.username == username)

thruT
thruTP
thruTnotP

val myBookmarks = thruTP.map(a => Bookmark.findByKey(a.key).get).zip(thruTP)

val recBookmarks = thruTnotP.map(a => Bookmark.findByKey(a.key).get).zip(thruTnotP)