package io.prediction.storage

import com.github.nscala_time.time.Imports._
import org.json4s._

case class ItemSet(
  id: String,
  appid: Int,
  iids: Seq[String],
  t: Option[DateTime])

trait ItemSets {
  /** Insert new ItemSet */
  def insert(itemSet: ItemSet): Unit

  /** Get an item set */
  def get(appid: Int, id: String): Option[ItemSet]

  /** Get by appid */
  def getByAppid(appid: Int): Iterator[ItemSet]

  /** Get by appid and t >= startTime and t < untilTime */
  def getByAppidAndTime(appid: Int, startTime: DateTime, untilTime: DateTime):
    Iterator[ItemSet]

  /** Delete itemSet */
  def delete(itemSet: ItemSet): Unit

  /** Delete by appid */
  def deleteByAppid(appid: Int): Unit

}

class ItemSetSerializer extends CustomSerializer[ItemSet](format => (
  {
    case JObject(fields) =>
      val seed = ItemSet(
        id = "",
        appid = 0,
        iids = Seq(),
        t = None)
      fields.foldLeft(seed) { case (itemset, field) =>
        field match {
          case JField("id", JString(id)) => itemset.copy(id = id)
          case JField("appid", JInt(appid)) => itemset.copy(
            appid = appid.intValue)
          case JField("iids", JArray(s)) => itemset.copy(iids = s.map(t =>
            t match {
              case JString(iid) => iid
              case _ => ""
            }
          ))
          case JField("t", JString(t)) => itemset.copy(
            t = Some(Utils.stringToDateTime(t)))
        }
      }
  },
  {
    case itemset: ItemSet =>
      JObject(
        JField("id", JString(itemset.id)) ::
        JField("appid", JInt(itemset.appid)) ::
        JField("iids", JArray(itemset.iids.map(JString(_)).toList)) ::
        JField("t", itemset.t.map(x =>
          JString(x.toString)).getOrElse(JNothing)) :: Nil)
  }
))
