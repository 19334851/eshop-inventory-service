package com.roncoo.eshop.inventory.mapper;

import org.apache.ibatis.annotations.*;

import com.roncoo.eshop.inventory.model.ProductInventory;

@Mapper
public interface ProductInventoryMapper {
	
	@Insert("INSERT INTO product_inventory(value,product_id) VALUES(#{value},#{productId})")  
	public void add(ProductInventory productInventory);
	
	@Update("UPDATE product_inventory SET value=#{value},product_id=#{productId} WHERE id=#{id}")  
	public void update(ProductInventory productInventory);
	
	@Delete("DELETE FROM product_inventory WHERE id=#{id}")  
	public void delete(Long id);
	
	@Select("SELECT * FROM product_inventory WHERE id=#{id}")  
	public ProductInventory findById(Long id);

	@Select("SELECT * FROM product_inventory WHERE product_id=#{productId}")
	@Results({
			@Result(column = "product_id", property = "productId"),
	})
	public ProductInventory findByProductId(Long productId);
	
}
