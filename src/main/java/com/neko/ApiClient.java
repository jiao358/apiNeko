package com.neko;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fuming.lj 2018/8/7
 **/
@Service
public class ApiClient {

     HttpConnectionManager manager = new HttpConnectionManager();
    static final String API_HOST = "api.huobi.pro";

    static final String API_URL = "https://" + API_HOST;

    String accessKeyId="a7fd725a-502746cd-69b903fd-4418a";
    String accessKeySecret="5774a589-a4b36db6-382fdc6f-6bbae";
    String assetPassword;




    public long createOrder(long orderId){
        String uri ="/v1/order/orders/" + orderId + "/place";

        ApiSignature sign = new ApiSignature();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, "POST", API_HOST, uri, new HashMap<>());

        long order=0L;
        try {
            CloseableHttpClient httpClient = manager.getHttpClient();
            HttpPost httpPost = new HttpPost(API_URL + uri);
            setHeader(httpPost);
            httpPost.setEntity( new StringEntity(JsonUtil.writeValue(null)));
            HttpResponse response = httpClient.execute(httpPost);

            String result ="";
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
            JSONObject ds = JSONObject.parseObject(result);
            order = ds.getLong("data");

        } catch (IOException e) {
            System.out.println("系统异常");
        }finally {
            return order;
        }

    }


    public String getOrderInfo(String orderId) throws Exception {
        ApiSignature sign = new ApiSignature();
        String uri="/v1/order/orders/" + orderId;
        Map<String,String> param = new HashMap<>();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, "GET", API_HOST, uri, param);
        CloseableHttpClient httpClient = manager.getHttpClient();
        String result="";
        HttpGet httpGet = new HttpGet(API_URL+uri+"?"+toQueryString(param));
        System.out.println("创建远程连接完毕:"+ httpGet.getURI());
        setHeader(httpGet);
        HttpResponse response = httpClient.execute(httpGet);
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, "utf-8");
            }
        }

        return result;

    }


    String toQueryString(Map<String, String> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + ApiSignature.urlEncode(entry.getValue());
        }).collect(Collectors.toList()));
    }

    String authData() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(this.assetPassword.getBytes(StandardCharsets.UTF_8));
        md.update("hello, moto".getBytes(StandardCharsets.UTF_8));
        Map<String, String> map = new HashMap<>();
        map.put("assetPwd", DatatypeConverter.printHexBinary(md.digest()).toLowerCase());
        try {
            return ApiSignature.urlEncode(JsonUtil.writeValue(map));
        } catch (IOException e) {
            throw new RuntimeException("Get json failed: " + e.getMessage());
        }
    }

    private void setHeader(HttpRequestBase base){
        if( base instanceof HttpGet){
            base.setHeader("Content-Type","application/x-www-form-urlencoded");
        }else{
            base.setHeader("Content-Type","application/json");
        }
        base.addHeader("user-agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 "
                + "Safari/537.36");
        base.addHeader("Accept-Language", "zh-cn");


    }



    public String get(String urlNameString) throws Exception {

        String result = null;
        try {

            CloseableHttpClient httpClient = manager.getHttpClient();

            HttpGet httpGet = new HttpGet(urlNameString);
            setHeader(httpGet);

            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
        } catch (Exception ex) {
            System.err.println("失败了");
        }
        return result;

    }

}
