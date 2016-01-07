package models

case class Bookmark(url: String, name: String, description: String)
//European Article Number (EAN) identifier is an international article number

//Data access object
object Bookmark {
  var bookmarks = Set(
    Bookmark("https://www.gofundme.com/", "GoFundMe",
      "the #1 do-it-yourself fundraising website to raise money online."),
    Bookmark("https://www.indiegogo.com/", "Indiegogo",
      "helps individuals, groups and non-profits raise money online to make their ideas a reality"),
    Bookmark("https://www.kickstarter.com/", "Kickstarter",
      "world's largest funding platform for creative projects."),
    Bookmark("https://angel.co/", "AngelList",
      "Find a great startup job, invest in a startup or raise money.")
  )
  def findAll = bookmarks.toList.sortBy(_.url)
  def findByUrl(url: String) = bookmarks.find(_.url == url)
  def findByName(name: String) = bookmarks.find(_.name == name)
  //add a new bookmark to the bookmarks set
  def add(bookmark: Bookmark): Unit = {
    bookmarks = bookmarks + bookmark //persistent storage
  }
}
