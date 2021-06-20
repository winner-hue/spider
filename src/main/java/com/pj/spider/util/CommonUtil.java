package com.pj.spider.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.pj.spider.config.CommonConfig;
import com.pj.spider.entity.Task;
import com.pj.spider.plugin.DownloadBase;
import com.pj.spider.plugin.DupBase;
import com.pj.spider.plugin.ExtractBase;
import com.pj.spider.plugin.StorageBase;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtil {
    static {
        try {
            CommonConfig.addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取md值
     *
     * @param m
     * @return
     */
    public static String getMd5(String m) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    m.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 加载SiteMapper中的配置文件
     */
    public static void loadSiteMapper() {
        Yaml yaml = new Yaml();
        try {
            InputStream in = CommonUtil.class.getClassLoader().getResourceAsStream("sitemapper/sitemapper.yml");
            CommonConfig.properties = yaml.loadAs(in, HashMap.class);
            for (int temp : CommonConfig.properties.keySet()) {
                String value = CommonConfig.properties.get(temp);
                String path = CommonUtil.class.getResource("/").getPath() + "sitemapper/" + value;
                File file = new File(path);
                for (String name : Objects.requireNonNull(file.list())) {
                    try {
                        InputStream resourceAsStream = CommonUtil.class.getClassLoader().getResourceAsStream("sitemapper/" + value + File.separator + name);
                        HashMap hashMap = yaml.loadAs(resourceAsStream, HashMap.class);
                        String domain = "base";
                        if (hashMap.get("domain") == null) {
                            if (!name.contains("base")) {
                                log.info(name + "文件配置错误");
                            }
                        } else {
                            domain = (String) hashMap.get("domain");
                        }
                        String key = value + "_" + domain;
                        CommonConfig.mapper.put(key, hashMap);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("加载配置文件错误， 退出系统。。。");
            System.exit(1);
        }
    }

    /**
     * 获取顶级域名，入参为二级域名
     *
     * @param secondDomain
     * @return
     */
    public static String getMainDomain(String secondDomain) {
        Pattern pattern = Pattern.compile("[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
        Matcher matcher = pattern.matcher(secondDomain);
        while (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * 获取域名
     *
     * @param url
     * @return
     */
    public static String[] getDomain(String url) {
        try {
            URL tempUrl = new URL(url.trim());
            String secondDomain = tempUrl.getHost();
            String mainDomain = getMainDomain(secondDomain);
            return new String[]{secondDomain, mainDomain};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object[] getPlugin(Task task, String name) {
        Integer taskType = task.getTaskType();
        String taskUrl = task.getTaskUrl();
        // 当配置文件的数量为0时，需要等待配置文件的加载
        while (CommonConfig.mapper.size() == 0) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String[] domains = CommonUtil.getDomain(taskUrl);
            String secondDomain = domains[0];
            String mainDomain = domains[1];
            for (int key : CommonConfig.properties.keySet()) {
                if (taskType == key) {
                    String value = CommonConfig.properties.get(key);
                    HashMap hashMap = CommonConfig.mapper.get(value + "_" + secondDomain) != null ? CommonConfig.mapper.get(value + "_" + secondDomain) : CommonConfig.mapper.get(value + "_" + mainDomain);
                    if (hashMap != null) {
                        String class_path = (String) ((HashMap) hashMap.get(name)).get("class");
                        return new Object[]{Class.forName(class_path).getDeclaredConstructor(Task.class).newInstance(task), (HashMap) hashMap.get(name)};
                    }
                }
            }

        } catch (Exception ignored) {
        }
        String value = CommonConfig.properties.get(taskType);
        HashMap hashMap = CommonConfig.mapper.get(value + "_" + "base");
        switch (name) {
            case "download":
                return new Object[]{new DownloadBase(task), (HashMap) hashMap.get(name)};
            case "dup":
                return new Object[]{new DupBase(task), (HashMap) hashMap.get(name)};
            case "extract":
                return new Object[]{new ExtractBase(task), (HashMap) hashMap.get(name)};
            case "storage":
                return new Object[]{new StorageBase(task), (HashMap) hashMap.get(name)};
            default:
                return null;
        }
    }

    public static boolean isMatch(String pattern, String content) {
        Matcher m = Pattern.compile(pattern).matcher(content);
        return m.find();
    }

    public static String[] match(String pattern, String content) {
        Matcher m = Pattern.compile(pattern).matcher(content);

        while (m.find()) {
            int n = m.groupCount();
            String[] ss = new String[n + 1];
            for (int i = 0; i <= n; i++) {
                ss[i] = m.group(i);
            }
            return ss;
        }
        return null;
    }


    public static <T> String getInsertSql(String tablename, Class<T> clazz, T t) {
        //insert into table_name (column_name1,column_name2, ...) values (value1,value2, ...)
        String sql = "";
        Field[] fields = ReflectUtil.getFieldsDirectly(clazz, false);
        StringBuffer topHalf = new StringBuffer("insert into " + tablename + " (");
        StringBuffer afterAalf = new StringBuffer("values (");
        for (Field field : fields) {
            if ("ID".equals(field.getName()) || "id".equals(field.getName())) {
                continue;   //id 自动生成无需手动插入
            }
            topHalf.append(field.getName()).append(",");
            if (ReflectUtil.getFieldValue(t, field.getName()) instanceof String) {
                afterAalf.append("'").append(ReflectUtil.getFieldValue(t, field.getName())).append("',");
            } else {
                afterAalf.append(ReflectUtil.getFieldValue(t, field.getName())).append(",");
            }
        }
        topHalf = new StringBuffer(StrUtil.removeSuffix(topHalf.toString(), ","));
        afterAalf = new StringBuffer(StrUtil.removeSuffix(afterAalf.toString(), ","));
        topHalf.append(") ");
        afterAalf.append(") ");
        sql = topHalf.toString() + afterAalf.toString();
        return sql;
    }

    /**
     * 生成更新语句
     * 必须含有id
     * 数据实体中 null 与 空字段不参与更新
     *
     * @param tablename 数据库中的表明
     * @param clazz     与数据库中字段一一对应的类
     * @param t         有数据的实体
     * @param <T>       数据实体类型,如 User
     */
    public static <T> String getUpdateSql(String tablename, Class<T> clazz, T t) {
        StringBuilder sql = new StringBuilder();
        String id = ""; //保存id名：ID or id
        Field[] fields = ReflectUtil.getFieldsDirectly(clazz, false);
        sql = new StringBuilder("update " + tablename + " set ");
        for (Field field : fields) {
            StringBuilder tmp = new StringBuilder();
            if ("ID".equals(field.getName()) || "id".equals(field.getName())) {
                id = field.getName();
                continue;//更新的时候无需set id=xxx
            }
            if (ReflectUtil.getFieldValue(t, field.getName()) != null && ReflectUtil.getFieldValue(t, field.getName()) != "") {
                tmp.append(field.getName()).append("=");
                if (ReflectUtil.getFieldValue(t, field.getName()) instanceof String) {
                    tmp.append("'").append(ReflectUtil.getFieldValue(t, field.getName())).append("',");
                } else if (ReflectUtil.getFieldValue(t, field.getName()) instanceof LocalDateTime) {
                    String replace = ReflectUtil.getFieldValue(t, field.getName()).toString().replace("T", " ");
                    tmp.append("'").append(replace).append("',");
                } else {
                    tmp.append(ReflectUtil.getFieldValue(t, field.getName())).append(",");
                }
                sql.append(tmp);
            }
        }
        sql = new StringBuilder(StrUtil.removeSuffix(sql.toString(), ",") + " where " + id + "='" + ReflectUtil.getFieldValue(t, id) + "'");
        return sql.toString();

    }

    public static <T> List<T> getBeans(ResultSet resultSet, Class<T> className) {
        List<T> list = new ArrayList<T>();
        Field[] fields = className.getDeclaredFields();
        try {
            while (resultSet.next()) {
                T instance = className.newInstance();
                for (Field field : fields) {
                    Object result = null;
                    try {
                        result = resultSet.getObject(field.getName());
                    } catch (SQLException e) {
                        continue;
                    }
                    if (result instanceof Long) {
                        result = Integer.parseInt(String.valueOf(result));
                    }
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    field.set(instance, result);
                    field.setAccessible(flag);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}