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
	
	public int addNewOrder(List<OrderVO> myOrderList) throws Exception{
		int orderId= orderDAO.selectOrderId();  //주문번호를 얻는다.
		for(int i=0; i<myOrderList.size();i++){
			OrderVO orderVO =(OrderVO)myOrderList.get(i);
			orderVO.setOrderId(orderId);
			orderDAO.insertNewOrder(orderVO);
			
			//장바구니에서 주문 상품 제거한다.
			orderDAO.deleteGoodsFromCart(orderVO);
		}
		return orderId;
	}	
	
	public OrderVO findMyOrder(String orderId) throws Exception{
		return orderDAO.findMyOrder(orderId);
	}

}
