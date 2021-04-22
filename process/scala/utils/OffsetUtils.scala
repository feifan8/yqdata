package utils


import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.collection.mutable

object OffsetUtils {
  def getOffsetsMap(groupId: String, topic: String):mutable.Map[TopicPartition, Long] = {
    //获取连接，使用时填写数据库
    val conn:Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root")
    //编写sql
    val sql:String = "select partition,offset from t_offset where groupid = ? and topic = ?"
    //获取ps
    val ps:PreparedStatement = conn.prepareStatement(sql)
    //设置参数，执行
    ps.setString(1,groupId)
    ps.setString(2,topic)
    val rs:ResultSet = ps.executeQuery()
    //获取返回值，封装成map
    val offsetsMap:mutable.Map[TopicPartition,Long] = mutable.Map[TopicPartition,Long]()
    while(rs.next()){
      val partition:Int = rs.getInt("partition")
      val offset:Int = rs.getInt("offset")
      offsetsMap += new TopicPartition(topic,partition) -> offset
    }
    //关闭资源
    rs.close()
    ps.close()
    conn.close()

    //返回map
    offsetsMap
  }

  /**
   * 将消费者组的偏移量存入mysql
   *
   * @param groupId
   * @param offsets
   */
  def saveOffsets(groupId: String, offsets: Array[OffsetRange]): Unit ={
    //加载驱动，连接
    val conn:Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root")
    //编写sql
    val sql:String = "replace into t_offset (topic,partition,groupid,offset) value(?,?,?,?)"
    //创建预编译语句对象
    val ps:PreparedStatement = conn.prepareStatement(sql)
    //设置参数，运行
    for(o <-offsets){
      ps.setString(1,o.topic)
      ps.setInt(2,o.partition)
      ps.setString(3,groupId)
      ps.setLong(4,o.untilOffset)
      ps.executeUpdate()
    }
    //关闭资源
    ps.close()
    conn.close()
  }

}
