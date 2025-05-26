-- Create keyspace if not exists
CREATE KEYSPACE IF NOT EXISTS analytics_ks
WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'replication_factor': 3
};

-- User activities table
CREATE TABLE IF NOT EXISTS analytics_ks.user_activities (
    user_id UUID,
    timestamp TIMESTAMP,
    action_type TEXT,
    entity_id TEXT,
    metadata TEXT,
    PRIMARY KEY ((user_id), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Analytics views table
CREATE TABLE IF NOT EXISTS analytics_ks.analytics_views (
    date DATE,
    view_type TEXT,
    total_events COUNTER,
    total_value DECIMAL,
    PRIMARY KEY ((date), view_type)
);

-- Materialized view for time-based queries
CREATE MATERIALIZED VIEW IF NOT EXISTS analytics_ks.activities_by_time AS
    SELECT * FROM user_activities
    WHERE timestamp IS NOT NULL AND user_id IS NOT NULL
    PRIMARY KEY ((timestamp), user_id)
    WITH CLUSTERING ORDER BY (user_id ASC);