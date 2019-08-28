package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.ProductInventory;

public interface ProductInventoryService {
	
	public void add(ProductInventory productInventory);
	
	public void update(ProductInventory productInventory);

	/**
	 * 删除Redis中的商品库存的缓存
	 * @param productInventory 商品库存
	 */
	void removeProductInventoryCache(ProductInventory productInventory);

	/**
	 * 设置商品库存的缓存
	 * @param productInventory 商品库存
	 */
	void setProductInventoryCache(ProductInventory productInventory);
	
	public void delete(Long id);
	
	public ProductInventory findById(Long id);

	public ProductInventory findByProductId(Long id);

	/**
	 * 获取商品库存的缓存
	 * @param productId
	 * @return
	 */
	ProductInventory getProductInventoryCache(Long productId);

}
