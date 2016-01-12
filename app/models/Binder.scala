package models

case class Binder(name: String, alias: String, description: String, username: String, tags: List[String])
//European Article Number (EAN) identifier is an international article number

//Data access object
object Binder {
  private var binders = Set(
    Binder("elementary-math", "Elementary Math", "Elementary math teaching notes", "afenner",List("learning","math","kids")),
    Binder("swift-dev", "Swift Dev", "Useful stackoverflow posts for mobile app dev in Swift","afenner",List("tutorial","programming","swift")),
    Binder("story-time", "Story Time", "My favorite post from Jesse's Story Time","afenner",List("funny")),
    Binder("scala-notes", "Scala Notes", "my evernote collection of scala learning notes", "xyang",List("tutorial","scala","learning")),
    Binder("play-notes", "Learn Play Framework", "Best tutorial articles/Github projects for getting started with Play","xyang",List("tutorial","webapp","scala")),
    Binder("python-learning", "Python-Learning Notes", "beginner prgrogramming in Python","kbusch",List("learning","python","programming"))
  )

  def findAll(username: String) = binders.filter(_.username == username).toList.sortBy(_.name)

  def findByName(name: String) = binders.find(_.name == name)

  //add a new bookmark to the bookmarks set
  def add(binder: Binder): Unit = {
    binders = binders + binder //persistent storage
  }

  def delete(username: String, bindername: String): Unit = {
    val newBinders = binders.filter(a => a.name != bindername || a.username != username)
    binders = newBinders
  }
}
