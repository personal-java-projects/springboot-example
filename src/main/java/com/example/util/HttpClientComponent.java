package com.example.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求第三方接口的工具类
 */
public class HttpClientComponent {

    private static Logger logger = LoggerFactory.getLogger(HttpClientComponent.class);
    public static final String UTF8_ENCODING = "UTF-8";
    private static final String HEADER_COOKIE = "Cookie";
    private static final int TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    /**
     * 获取url上的参数
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> getParameter(String url) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        final String charset = "utf-8";

        url = URLDecoder.decode(url, charset);

        if (url.indexOf('?') != -1) {
            final String contents = url.substring(url.indexOf('?') + 1);
            String[] keyValues = contents.split("&");
            for (int i = 0; i < keyValues.length; i++) {
                String key = keyValues[i].substring(0, keyValues[i].indexOf("="));
                String value = keyValues[i].substring(keyValues[i].indexOf("=") + 1);
                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * 将URLDecoder编码转成UTF8
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getUtf8ByURLDecoder(String str) throws UnsupportedEncodingException {
        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");

        return URLDecoder.decode(url, "UTF-8");
    }

    /**
     * httpClient的get请求方式
     * 使用GetMethod来访问一个URL对应的网页实现步骤：
     * 1.生成一个HttpClient对象并设置相应的参数；
     * 2.生成一个GetMethod对象并设置响应的参数；
     * 3.用HttpClient生成的对象来执行GetMethod生成的Get方法；
     * 4.处理响应状态码；
     * 5.若响应正常，处理HTTP响应内容；
     * 6.释放连接。
     * @param url
     * @param charset
     * @return
     */
    public static StringBuffer doGet(String url, String charset, Map<String, Object> headers) throws Exception {
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        /**
         * 2.生成GetMethod对象并设置参数
         */
        GetMethod getMethod = new GetMethod(url);

        for (Map.Entry<String, Object> entry : headers.entrySet()) {

//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            getMethod.setRequestHeader(entry.getKey(), (String) entry.getValue());
        }

        //设置get请求超时为5秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        //设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

        /**
         * 3.执行HTTP GET 请求
         */
        int statusCode = httpClient.executeMethod(getMethod);

        if (statusCode != HttpStatus.SC_OK) {
            throw new Exception("网络请求出错： " + getMethod.getStatusLine());
        }

        /**
         * 5.处理HTTP响应内容
         */
        //HTTP响应头部信息，这里简单打印
//      Header[] headers = getMethod.getResponseHeaders();
//      for (Header h: headers){
//          System.out.println(h.getName() + "---------------" + h.getValue());
//      }

        //读取HTTP响应内容，这里简单打印网页内容
        //读取为字节数组
//        byte[] responseBody = getMethod.getResponseBody();
//        String response = "";
//        response = new String(responseBody, charset);
//      System.out.println("-----------response:" + response);

        //读取为InputStream，在网页内容数据量大时候推荐使用
        InputStream response = getMethod.getResponseBodyAsStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(response));
        StringBuffer stringBuffer = new StringBuffer();
        String str = "";

        while ((str = br.readLine()) != null) {
            stringBuffer.append(str);
        }

        System.out.println("stringBuffer: " + stringBuffer);

        /**
         * 6.释放连接
         */
        getMethod.releaseConnection();

        return stringBuffer;
    }

    /**
     * post请求
     * @param url
     * @param json
     * @return
     */
    public static String doPost(String url, JSONObject json){
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        postMethod.addRequestHeader("accept", "*/*");
        postMethod.addRequestHeader("connection", "Keep-Alive");
        //设置json格式传送
        postMethod.addRequestHeader("Content-Type", "application/json;charset=utf-8");
        //必须设置下面这个Header
        postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        //添加请求参数
        postMethod.addParameter("commentId", json.getString("commentId"));

        String res = "";
        try {
            int code = httpClient.executeMethod(postMethod);
            if (code == 200){
                res = postMethod.getResponseBodyAsString();
                System.out.println(res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
