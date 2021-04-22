package utils

import org.apache.spark.sql.{ForeachWriter, Row}

import java.sql.{Connection, DriverManager, PreparedStatement}

//spark消息传入mysql
abstract class BaseJdbcSink(sql:String) extends ForeachWriter[Row]{
  def realProcess(sql:String,row:Row)

  var conn:Connection = _
  var ps:PreparedStatement = _
  //开启连接
  override def open(partitionId: Long, version: Long): Boolean = {
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid19?characterEncoding=UTF-8","root","root")
    true
  }
  //处理数据
  override def process(value: Row): Unit = {
    realProcess(sql,value)
  }
  //关闭连接
  override def close(errorOrNull: Throwable): Unit = {
    if(conn != null){
      conn.close()
    }
    if(ps != null){
      ps.close()
    }

  }

}
