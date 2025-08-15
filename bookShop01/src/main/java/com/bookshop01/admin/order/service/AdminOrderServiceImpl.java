package com.bookshop01.admin.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bookshop01.admin.order.dao.AdminOrderDAO;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;


@Service
@Primary
@Transactional(propagation=Propagation.REQUIRED)
public class AdminOrderServiceImpl implements AdminOrderService {
	@Autowired
	private AdminOrderDAO adminOrderDAO;
	
	public Map<String, Object>  listNewOrders(Map condMap) throws Exception{
		Map<String, Object> newOrderMap =new HashMap<String, Object>();
		List<OrderVO> newOrdersList = adminOrderDAO.selectNewOrdersList(condMap);
		int totalOrdersCount = adminOrderDAO.selectTotalOrders(condMap);
		newOrderMap.put("newOrdersList", newOrdersList);
		newOrderMap.put("totalOrdersCount", totalOrdersCount);
		
		
		return newOrderMap;
	}
	
	@Override
	public void  modifyDeliveryState(Map deliveryMap) throws Exception {
		adminOrderDAO.updateDeliveryState(deliveryMap);
	}
	
	@Override
	public Map<String, Object> searchOrderDetail(Map<String, String> searchCondMap) throws Exception{
		
		Map<String, Object> searchResultMap = new HashMap<>();
		ArrayList<OrderVO> newOrdersList =adminOrderDAO.selectSearchOrderDetail(searchCondMap);
		OrderVO deliveryInfo=(OrderVO)newOrdersList.get(0);
		String member_id=(String)deliveryInfo.getMember_id();
		MemberVO orderer=adminOrderDAO.selectOrderer(member_id);
		searchResultMap.put("newOrdersList",newOrdersList);
		searchResultMap.put("deliveryInfo",deliveryInfo);
		searchResultMap.put("orderer", orderer);
		return searchResultMap;
	}

	@Override
	public Map adminOrderDetail(int order_id) throws Exception {
		Map orderMap=new HashMap();
		ArrayList<OrderVO> orderList =adminOrderDAO.selectAdminOrderDetail(order_id);
		OrderVO deliveryInfo=(OrderVO)orderList.get(0);
		String member_id=(String)deliveryInfo.getMember_id();
		MemberVO orderer=adminOrderDAO.selectOrderer(member_id);
		orderMap.put("orderList",orderList);
		orderMap.put("deliveryInfo",deliveryInfo);
		orderMap.put("orderer", orderer);
		return orderMap;
	}

}
