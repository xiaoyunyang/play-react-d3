package models

/**
  * Created by xiaoyun on 1/2/16.
  */

import java.text.SimpleDateFormat
import java.util.Date

// Product2 is product stored in the warehouse
case class Product2(ean: Long, description: String)
case class Preparation(orderNumber: Long, product: Product2,
                       quantity: Int, location: String)
object PickList {
  def find(warehouse: String) : List[Preparation] = {
    val p = Product2(5010255079763L, "Large paper clips 1000 pack")
    List(
      Preparation(3141592, p, 200, "Aisle 42 bin 7"),
      Preparation(6535897, p, 500, "Aisle 42 bin 7"),
      Preparation(93, p, 100, "Aisle 42 bin 7")
    )
  }
}
object Warehouse {
  def find() = {
    List("W123", "W456")
  }
}
object Order {
  def backlog(warehouse: String): String = {
    Thread.sleep(5000L)
    new SimpleDateFormat("mmss").format(new Date())
  }
}