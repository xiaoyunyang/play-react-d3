package models
import play.api.libs._
import scala.util.hashing.MurmurHash3._
case class Bookmark(key: Int, url: String, username: String, title: String,  description: Option[String], favicon: String)
//MurmurHash.stringHash of url+username is the key, i.e., unique identifier

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
    Bookmark(stringHash("https://www.khanacademy.org/"+"afenner"), "https://www.khanacademy.org/", "afenner",
      "Khan Academy",Some("Free Online education."), "https://stagr.in/img/placeholder.png"),
    Bookmark(stringHash("https://www.khanacademy.org/"+"xyang"), "https://www.khanacademy.org/", "xyang",
      "Khan Academy",Some("online learning platform."), "https://stagr.in/img/placeholder.png"),
    Bookmark(stringHash("http://www.number-shapes.com/"+"afenner"), "http://www.number-shapes.com/", "afenner",
      "NumberShapes", Some("Tablet early math education using symbols and hand gestures."), "https://stagr.in/img/placeholder.png"),
    Bookmark(stringHash("http://theeqns.com/"+"xyang"), "http://theeqns.com/", "xyang",
      "EQNS", Some("equations for high school and college sciences."), "https://stagr.in/img/placeholder.png")

  )
  def findAll = bookmarks.toList.sortBy(_.url)
  def findByUrl(url: String) = bookmarks.find(_.url == url)
  def findByTitle(title: String) = bookmarks.find(_.title == title)

  def findByKey(key: Int) = bookmarks.find(_.key == key)

  def findByUserName(username: String) = bookmarks.filter(_.username == username).toList

  //add a new bookmark to the bookmarks set
  def add(bookmark: Bookmark): Unit = {
    bookmarks = bookmarks + bookmark //persistent storage
  }
  def save(bookmark: Bookmark) = {
    findByKey(bookmark.key).map( oldBookmark =>
      this.bookmarks = this.bookmarks - oldBookmark + bookmark
    ).getOrElse(
      throw new IllegalArgumentException("Bookmark not found")
    )
  }
}
