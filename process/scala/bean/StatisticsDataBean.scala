package bean
//封装省份每一天的数据
case class StatisticsDataBean(
                             var dateId:String,
                             var provinceShortName:String,
                             var locationId:String,
                             var confirmedCount:Int,
                             var currentConfirmedCount:Int,
                             var currentConfirmedIncr:Int,
                             var confirmedIncr:Int,
                             var curedCount:Int,
                             var curedIncr:Int,
                             var suspectedCountIncr:Int,
                             var suspectedCount:Int,
                             var deadCount:Int,
                             var deadIncr:Int
                             )
