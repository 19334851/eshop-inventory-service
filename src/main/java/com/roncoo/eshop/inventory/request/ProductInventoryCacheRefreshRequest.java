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
        ProductInventory productInventory = productInventoryService.findByProductId(productId);
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
