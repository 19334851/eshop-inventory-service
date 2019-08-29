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
		System.out.println("===========日志===========: 已修改数据库中的库存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getValue());
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
		String key = ProductInventoryServiceImpl.PREFIX + productInventory.getProductId();
		jedis.del(key);
		System.out.println("===========日志===========: 已删除redis中的缓存，key=" + key);
	}

	@Override
	public void setProductInventoryCache(ProductInventory productInventory) {
		Jedis jedis = jedisPool.getResource();
		String key = ProductInventoryServiceImpl.PREFIX + productInventory.getProductId();
		jedis.set(key, JSONObject.toJSONString(productInventory));
		System.out.println("===========日志===========: 已更新商品库存的缓存，商品id=" + productInventory.getProductId() + ", 商品库存数量=" + productInventory.getValue() + ", key=" + key);
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
