package models
import play.api.libs._
case class Bookmark(key: String, url: String, name: String, desc: String, username: String)
//SHA1 of url+username is the key, i.e., unique identifier

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
    Bookmark("e8b1c2e18703ae5d71620cd5990d450224ce2e3e", "https://www.khanacademy.org/", "Khan Academy","Free Online education.", "afenner"),
    Bookmark("eafdcb58ca4b93fa257df6ca49ed80a04fe9639e", "https://www.khanacademy.org/", "Khan Academy","online learning platform.", "xyang"),

    Bookmark("5b3373b5c05872decd62434703a91e3765a66f25", "http://www.number-shapes.com/", "NumberShapes",
      "Tablet early math education using symbols and hand gestures.", "afenner"),
    Bookmark("a00106373eaaac94abe9829d55652f9541af8f87", "http://theeqns.com/", "EQNS", "equations for high school and college sciences.", "xyang")

  )
  def findAll = bookmarks.toList.sortBy(_.url)
  def findByUrl(url: String) = bookmarks.find(_.url == url)
  def findByName(name: String) = bookmarks.find(_.name == name)

  def findByKey(key: String) = bookmarks.find(_.key == key)

  def findByUserName(username: String) = bookmarks.filter(_.username == username).toList

  //add a new bookmark to the bookmarks set
  def add(bookmark: Bookmark): Unit = {
    bookmarks = bookmarks + bookmark //persistent storage
  }
}
