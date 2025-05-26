package com.healthcare.analytics.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Objects;

@Table("user_activities")
public class UserActivity {

    @PrimaryKey
    private UserActivityKey key;

    @Column("action_type")
    private String actionType;

    @Column("entity_id")
    private String entityId;

    @Column("metadata")
    private String metadata;

    // Constructors, getters, setters
    public UserActivity() {}

    public UserActivity(UserActivityKey key, String actionType, String entityId, String metadata) {
        this.key = key;
        this.actionType = actionType;
        this.entityId = entityId;
        this.metadata = metadata;
    }

    // equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActivity that = (UserActivity) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(actionType, that.actionType) &&
                Objects.equals(entityId, that.entityId) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, actionType, entityId, metadata);
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "key=" + key +
                ", actionType='" + actionType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", metadata='" + metadata + '\'' +
                '}';
    }

    // Getters and setters
    public UserActivityKey getKey() { return key; }
    public void setKey(UserActivityKey key) { this.key = key; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}