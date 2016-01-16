package models
import scala.util.hashing.MurmurHash3._

case class Tag(name: String, key: Int, username: String, url: String)

//Data access object
object Tag {
  private var tags = Set(
    Tag("learning", stringHash("https://www.khanacademy.org/"+"afenner"), "afenner", "https://www.khanacademy.org/"), //t1,p1,l1
    Tag("learning", stringHash("http://www.number-shapes.com/"+"afenner"), "afenner", "http://www.number-shapes.com/"),//t1,p1,l2
    Tag("math", stringHash("http://www.number-shapes.com/"+"afenner"), "afenner", "http://www.number-shapes.com/"), //t2,p1,l2
    Tag("online", stringHash("https://www.khanacademy.org/"+"xyang"), "xyang", "https://www.khanacademy.org/"), //t3,p2,l1
    Tag("education", stringHash("https://www.khanacademy.org/"+"xyang"), "xyang", "https://www.khanacademy.org/"), //t4,p2,l1
    Tag("learning", stringHash("http://theeqns.com/"+"xyang"), "xyang", "http://theeqns.com/") //t1,p2,l3
  )

  def findAll = tags.toList.sortBy(_.name)
  def findByName(name: String) = tags.filter(_.name == name).toList
  def findByUsername(username: String) = tags.filter(_.username == username).toList.sortBy(_.name)

  def findByNameAndUsername(username: String, name: String) =
    tags.filter(a => a.username==username && a.name==name).toList.sortBy(_.name)

  //add a new bookmark to the bookmarks set
  def add(tag: Tag): Unit = {
    tags = tags + tag //persistent storage
  }

}
