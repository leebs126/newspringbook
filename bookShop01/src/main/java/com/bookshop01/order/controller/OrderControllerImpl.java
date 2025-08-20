package com.bookshop01.order.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.common.base.BaseController;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.service.OrderService;
import com.bookshop01.order.vo.OrderVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@Primary
@RequestMapping(value="/order")
public class OrderControllerImpl extends BaseController implements OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderVO orderVO;
	
	@Override
	@RequestMapping(value="/orderEachGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderEachGoods(@ModelAttribute("orderVO") OrderVO _orderVO,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
		
		request.setCharacterEncoding("utf-8");
		HttpSession session=request.getSession();
		session=request.getSession();
		
		Boolean isLogOn=(Boolean)session.getAttribute("isLogOn");
		String action=(String)session.getAttribute("action");
		//로그인 여부 체크
		//이전에 로그인 상태인 경우는 주문과정 진행
		//로그아웃 상태인 경우 로그인 화면으로 이동
		if(isLogOn==null || isLogOn==false){
			session.setAttribute("orderInfo", _orderVO);
			session.setAttribute("action", "/order/orderEachGoods.do");
			return new ModelAndView("redirect:/member/loginForm.do");
		}else{
			 if(action!=null && action.equals("/order/orderEachGoods.do")){
				orderVO=(OrderVO)session.getAttribute("orderInfo");
				session.removeAttribute("action");
			 }else {
				 orderVO=_orderVO;
			 }
		 }
		
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/order/orderGoodsForm";
		ModelAndView mav = new ModelAndView(viewName);
		
		List<OrderVO> myOrderList=new ArrayList<OrderVO>();
		myOrderList.add(orderVO);
		calcOrderGoodsInfo(request,myOrderList);  //최종결제금액, 총주문액, 총배송비,총할인액을 구해서 session에 저장하는 메서드

		return mav;
	}
	
	@Override
	@RequestMapping(value="/orderAllCartGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderAllCartGoods( @RequestParam("cart_goods_qty")  String[] cart_goods_qty,
			                 HttpServletRequest request, HttpServletResponse response)  throws Exception{
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/order/orderGoodsForm";
		
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		Map cartMap=(Map)session.getAttribute("cartMap");
		List<OrderVO> myOrderList=new ArrayList<OrderVO>();
		
		List<GoodsVO> myGoodsList=(List<GoodsVO>)cartMap.get("myGoodsList");
		
		for(int i=0; i<cart_goods_qty.length;i++){
			String[] cart_goods=cart_goods_qty[i].split(":");
			for(int j = 0; j< myGoodsList.size();j++) {
				GoodsVO goodsVO = myGoodsList.get(j);
				int goods_id = goodsVO.getGoods_id();
				if(goods_id==Integer.parseInt(cart_goods[0])) {
					OrderVO _orderVO=new OrderVO();
					String goods_title=goodsVO.getGoods_title();
					int goods_sales_price=goodsVO.getGoods_sales_price();
					String goods_fileName=goodsVO.getGoods_fileName();
					int goods_price = goodsVO.getGoods_price();
					_orderVO.setGoods_id(goods_id);
					_orderVO.setGoods_title(goods_title);
					_orderVO.setGoods_sales_price(goods_sales_price);
					_orderVO.setGoods_price(goods_price);
					_orderVO.setGoods_fileName(goods_fileName);
					_orderVO.setOrder_goods_qty(Integer.parseInt(cart_goods[1]));
					myOrderList.add(_orderVO);
					break;
				}
			}
		}
		
		//최종결제금액, 총주문액, 총배송비,총할인액을 구해서 session에 저장하는 메서드
		calcOrderGoodsInfo(request,myOrderList);
		
		return mav;
	}	
	
	@Override
	@RequestMapping(value="/payToOrderGoods.do" ,method = RequestMethod.POST)
	public ModelAndView payToOrderGoods(@RequestParam Map<String, String> receiverMap,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/order/orderResult";
		ModelAndView mav = new ModelAndView(viewName);
		
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("orderer");
		String member_id=memberVO.getMemberId();
		String orderer_name=memberVO.getMemberName();
		String orderer_hp = memberVO.getHp1()+"-"+memberVO.getHp2()+"-"+memberVO.getHp3();
		Map<Integer, List<OrderVO>> myOrderMap=(Map)session.getAttribute("myOrderMap");
		
				
		Iterator ite = myOrderMap.keySet().iterator();
		List<OrderVO >myOrderList= null;
		while(ite.hasNext())
		{
			int orderSeq = (Integer)ite.next();
			myOrderList = myOrderMap.get(orderSeq);
		
			for(int i=0; i<myOrderList.size();i++){
				OrderVO orderVO=(OrderVO)myOrderList.get(i);
				orderVO.setMember_id(member_id);
				orderVO.setOrderer_name(orderer_name);
				orderVO.setReceiver_name(receiverMap.get("receiver_name"));
				
				orderVO.setReceiver_hp1(receiverMap.get("receiver_hp1"));
				orderVO.setReceiver_hp2(receiverMap.get("receiver_hp2"));
				orderVO.setReceiver_hp3(receiverMap.get("receiver_hp3"));
				orderVO.setReceiver_tel1(receiverMap.get("receiver_tel1"));
				orderVO.setReceiver_tel2(receiverMap.get("receiver_tel2"));
				orderVO.setReceiver_tel3(receiverMap.get("receiver_tel3"));
				
				orderVO.setDelivery_address(receiverMap.get("delivery_address"));
				orderVO.setDelivery_message(receiverMap.get("delivery_message"));
				orderVO.setDelivery_method(receiverMap.get("delivery_method"));
				orderVO.setGift_wrapping(receiverMap.get("gift_wrapping"));
				orderVO.setPay_method(receiverMap.get("pay_method"));
				orderVO.setCard_com_name(receiverMap.get("card_com_name"));
				orderVO.setCard_pay_month(receiverMap.get("card_pay_month"));
				orderVO.setPay_orderer_hp_num(receiverMap.get("pay_orderer_hp_num"));	
				orderVO.setOrderer_hp(orderer_hp);	
				myOrderList.set(i, orderVO); ; //각 orderVO에 주문자 정보를 세팅한 후 다시 myOrderList에 저장한다.
			}//end for
		}
	    int orderId = orderService.addNewOrder(myOrderList);
	    mav.addObject("orderId", orderId);
		mav.addObject("myOrderInfo",receiverMap);  //OrderVO로 주문결과 페이지에  주문자 정보를 표시한다.
		mav.addObject("myOrderMap", myOrderMap);
		return mav;
	}
	
	
	//주문상품들의 수량, 할인액, 최종결제금액을 구해서 session에 저장하는 메서드
	private void calcOrderGoodsInfo(HttpServletRequest request, List<OrderVO> myOrderList) {
		HttpSession session=request.getSession();
		MemberVO orderer=(MemberVO)session.getAttribute("memberInfo");
		int orderSeq = 0;  //최종 주문 전 주문 리스트에 임시로 할당한 주문번호
		Map<Integer, List<OrderVO>> myOrderMap = new TreeMap<>(Comparator.reverseOrder());
		myOrderMap.put(orderSeq++, myOrderList);
		
		int finalTotalOrderPrice = 0;  //최종결제금액
		int totalOrderPrice = 0; 		//총주문액
		int totalDeliveryPrice = 0;		//총배송비
		int totalDiscountedPrice = 0;	//총할인액
		int totalOrderGoodsQty = 0; 	//총주문개수
		int orderGoodsQty = 0;			//총주문수량
		for (OrderVO orderVO : myOrderList) {
			orderGoodsQty = orderVO.getOrder_goods_qty();
			totalOrderPrice+= orderVO.getGoods_price() * orderGoodsQty;
			totalDeliveryPrice += orderVO.getGoods_delivery_price();
			totalOrderGoodsQty+= orderGoodsQty;
		}
		
		totalDiscountedPrice = (int)(totalOrderPrice * 0.1);  //10프로 할인
		finalTotalOrderPrice = totalOrderPrice - totalDiscountedPrice;
		
		session.setAttribute("myOrderMap", myOrderMap);
		session.setAttribute("orderer", orderer);
		session.setAttribute("finalTotalOrderPrice", finalTotalOrderPrice);
		session.setAttribute("totalOrderPrice", totalOrderPrice);
		session.setAttribute("totalOrderGoodsQty", totalOrderGoodsQty);
		session.setAttribute("totalDeliveryPrice", totalDeliveryPrice);
		session.setAttribute("totalDiscountedPrice", totalDiscountedPrice);
		
	}

}
