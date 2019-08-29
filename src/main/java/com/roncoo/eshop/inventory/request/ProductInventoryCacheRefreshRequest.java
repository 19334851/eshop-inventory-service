package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

public class ProductInventoryCacheRefreshRequest implements Request{

    /**
     * 商品id
     */
    private Long productId;
    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    /**
     * 是否强制刷新缓存
     */
    private boolean forceRefresh;


    public ProductInventoryCacheRefreshRequest(
            Long productId,
            ProductInventoryService productInventoryService,
            boolean forceRefresh
            ) {
        this.productId = productId;
        this.productInventoryService = productInventoryService;
        this.forceRefresh = forceRefresh;
    }

    @Override
    public void process() {
        System.out.println("===========日志===========: 询到商品最新的库存数量，商品id=" + productId);
        ProductInventory productInventory = productInventoryService.findByProductId(productId);
        System.out.println("===========日志===========: 已查询到商品最新的库存数量，商品id=" + productId + ", 商品库存数量=" + productInventory.getValue());
        productInventoryService.setProductInventoryCache(productInventory);
    }

    /**
     * 获取商品id
     */
    public Long getProductId() {
        return productId;
    }

    @Override
    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
