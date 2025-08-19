package com.bookshop01.mypage.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

@Mapper
public interface MyPageDAO {
	public List<OrderVO> selectMyOrderGoodsList(Map<String, String> condMap) throws DataAccessException;
	public int selectTotalOrders(Map<String, String> condMap) throws DataAccessException;
	public List selectMyOrderInfo(String order_id) throws DataAccessException;
	public List<OrderVO> selectMyOrderHistoryList(Map dateMap) throws DataAccessException;
	public void updateMyInfo(Map memberMap) throws DataAccessException;
	public MemberVO selectMyDetailInfo(String member_id) throws DataAccessException;
	public void updateMyOrderCancel(String order_id) throws DataAccessException;
}
