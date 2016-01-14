package models

case class Tag(name: String, key: String, username: String, url: String)

//Data access object
object Tag {
  private var tags = Set(
    Tag("learning", "e8b1c2e18703ae5d71620cd5990d450224ce2e3e", "afenner", "https://www.khanacademy.org/"), //t1,p1,l1
    Tag("learning", "5b3373b5c05872decd62434703a91e3765a66f25", "afenner", "http://www.number-shapes.com/"),//t1,p1,l2
    Tag("math", "5b3373b5c05872decd62434703a91e3765a66f25", "afenner", "http://www.number-shapes.com/"), //t2,p1,l2
    Tag("online", "eafdcb58ca4b93fa257df6ca49ed80a04fe9639e", "xyang", "https://www.khanacademy.org/"), //t3,p2,l1
    Tag("education", "eafdcb58ca4b93fa257df6ca49ed80a04fe9639e", "xyang", "https://www.khanacademy.org/"), //t4,p2,l1
    Tag("learning", "a00106373eaaac94abe9829d55652f9541af8f87", "xyang", "http://theeqns.com/") //t1,p2,l3
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
