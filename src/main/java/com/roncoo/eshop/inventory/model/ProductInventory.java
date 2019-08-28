package com.roncoo.eshop.inventory.model;

public class ProductInventory {
	
	private Long id;
	private Integer value;
	private Long productId;

	public ProductInventory(){

	}

	public ProductInventory(Long id, Long productId, Integer value) {
		this.id = id;
		this.productId = productId;
		this.value = value;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
}
