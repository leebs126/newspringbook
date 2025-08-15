package com.bookshop01.admin.order.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

@Mapper
public interface AdminOrderDAO {
	public ArrayList<OrderVO> selectNewOrdersList(Map condMap) throws DataAccessException;
	public int selectTotalOrders(Map condMap) throws DataAccessException;
	public void  updateDeliveryState(Map deliveryMap) throws DataAccessException;
	public ArrayList<OrderVO> selectSearchOrderDetail(Map<String, String> condMap) throws DataAccessException;
	public MemberVO selectOrderer(String member_id) throws DataAccessException;
	
	public ArrayList<OrderVO> selectAdminOrderDetail(int order_id) throws DataAccessException;
}
