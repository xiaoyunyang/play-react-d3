package models

case class Binder(name: String, alias: String, description: String, username: String)
//European Article Number (EAN) identifier is an international article number

//Data access object
object Binder {
  var binders = Set(
    Binder("elementary-math", "Elementary Math", "Elementary math teaching notes", "afenner"),
    Binder("swift-dev", "Swift Dev", "Useful stackoverflow posts for mobile app dev in Swift","afenner"),
    Binder("story-time", "Story Time", "My favorite post from Jesse's Story Time","afenner"),
    Binder("scala-notes", "Scala Notes", "my evernote collection of scala learning notes", "xyang"),
    Binder("play-notes", "Learn Play Framework", "Best tutorial articles/Github projects for getting started with Play","xyang")
  )

  def findAll(username: String) = binders.filter(_.username == username).toList.sortBy(_.name)
  def findByName(name: String) = binders.find(_.name == name)
  //def findByUsername(username: String) = binders.find(_.username == username)

  //add a new bookmark to the bookmarks set
  def add(binder: Binder): Unit = {
    binders = binders + binder //persistent storage
  }
}
