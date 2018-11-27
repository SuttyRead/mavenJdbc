package com.ua.sutty.jdbc.repository;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ResourceBundle;

public abstract class AbstractJdbcDao {

    private BasicDataSource basicDataSource = null;
    private String dataSource = "h2";

    public AbstractJdbcDao(BasicDataSource basicDataSource, String dataSource) {
        this.basicDataSource = basicDataSource;
        this.dataSource = dataSource;
    }

    public AbstractJdbcDao() {
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public void setBasicDataSource(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Connection createConnection() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(dataSource);
        if (basicDataSource == null) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        Connection connection;
        try {
            Class.forName(resourceBundle.getString("jdbc.driver")).newInstance();
            connection = basicDataSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState());
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
