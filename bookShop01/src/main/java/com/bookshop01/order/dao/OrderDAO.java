package com.bookshop01.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.order.vo.OrderVO;

@Mapper
public interface OrderDAO {
	public List<OrderVO> listMyOrderGoods(OrderVO orderBean) throws DataAccessException;
	public void insertNewOrder(OrderVO orderVO) throws DataAccessException;
	public OrderVO findMyOrder(String order_id) throws DataAccessException;
	public void deleteGoodsFromCart(OrderVO orderVO)throws DataAccessException;
	public int selectOrderId() throws DataAccessException;
}
