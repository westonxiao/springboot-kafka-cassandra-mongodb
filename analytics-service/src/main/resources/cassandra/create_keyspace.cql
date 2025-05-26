-- Create keyspace for healthcare analytics
CREATE KEYSPACE IF NOT EXISTS healthcare_analytics
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Switch to the created keyspace
USE healthcare_analytics;

-- Create user activity table
CREATE TABLE IF NOT EXISTS user_activity (
    user_id text,
    activity_time timestamp,
    activity_type text,
    details text,
    PRIMARY KEY ((user_id), activity_time)
) WITH CLUSTERING ORDER BY (activity_time DESC);

-- Create analytics view table (materialized view)
CREATE TABLE IF NOT EXISTS analytics_view (
    view_id text,
    view_date date,
    view_type text,
    count counter,
    PRIMARY KEY ((view_id), view_date)
);