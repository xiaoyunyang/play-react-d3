package models

case class User(username: String, name: String)
//European Article Number (EAN) identifier is an international article number

//Data access object
object User {
  private var users = Set(
    User("afenner", "Andrew Fenner"),
    User("xyang", "Xiaoyun Yang"),
    User("kbusch", "Kyle Buschbacher")
  )
  def findAll = users.toList.sortBy(_.username)
  def findByUsername(username: String) = users.find(_.username == username)

}
