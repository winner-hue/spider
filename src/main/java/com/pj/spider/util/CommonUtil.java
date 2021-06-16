package com.pj.spider.util;

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
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
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
                        CommonConfig.mapper.putIfAbsent(key, hashMap);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
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
}
