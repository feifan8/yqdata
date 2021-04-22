package bean

case class CovidBean(
                      provinceName: String,//省份
                      provinceShortName: String,//省份短名
                      cityName: String,
                      currentConfirmedCount: Int,//当前确诊人数
                      confirmedCount: Int,//累积确诊人数
                      suspectedCount: Int,//疑似病例人数
                      curedCount: Int,//治愈人数
                      deadCount: Int,//死亡人数
                      locationId: String,//位置id
                      pid: String,//城市id
                      statisticsData: String,//每天统计数据
                      cities: String,//下属城市
                      dateTime: String//日期
)
