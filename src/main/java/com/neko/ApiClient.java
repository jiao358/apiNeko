package com.neko;

import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fuming.lj 2018/8/7
 **/
public class ApiClient {

    static HttpConnectionManager manager;

    static final String API_HOST = "api.huobi.pro";

    static final String API_URL = "https://" + API_HOST;

    String accessKeyId="205911d9-34c137a1-33ed938c-d9c12";
    String accessKeySecret="c35095b1-d25a11d6-a949a451-205a4";
    String assetPassword;

    static {
        manager=   new HttpConnectionManager();
        manager.init();

    }

    /**
     * 获取accountId
     * @return
     */
    public String getAccountId(){
        String uri = "/v1/account/accounts";
        String ab= get(uri,new HashMap<>());
        return ab;
    }

    public void execute(long orderId){
        String uri ="/v1/order/orders/" + orderId + "/place";

        ApiSignature sign = new ApiSignature();
        Map<String,String> access =new HashMap<>();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, "POST", API_HOST, uri, access);
        long order=0L;
        try {
            CloseableHttpClient httpClient = manager.getHttpClient();

            HttpPost httpPost = new HttpPost(API_URL + uri+ "?" + toQueryString(access));
            setHeader(httpPost);
            StringEntity postBody =new StringEntity(JsonUtil.writeValue(null),"utf-8");
            postBody.setContentType("application/json; charset=utf-8");
            postBody.setContentEncoding("utf-8");
            httpPost.setEntity(postBody);
            HttpResponse response = httpClient.execute(httpPost);


            String result ="";
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
            System.out.println("执行结果 :" + result);
            JSONObject ds = JSONObject.parseObject(result);
            order = ds.getLong("data");

        } catch (IOException e) {
            System.out.println("执行订单异常");

        }





    }


    public String getCash(int accountId){
        String uri  = "/v1/account/accounts/"+accountId+"/balance";
        Map param = new HashMap();

       return  get(uri,param);
    }

    public long createOrder(){
        String uri = "/v1/order/orders";

        ApiSignature sign = new ApiSignature();
        Map<String,String> param = new HashMap<>();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, "POST", API_HOST, uri, param);
        long order=0L;
        try {

            CreateOrderRequest createOrderReq = new CreateOrderRequest();
            createOrderReq.accountId = String.valueOf(4267079);
            createOrderReq.amount = Double.toString(0.1 * (double)20000 / 10000);

            createOrderReq.symbol = "htusdt";
            createOrderReq.type = CreateOrderRequest.OrderType.BUY_MARKET;
            createOrderReq.source = "api";

            CloseableHttpClient httpClient = manager.getHttpClient();
            HttpPost httpPost = new HttpPost(API_URL + uri+"?"+toQueryString(param));
            setHeader(httpPost);
            StringEntity se= new StringEntity(JsonUtil.writeValue(createOrderReq),"utf-8");
            se.setContentType("application/json; charset=utf-8");
            se.setContentEncoding("utf-8");



            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);

            String result ="";
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }

            System.out.println("order 返回结果:"+ result);
            JSONObject ds = JSONObject.parseObject(result);
            order = ds.getLong("data");

        } catch (IOException e) {
            System.out.println(" 创建订单异常 e");

        }finally {
            return order;
        }

    }

    public List<JSONObject> getAccountAmount(int orderId) throws Exception {
        String uri="/v1/account/accounts/"+orderId+"/balance";
        Map<String,String> param = new HashMap<>();


        JSONObject  ds = JSONObject.parseObject(get(uri,param));
        List<JSONObject> result = new ArrayList();
        String success = ds.getString("status");
        if(success.equals("ok")){
            JSONArray array=  ds.getJSONObject("data").getJSONArray("list");
            List<JSONObject> linkedList = new ArrayList();
            for(int i=0;i<array.size();i++){
                linkedList.add(array.getJSONObject(i));

            }

            linkedList.forEach(domain->{
                if(domain.getString("currency").equals("usdt") || "ht".equals(domain.getString("currency"))){
                    result.add(domain);
                }
            });

        }




        return result;

    }


    public String getOrderInfo(String orderId) throws Exception {
        String uri="/v1/order/orders/" + orderId;
        Map<String,String> param = new HashMap<>();
        return  get(uri,param);

    }

    String get(String uri,Map<String,String > param ){
        ApiSignature sign = new ApiSignature();
        sign.createSignature(this.accessKeyId, this.accessKeySecret, "GET", API_HOST, uri, param);
        CloseableHttpClient httpClient = manager.getHttpClient();
        String result="";
        try{
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
        }catch (Exception e){
            System.out.println("get 请求失败");
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
