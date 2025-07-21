package com.bookshop01.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import com.bookshop01.order.dao.OrderDAO;
import com.bookshop01.order.vo.OrderVO;


@Service
@Primary
@Transactional(propagation=Propagation.REQUIRED)
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderDAO orderDAO;
	
	public List<OrderVO> listMyOrderGoods(OrderVO orderVO) throws Exception{
		List<OrderVO> orderGoodsList;
		orderGoodsList=orderDAO.listMyOrderGoods(orderVO);
		return orderGoodsList;
	}
	
	public void addNewOrder(List<OrderVO> myOrderList) throws Exception{
		int order_id= orderDAO.selectOrderID();  //주문번호를 얻는다.
		for(int i=0; i<myOrderList.size();i++){
			OrderVO orderVO =(OrderVO)myOrderList.get(i);
			orderVO.setOrder_id(order_id);
			orderDAO.insertNewOrder(orderVO);
			
			//장바구니에서 주문 상품 제거한다.
			orderDAO.deleteGoodsFromCart(orderVO);
		}
	}	
	
	public OrderVO findMyOrder(String order_id) throws Exception{
		return orderDAO.findMyOrder(order_id);
	}

}
