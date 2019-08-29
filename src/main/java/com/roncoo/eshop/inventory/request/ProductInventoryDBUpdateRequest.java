package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

public class ProductInventoryDBUpdateRequest implements Request{

    /**
     * 商品库存
     */
    private ProductInventory productInventory;

    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory,ProductInventoryService productInventoryService){
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        System.out.println("===========日志===========: 数据库更新请求开始执行，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getValue());

        // 删除redis中的缓存
        productInventoryService.removeProductInventoryCache(productInventory);
        // 修改数据库中的库存
        productInventoryService.update(productInventory);

    }

    /**
     * 获取商品id
     */
    public Long getProductId() {
        return productInventory.getProductId();
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
