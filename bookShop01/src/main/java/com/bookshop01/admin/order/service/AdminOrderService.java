package com.bookshop01.admin.order.service;

import java.util.Map;


public interface AdminOrderService {
	public Map<String, Object> listNewOrders(Map<String,Object> condMap) throws Exception;
	public void  modifyDeliveryState(Map<String, String> deliveryMap) throws Exception;
	public Map<String, Object> searchOrderDetail(Map<String, String> searchCondMap) throws Exception;
	public Map<String, Object> adminOrderDetail(int orderId) throws Exception;
}
