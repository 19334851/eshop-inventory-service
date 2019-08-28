package com.roncoo.eshop.inventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roncoo.eshop.inventory.mapper.ProductInventoryMapper;
import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

	static final String PREFIX = "product_inventory_";

	@Autowired
	private ProductInventoryMapper productInventoryMapper;

	@Autowired
	private JedisPool jedisPool;

	public void add(ProductInventory productInventory) {
		productInventoryMapper.add(productInventory);
		this.setProductInventoryCache(productInventory);
	}

	public void update(ProductInventory productInventory) {
		productInventoryMapper.update(productInventory);
	}

	public void delete(Long id) {
		ProductInventory productInventory = findById(id);
		productInventoryMapper.delete(id);
		this.removeProductInventoryCache(productInventory);
	}

	public ProductInventory findById(Long id) {
		return productInventoryMapper.findById(id);
	}

	public ProductInventory findByProductId(Long productId) {
		return productInventoryMapper.findByProductId(productId);
	}

	@Override
	public void removeProductInventoryCache(ProductInventory productInventory) {
		Jedis jedis = jedisPool.getResource();
		jedis.del(ProductInventoryServiceImpl.PREFIX + productInventory.getProductId());
	}

	@Override
	public void setProductInventoryCache(ProductInventory productInventory) {
		Jedis jedis = jedisPool.getResource();
		jedis.set(ProductInventoryServiceImpl.PREFIX + productInventory.getProductId(), JSONObject.toJSONString(productInventory));
	}

	/**
	 * 获取商品库存的缓存
	 * @param productId
	 * @return
	 */
	public ProductInventory getProductInventoryCache(Long productId) {
		Jedis jedis = jedisPool.getResource();
		String dataJSON = jedis.get(ProductInventoryServiceImpl.PREFIX + productId);

		if (dataJSON != null && !"".equals(dataJSON)) {
			JSONObject dataJSONObject = JSONObject.parseObject(dataJSON);
			return new ProductInventory(dataJSONObject.getLong("id"),
					dataJSONObject.getLong("productId"),
					dataJSONObject.getInteger("value"));
		}
		return null;
	}
}
