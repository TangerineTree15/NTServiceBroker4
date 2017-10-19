package com.naturaltel.config.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DATABASE_PROPERTIES")
@XmlAccessorType(XmlAccessType.FIELD)
public class DBInfo {

    @XmlAttribute(name = "driver_class")
    private String driverClass;
    
    @XmlAttribute(name = "user_id")
    private String userId;
    
    @XmlAttribute(name = "user_password")
    private String userPassword;
    
    @XmlAttribute(name = "connection_url")
    private String connectionUrl;

    public String getDriverClass() {
        return driverClass;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DBInfo [driverClass=");
        builder.append(driverClass);
        builder.append(", userId=");
        builder.append(userId);
        builder.append(", userPassword=");
        builder.append(userPassword);
        builder.append(", connectionUrl=");
        builder.append(connectionUrl);
        builder.append("]");
        return builder.toString();
    }
    
    
}
