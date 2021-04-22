package process

import bean.{CovidBean, StatisticsDataBean}
import com.alibaba.fastjson.JSON
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.{SparkContext, sql}
import org.apache.spark.sql.{DataFrame, Dataset, ForeachWriter, Row, SparkSession}
import utils.BaseJdbcSink

import scala.collection.immutable.StringOps
import scala.collection.mutable


object Covid19_Data_Process {
  def main(args: Array[String]): Unit ={
    //hadoop
    System.setProperty("hadoop.home.dir", "E:\\Javahadoop\\winutils-master\\hadoop-3.0.0")

    //1 StructuredStreaming环境
    val spark:sql.SparkSession = SparkSession.builder().master("local[*]").appName("Covid19_Data_Process").getOrCreate()
    val sc:SparkContext = spark.sparkContext
    sc.setLogLevel("WARN")
    //隐式转换
    import spark.implicits._
    import org.apache.spark.sql.functions._
    import scala.collection.JavaConversions._

    //2 连接kafka
    val kafkaDF:DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092,localhost:9093,localhost:9094")
      .option("subscribe","covid19")
      .load()
    //取value
    val jsonStrDS:Dataset[String] = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]

    //3 处理数据
    //将jsonStr转为样例类
    val covidBeanDS:Dataset[CovidBean] = jsonStrDS.map(jsonStr => {
      JSON.parseObject(jsonStr,classOf[CovidBean])
    })

    //分离出省份数据
    val provinceDS:Dataset[CovidBean] = covidBeanDS.filter(_.statisticsData != null)

    //分离出城市数据
    val cityDS:Dataset[CovidBean] = covidBeanDS.filter(_.statisticsData == null)

    //分离出各省份每一天的统计数据
    val statisticsDataDS:Dataset[StatisticsDataBean] = provinceDS.flatMap(p => {
      val jsonStr: StringOps = p.statisticsData
      val list: mutable.Buffer[StatisticsDataBean] = JSON.parseArray(jsonStr, classOf[StatisticsDataBean])
      list.map(s => {
        s.provinceShortName = p.provinceShortName
        s.locationId = p.locationId
        s
      })
    })


    //4 统计分析
    //4.1全国疫情汇总：现有确诊，累计确诊，现有疑似，累计治愈，累计死亡，按照日期分组统计 *
    val result1:DataFrame = provinceDS.groupBy('datetime)
      .agg(sum(('currentConfirmedCount)) as "currentConfirmedCount",
        sum(('confirmedCount)) as "confirmedCount",
        sum(('suspectedCount)) as "suspectedCount",
        sum(('curedCount)) as "curedCount",
        sum(('deadCount)) as "deadCount"
      )

    //4.2全国各省份_累计确诊数_地图，按照日期-省份分组 *
    val result2:DataFrame = provinceDS.select('datetime,'locationId,'provinceShortName,'currentConfirmedCount,'confirmedCount,'suspectedCount,'curedCount,'deadCount)

    //4.3全国疫情趋势图，按照日期分组 *
    val result3:DataFrame = statisticsDataDS.groupBy('dateId)
      .agg(
        sum('confirmedIncr) as "confirmedIncr",//新增确诊
        sum('confirmedCount) as "confirmedCount",//累计确诊
        sum('suspectedCount) as "suspectedCount",//累计疑似
        sum('curedCount) as "curedCount",//累计治愈
        sum('deadCount) as "deadCount"//累计死亡
      )

    //4.4境外输入排行，按照城市分组
    val result4: Dataset[Row] = cityDS.filter(_.cityName.contains("境外输入"))
      .groupBy('datetime,'provinceShortName,'pid)
      .agg(sum('confirmedCount) as "confirmedCount")
      .sort('confirmedCount.desc)

    //4.5统计地区累计确诊地图 cityName null
    val result5:DataFrame = cityDS.filter(_.provinceShortName.equals("广东")).select('datetime,'locationId,'provinceShortName,'cityName,'currentConfirmedCount,'confirmedCount,'suspectedCount,'curedCount,'deadCount)

    //5 输出结果

    result1.writeStream
      .format("console")//输出目的地
      .outputMode("complete")//输出模式
      .trigger(Trigger.ProcessingTime(0))//触发间隔
      .option("truncate",false)//列名长但不截断
      .start()
    result1.writeStream
      .foreach(new BaseJdbcSink("replace into covid19_1 (datetime,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount) value(?,?,?,?,?,?)") {
        override def realProcess(sql: String, row: Row): Unit = {
          val datetime:String = row.getAs[String]("datetime")
          val currentConfirmedCount:Long = row.getAs[Long]("currentConfirmedCount")
          val confirmedCount:Long = row.getAs[Long]("confirmedCount")
          val suspectedCount:Long = row.getAs[Long]("suspectedCount")
          val curedCount:Long = row.getAs[Long]("curedCount")
          val deadCount:Long = row.getAs[Long]("deadCount")
          ps = conn.prepareStatement(sql)
          ps.setString(1,datetime)
          ps.setLong(2,currentConfirmedCount)
          ps.setLong(3,confirmedCount)
          ps.setLong(4,suspectedCount)
          ps.setLong(5,curedCount)
          ps.setLong(6,deadCount)
          ps.executeUpdate()
        }
      })
      .outputMode("complete")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",false)
      .start()

    result2.writeStream
      .format("console")//输出目的地
      .outputMode("append")//输出模式
      .trigger(Trigger.ProcessingTime(0))//触发间隔
      .option("truncate",false)//列名长但不截断
      .start()
    result2.writeStream
      .foreach(new BaseJdbcSink("replace into covid19_2 (datetime,locationId,provinceShortName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount) value(?,?,?,?,?,?,?,?)") {
        override def realProcess(sql: String, row: Row): Unit = {
          val datetime:String = row.getAs[String]("datetime")
          val locationId:String = row.getAs[String]("locationId")
          val provinceShortName:String = row.getAs[String]("provinceShortName")
          val currentConfirmedCount:Int = row.getAs[Int]("currentConfirmedCount")
          val confirmedCount:Int = row.getAs[Int]("confirmedCount")
          val suspectedCount:Int = row.getAs[Int]("suspectedCount")
          val curedCount:Int = row.getAs[Int]("curedCount")
          val deadCount:Int = row.getAs[Int]("deadCount")
          ps = conn.prepareStatement(sql)
          ps.setString(1,datetime)
          ps.setString(2,locationId)
          ps.setString(3,provinceShortName)
          ps.setInt(4,currentConfirmedCount)
          ps.setInt(5,confirmedCount)
          ps.setInt(6,suspectedCount)
          ps.setInt(7,curedCount)
          ps.setInt(8,deadCount)
          ps.executeUpdate()
        }
      })
      .outputMode("append")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",false)
      .start()

    result3.writeStream
      .format("console")//输出目的地
      .outputMode("complete")//输出模式
      .trigger(Trigger.ProcessingTime(0))//触发间隔
      .option("truncate",false)//列名长但不截断
      .start()
    result3.writeStream
      .foreach(new BaseJdbcSink("replace into covid19_3 (dateId,confirmedIncr,confirmedCount,suspectedCount,curedCount,deadCount) value(?,?,?,?,?,?)") {
        override def realProcess(sql: String, row: Row): Unit = {
          val dateId:String = row.getAs[String]("dateId")
          val confirmedIncr:Long = row.getAs[Long]("confirmedIncr")
          val confirmedCount:Long = row.getAs[Long]("confirmedCount")
          val suspectedCount:Long = row.getAs[Long]("suspectedCount")
          val curedCount:Long = row.getAs[Long]("curedCount")
          val deadCount:Long = row.getAs[Long]("deadCount")
          ps = conn.prepareStatement(sql)
          ps.setString(1,dateId)
          ps.setLong(2,confirmedIncr)
          ps.setLong(3,confirmedCount)
          ps.setLong(4,suspectedCount)
          ps.setLong(5,curedCount)
          ps.setLong(6,deadCount)
          ps.executeUpdate()
        }
      })
      .outputMode("complete")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",false)
      .start()

    result4.writeStream
      .format("console")//输出目的地
      .outputMode("complete")//输出模式
      .trigger(Trigger.ProcessingTime(0))//触发间隔
      .option("truncate",false)//列名长但不截断
      .start()
    result4.writeStream
      .foreach(new BaseJdbcSink("replace into covid19_4 (datetime,provinceShortName,pid,confirmedCount) value(?,?,?,?)") {
        override def realProcess(sql: String, row: Row): Unit = {
          val datetime:String = row.getAs[String]("datetime")
          val provinceShortName:String = row.getAs[String]("provinceShortName")
          val pid:String = row.getAs[String]("pid")
          val confirmedCount:Long = row.getAs[Long]("confirmedCount")
          ps = conn.prepareStatement(sql)
          ps.setString(1,datetime)
          ps.setString(2,provinceShortName)
          ps.setString(3,pid)
          ps.setLong(4,confirmedCount)
          ps.executeUpdate()
        }
      })
      .outputMode("complete")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",false)
      .start()


    result5.writeStream
      .format("console")//输出目的地
      .outputMode("append")//输出模式
      .trigger(Trigger.ProcessingTime(0))//触发间隔
      .option("truncate",false)//列名长但不截断
      .start()
    result5.writeStream
      .foreach(new BaseJdbcSink("replace into covid19_5 (datetime,locationId,provinceShortName,cityName,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount) value(?,?,?,?,?,?,?,?,?)") {
        override def realProcess(sql: String, row: Row): Unit = {
          val datetime:String = row.getAs[String]("datetime")
          val locationId:String = row.getAs[String]("locationId")
          val provinceShortName:String = row.getAs[String]("provinceShortName")
          val cityName:String = row.getAs[String]("cityName")
          val currentConfirmedCount:Int = row.getAs[Int]("currentConfirmedCount")
          val confirmedCount:Int = row.getAs[Int]("confirmedCount")
          val suspectedCount:Int = row.getAs[Int]("suspectedCount")
          val curedCount:Int = row.getAs[Int]("curedCount")
          val deadCount:Int = row.getAs[Int]("deadCount")
          ps = conn.prepareStatement(sql)
          ps.setString(1,datetime)
          ps.setString(2,locationId)
          ps.setString(3,provinceShortName)
          ps.setString(4,cityName)
          ps.setInt(5,currentConfirmedCount)
          ps.setInt(6,confirmedCount)
          ps.setInt(7,suspectedCount)
          ps.setInt(8,curedCount)
          ps.setInt(9,deadCount)
          ps.executeUpdate()
        }
      })
      .outputMode("append")
      .trigger(Trigger.ProcessingTime(0))
      .option("truncate",false)
      .start()
      .awaitTermination()

  }
}
