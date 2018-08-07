package com.neko;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author fuming.lj 2018/8/7
 **/
public class Main {
    public static void main(String[] args) throws Exception{
        ApiClient apiClient= new ApiClient();
        //String ab= apiClient.getOrderInfo("9339040475");
        //System.out.println(ab);
        CreateOrderRequest createOrderReq = new CreateOrderRequest();
        createOrderReq.accountId = String.valueOf(21321);
        createOrderReq.amount = Double.toString(123);

        createOrderReq.symbol = "htusdt";
        createOrderReq.type = CreateOrderRequest.OrderType.BUY_MARKET;
        createOrderReq.source = "api";
        String aa= JsonUtil.writeValue(createOrderReq);
        System.out.println(aa);
    }
}
