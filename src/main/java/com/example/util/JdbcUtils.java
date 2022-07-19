package com.example.util;


import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcUtils {
    /**
     * 获取字段相关类:
     * 使用完记得关闭连接
     */
    public static ResultSetMetaData getResultSetMetaData(String tableName) {
        PreparedStatement preparedStatement = null;
        ResultSetMetaData resultSetMetaData = null;
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("select * from " + tableName + " where 1=2");
            resultSetMetaData = preparedStatement.executeQuery().getMetaData();
        } catch (Exception e) {
            log.error("数据库连接异常", e);
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return resultSetMetaData;
    }


    /**
     * 获取当前数据库下的所有表名称
     */
    public static List<String> getAllTableName() {
        List<String> tables = new ArrayList();
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SHOW TABLES ");
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                tables.add(tableName);
            }
        } catch (SQLException e) {
            log.error("数据库连接异常", e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }


        return tables;
    }


    /**
     * 获得某表的建表语句
     */
    public static String getCreateTableDDL(String tableName) {
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String createDDLSql = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SHOW CREATE TABLE " + tableName);
            if (resultSet != null && resultSet.next()) {
                createDDLSql = resultSet.getString(2);
            }
        } catch (SQLException e) {
            log.error("数据库连接异常", e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }
        return createDDLSql;
    }

    /**
     * 获得某表的注释
     */
    public static String getTableCommnet(String tableName) {
        String creatDDLSql = getCreateTableDDL(tableName);
        String comment = null;
        int index = creatDDLSql.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = creatDDLSql.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }


    /**
     * 获取表中字段的所有注释
     */
    public static List<String> getColumnComments(String tableName) {
        //与数据库的连接
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        String tableSql = "select * from " + tableName;
        //列名注释集合
        List<String> columnComments = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(tableSql);
            resultSet = preparedStatement.executeQuery("show full columns from " + tableName);
            while (resultSet.next()) {
                columnComments.add(resultSet.getString("Comment"));
            }
        } catch (SQLException e) {
            log.error("数据库连接异常", e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
        return columnComments;
    }


    /**
     * 获取数据连接
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://101.35.44.70:3306/example?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true", "root", "root");
        } catch (Exception e) {
            log.error("get connection failure", e);
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("close connection failure", e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("close preparedStatement failure ", e);
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("close resultSet failure ", e);
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("close statement failure ", e);
            }
        }
    }


}

