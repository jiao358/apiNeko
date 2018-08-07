package com.neko;

/**
 * @author fuming.lj 2018/8/7
 **/
public class Main {
    public static void main(String[] args) throws Exception{
        ApiClient apiClient= new ApiClient();
        String ab= apiClient.getOrderInfo("9339040475");
        System.out.println(ab);

    }
}
