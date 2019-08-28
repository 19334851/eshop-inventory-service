package com.roncoo.eshop.inventory.request;

public interface Request {

    void process();
    Long getProductId();
    boolean isForceRefresh();
}
