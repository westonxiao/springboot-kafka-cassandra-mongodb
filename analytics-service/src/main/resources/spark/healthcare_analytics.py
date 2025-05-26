from pyspark.sql import SparkSession
from pyspark.sql.functions import *
import os

# Initialize Spark
spark = SparkSession.builder \
    .appName("HealthcareAnalytics") \
    .config("spark.cassandra.connection.host", os.getenv("CASSANDRA_HOST", "localhost")) \
    .getOrCreate()

# Read from Cassandra
df = spark.read \
    .format("org.apache.spark.sql.cassandra") \
    .options(table="user_activities", keyspace="analytics_ks") \
    .load()

# Daily aggregation
daily_stats = df.groupBy(
    to_date(col("timestamp")).alias("date"),
    col("action_type")
).agg(
    count("*").alias("event_count"),
    approx_count_distinct("user_id").alias("unique_users")
)

# Write back to Cassandra
daily_stats.write \
    .format("org.apache.spark.sql.cassandra") \
    .options(table="daily_activity_stats", keyspace="analytics_ks") \
    .mode("append") \
    .save()