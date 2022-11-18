import sys
import json
from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *
import time

if __name__ == "__main__":
    # 检查参数
    if len(sys.argv) != 4:
        print("参数错误", file=sys.stderr)
        sys.exit(-1)
    # 初始化Spark session
    spark = SparkSession\
        .builder\
        .appName("WeiboSpark")\
        .getOrCreate()
    sc = spark.sparkContext
    # 日志等级调整
    sc.setLogLevel("ERROR")
    # 设置kafka bootstrap server，默认为localhost:9092
    bootstrapServers = sys.argv[1]
    # kafka订阅类型
    subscribeType = sys.argv[2]
    # kafka主题
    topics = sys.argv[3]
    # 从kafka制定主题的流式数据，组织成dataframe
    df = spark\
        .readStream\
        .format("kafka")\
        .option("kafka.bootstrap.servers", bootstrapServers)\
        .option(subscribeType, topics)\
        .load()
    
    # 1 查看dataframe的schema
    df.printSchema()
    # 2 把dataframe的value列字段转为字符串
    df = df.selectExpr("CAST(value AS STRING) as value", "timestamp")
    # 3 对timestamp执行window操作，设置滑动窗口长度为30秒，滑动间隔为10秒
    # 统计字符串在某个窗口时间范围内的出现次数
    df = df.groupBy(window("timestamp", "30 seconds", "10 seconds"), "value")\
        .count()
    
   # 输出到控制台
    writeStream = df.writeStream.format("console")\
        .outputMode("complete")\
        .option("truncate", False)\
        .start()
    writeStream.awaitTermination()



