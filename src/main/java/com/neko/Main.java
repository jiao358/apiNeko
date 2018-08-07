package com.neko;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author fuming.lj 2018/8/7
 **/
public class Main {
    public static void main(String[] args) throws Exception{
        ApiClient apiClient= new ApiClient();
        String ab= apiClient.getOrderInfo("9339040475");
        System.out.println("获取 getOrderInfo 接口"+ab);

        String account=apiClient.getAccountId();
        System.out.println("获取 account 接口:"+ account);


    }
}
