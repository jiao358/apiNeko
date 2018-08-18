package com.neko;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author fuming.lj 2018/8/7
 **/
public class Main {
    public static void main(String[] args) throws Exception{


        ApiClient apiClient= new ApiClient();


        String account=apiClient.getAccountId();
        System.out.println("获取 account 接口:"+ account);
        int accId = 4267079;
        List<JSONObject> str= apiClient.getAccountAmount(4267079);
        System.out.println("当前账户状态:"+str);
        Thread.sleep(8*1000);
        long begin = System.currentTimeMillis();
        long id =apiClient.createOrder();


        String ab= apiClient.getOrderInfo(id+"");

        System.out.println("查询后订单状态为:"+ab);
//        System.out.println("创建orderId"+id+" 并且查询时间:"+(System.currentTimeMillis()-begin)+" ms");
  //      List<JSONObject> bb= apiClient.getAccountAmount(4267079);



    }
}
