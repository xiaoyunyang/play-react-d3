package controllers

/**
  * Created by xiaoyun on 1/11/16.
  */
object TagHelper {

  /* mapList:
   * create multimap (K,List[V]) from a list
   */
  def mapList[K,V](l: List[V])(key: V => K): Map[K, List[V]] = {
    var map = Map.empty[K, List[V]]
    def addBinding(key: K, value: V) =
      map += (key -> (value :: (map get key getOrElse Nil)))
    l.foreach(v => addBinding(key(v), v))
    map
  }
}
