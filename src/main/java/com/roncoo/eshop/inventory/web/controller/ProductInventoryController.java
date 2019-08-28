package com.roncoo.eshop.inventory.web.controller;

import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/product-inventory")
public class ProductInventoryController {

	@Autowired
	private ProductInventoryService productInventoryService;

	@Autowired
	private RequestAsyncProcessService requestAsyncProcessService;
	
	@RequestMapping("/add") 
	@ResponseBody
	public String add(ProductInventory productInventory) {
		try {
			productInventoryService.add(productInventory);
		} catch (Exception e) {
			e.printStackTrace(); 
			return "error";
		}
		return "success";
	}
	
	@RequestMapping("/update") 
	@ResponseBody
	public String update(ProductInventory productInventory) {
		try {
			Request request = new ProductInventoryDBUpdateRequest(productInventory,productInventoryService);
			requestAsyncProcessService.process(request);
		} catch (Exception e) {
			e.printStackTrace(); 
			return "error";
		}
		return "success";
	}
	
	@RequestMapping("/delete") 
	@ResponseBody
	public String delete(Long id) {
		try {
			productInventoryService.delete(id); 
		} catch (Exception e) {
			e.printStackTrace(); 
			return "error";
		}
		return "success";
	}
	
	@RequestMapping("/findById") 
	@ResponseBody
	public ProductInventory findById(Long id){
		try {
			return productInventoryService.findById(id);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return new ProductInventory();
	}

	@RequestMapping("/findByProductId")
	@ResponseBody
	public ProductInventory findByProductId(Long productId){
		try {
			return productInventoryService.findByProductId(productId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ProductInventory();
	}

	@RequestMapping("/getProductInventory")
	@ResponseBody
	public ProductInventory getProductInventory(Long productId){
		ProductInventory productInventory = null;

		try {
			Request request = new ProductInventoryCacheRefreshRequest(productId,productInventoryService,false);
			requestAsyncProcessService.process(request);
			// 将请求扔给service异步去处理以后，就需要while(true)一会儿，在这里hang住
			// 去尝试等待前面有商品库存更新的操作，同时缓存刷新的操作，将最新的数据刷新到缓存中
			long startTime = System.currentTimeMillis();
			long endTime = 0L;
			long waitTime = 0L;
			while (true){
				if(waitTime > 200){
					break;
				}
				// 尝试去redis中读取一次商品库存的缓存数据
				productInventory = productInventoryService.getProductInventoryCache(productId);
				// 如果读取到了结果，那么就返回
				if(productInventory != null){
					return productInventory;
				}else{
					Thread.sleep(20);
					endTime = System.currentTimeMillis();
					waitTime = endTime - startTime;
				}

			}
			// 直接尝试从数据库中读取数据
			productInventory = productInventoryService.findByProductId(productId);
			if(productInventory != null) {
				// 将缓存刷新一下
				// 这个过程，实际上是一个读操作的过程，但是没有放在队列中串行去处理，还是有数据不一致的问题
				request = new ProductInventoryCacheRefreshRequest(
						productId, productInventoryService,true);
				requestAsyncProcessService.process(request);

				// 代码会运行到这里，只有三种情况：
				// 1、就是说，上一次也是读请求，数据刷入了redis，但是redis LRU算法给清理掉了，标志位还是false
				// 所以此时下一个读请求是从缓存中拿不到数据的，再放一个读Request进队列，让数据去刷新一下
				// 2、可能在200ms内，就是读请求在队列中一直积压着，没有等待到它执行（在实际生产环境中，基本是比较坑了）
				// 所以就直接查一次库，然后给队列里塞进去一个刷新缓存的请求
				// 3、数据库里本身就没有，缓存穿透，穿透redis，请求到达mysql库

				return productInventory;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ProductInventory(0L,productId,0);
	}
}
