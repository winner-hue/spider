package com.pj.spider.dispatch;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;
import com.pj.spider.entity.Task;
import com.pj.spider.entity.UrlStructData;
import com.pj.spider.util.CommonUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlService {
    public static Connection connection;
    public static Statement statement;

    static {
        try {
            //数据源配置
            Properties properties = new Properties();
            //通过当前类的class对象获取资源文件
            InputStream is = SqlService.class.getResourceAsStream("/database.properties");
            properties.load(is);
            //返回的是DataSource，不是DruidDataSource
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            //获取连接
            connection = dataSource.getConnection();
            //Statement接口
            statement = connection.createStatement();
        } catch (Exception ignore) {
        }
    }


    public static List<Task> getTask() {
        try {
            String sql = "select * from tasks where taskStatus=0 and taskDoTime<now() and taskQueueName='" + DispatchConfig.dispatchTaskQueueName + "' limit " + DispatchConfig.dispatchTaskSize;
            ResultSet resultSet = statement.executeQuery(sql);
            return CommonUtil.getBeans(resultSet, Task.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateTask(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTask() {
        String sql = "update tasks set taskStatus=0 where  (NOW() - `taskDotime`) > `taskLimitTime`";
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        updateTask();
    }
}
