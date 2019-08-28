package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;

import java.util.concurrent.ArrayBlockingQueue;

public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

    @Override
    public void process(Request request) {
        try {
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
            queue.put(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayBlockingQueue<Request> getRoutingQueue(Long productId){
        RequestQueue requestQueue = RequestQueue.getInstance();
        // 先获取productId的hash值
        String key = String.valueOf(productId);
        int h;
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);

        // 对hash值取模，将hash值路由到指定的内存队列中，比如内存队列大小8
        // 用内存队列的数量对hash值取模之后，结果一定是在0~7之间
        // 所以任何一个商品id都会被固定路由到同样的一个内存队列中去的
        int index = (requestQueue.queueSize() - 1) & hash;

        return requestQueue.getQueue(index);
    }

}
