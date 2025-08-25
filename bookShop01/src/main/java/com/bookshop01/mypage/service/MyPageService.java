package com.bookshop01.mypage.service;

import java.util.List;
import java.util.Map;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

public interface MyPageService{
	public Map<String, Object> listMyOrderGoods(Map<String, String> condMap) throws Exception;
	public List findMyOrderInfo(String orderId) throws Exception;
	public Map<String, Object> listMyOrderHistory(Map<String, String> condMap) throws Exception;
	public MemberVO  modifyMyInfo(Map memberMap) throws Exception;
	public void cancelOrder(String orderId) throws Exception;
	public MemberVO myDetailInfo(String memberId) throws Exception;

}
