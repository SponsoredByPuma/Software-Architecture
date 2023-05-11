package util

object Util {
  def listRemoveAt[T](list: List[T], i: Int): List[T] = {
    val (front, back) = list.splitAt(i)
    front ++ back.tail
  }
}
