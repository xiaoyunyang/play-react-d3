package models

case class Bookmark(url: String, name: String, desc: String, username: String)
//European Article Number (EAN) identifier is an international article number

//Data access object
object Bookmark {
  private var bookmarks = Set(
    /*
    Bookmark("https://www.gofundme.com/", "GoFundMe",
      List(("afenner", "the #1 do-it-yourself fundraising website to raise money online.")),
    Bookmark("https://www.indiegogo.com/", "Indiegogo",
      "helps individuals, groups and non-profits raise money online to make their ideas a reality"),
    Bookmark("https://www.kickstarter.com/", "Kickstarter",
      "world's largest funding platform for creative projects."),
    Bookmark("https://angel.co/", "AngelList",
      "Find a great startup job, invest in a startup or raise money."),
*/
    Bookmark("https://www.khanacademy.org/", "Khan Academy","Free Online education.", "afenner"),
    Bookmark("https://www.khanacademy.org/", "Khan Academy","online learning platform.", "xyang"),

    Bookmark("http://www.number-shapes.com/", "NumberShapes",
      "Tablet early math education using symbols and hand gestures.", "afenner"),
    Bookmark("http://theeqns.com/", "EQNS", "equations for high school and college sciences.", "xyang")

  )
  def findAll = bookmarks.toList.sortBy(_.url)
  def findByUrl(url: String) = bookmarks.find(_.url == url)
  def findByName(name: String) = bookmarks.find(_.name == name)

  def findByUserName(username: String) = bookmarks.filter(_.username == username).toList

  //add a new bookmark to the bookmarks set
  def add(bookmark: Bookmark): Unit = {
    bookmarks = bookmarks + bookmark //persistent storage
  }
}
