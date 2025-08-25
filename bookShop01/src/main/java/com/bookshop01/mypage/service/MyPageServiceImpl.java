package com.bookshop01.mypage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.mypage.dao.MyPageDAO;
import com.bookshop01.order.vo.OrderVO;

@Service
@Primary
@Transactional(propagation=Propagation.REQUIRED)
public class MyPageServiceImpl  implements MyPageService {
	@Autowired
	private MyPageDAO myPageDAO;

	@Override
	public Map<String, Object> listMyOrderGoods(Map<String, String> condMap) throws Exception{
		List<OrderVO> myOrdersList = myPageDAO.selectMyOrderGoodsList(condMap);
		Map<String, Object> myOrdersMap = new HashMap<String, Object>();
		int totalOrdersCount = myPageDAO.selectTotalOrders(condMap);
		myOrdersMap.put("myOrdersList", myOrdersList);
		myOrdersMap.put("totalOrdersCount", totalOrdersCount);
		return myOrdersMap;
	}
	
	@Override
	public List findMyOrderInfo(String orderId) throws Exception{
		return myPageDAO.selectMyOrderInfo(orderId);
	}
	
	@Override
	public Map<String, Object> listMyOrderHistory(Map<String, String> condMap) throws Exception {
		
		List<OrderVO> myOrdersHistList = myPageDAO.selectMyOrderHistoryList(condMap);
		Map<String, Object> myOrdersHistMap = new HashMap<String, Object>();
		int totalOrdersCount = myPageDAO.selectTotalOrders(condMap);
		myOrdersHistMap.put("myOrdersHistList", myOrdersHistList);
		myOrdersHistMap.put("totalOrdersCount", totalOrdersCount);
		return myOrdersHistMap;
	}
	
	@Override
	public MemberVO  modifyMyInfo(Map memberMap) throws Exception{
		 String memberId=(String)memberMap.get("memberId");
		 myPageDAO.updateMyInfo(memberMap);
		 return myPageDAO.selectMyDetailInfo(memberId);
	}
	
	@Override
	public void cancelOrder(String orderId) throws Exception{
		myPageDAO.updateMyOrderCancel(orderId);
	}
	
	@Override
	public MemberVO myDetailInfo(String memberId) throws Exception{
		return myPageDAO.selectMyDetailInfo(memberId);
	}
}
