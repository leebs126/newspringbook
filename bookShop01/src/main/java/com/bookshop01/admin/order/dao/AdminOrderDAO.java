package com.bookshop01.admin.order.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

@Mapper
public interface AdminOrderDAO {
	public ArrayList<OrderVO> selectNewOrdersList(Map<String,Object> condMap) throws DataAccessException;
	public int selectTotalOrders(Map<String, Object> condMap) throws DataAccessException;
	public void  updateDeliveryState(Map<String,String> deliveryMap) throws DataAccessException;
	public ArrayList<OrderVO> selectSearchOrderDetail(Map<String, String> condMap) throws DataAccessException;
	public MemberVO selectOrderer(String memberId) throws DataAccessException;
	public ArrayList<OrderVO> selectAdminOrderDetail(int orderId) throws DataAccessException;
}
