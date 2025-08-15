package com.bookshop01.admin.order.service;

import java.util.Map;


public interface AdminOrderService {
	public Map<String, Object> listNewOrders(Map condMap) throws Exception;
	public void  modifyDeliveryState(Map deliveryMap) throws Exception;
	public Map<String, Object> searchOrderDetail(Map<String, String> searchCondMap) throws Exception;
	public Map adminOrderDetail(int order_id) throws Exception;
}
