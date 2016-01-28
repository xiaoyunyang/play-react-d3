package models

case class Product(ean: Long, name: String, description: String, pieces: Int, active: Boolean)
//European Article Number (EAN) identifier is an international article number

//Data access object
object Product {
  private var products = Set(
    Product(5010255079763L, "Paperclips Large",
      "Large Plain Pack of 1000", 12, true),
    Product(5018206244666L, "Giant Paperclips",
      "Giant Plain 51mm 100 pack", 42, true),
    Product(5018306332812L, "Paperclip Giant Plain",
      "Giant Plain Pack of 10000", 3, true),
    Product(5018306312913L, "No Tear Paper Clip",
      "No Tear Extra Large Pack of 1000", 12, true),
    Product(5018206244611L, "Zebra Paperclips",
      "Zebra Length 28mm Assorted 150 Pack", 15, true)
  )
  def findAll = products.toList.sortBy(_.ean)
  def findByEan(ean: Long) = products.find(_.ean == ean)

  def add(product: Product): Unit = {
    products = products + product //persistent storage
  }
  def delete(ean: Long): Unit = {
    val newProducts = products.filter(a => a.ean != ean)
    products = newProducts
  }
  /*
   * takes a product instance as a parameter and replaces the product
   * that has the same unique EAN code.
   */

  def save(product: Product) = {
    findByEan(product.ean).map( oldProduct =>
      this.products = this.products - oldProduct + product
    ).getOrElse(
      throw new IllegalArgumentException("Product not found")
    )
  }
}
