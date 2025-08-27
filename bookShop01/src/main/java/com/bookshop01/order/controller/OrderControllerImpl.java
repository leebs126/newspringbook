package com.bookshop01.order.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
	@PostMapping("/orderEachGoods.do")
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
	@PostMapping("/orderAllCartGoods.do")
	public ModelAndView orderAllCartGoods( @RequestParam("cartGoodsQty")  String[] cartGoodsQty,
			                 HttpServletRequest request, HttpServletResponse response)  throws Exception{
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/order/orderGoodsForm";
		
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		Map cartMap=(Map)session.getAttribute("cartMap");
		List<OrderVO> myOrderList=new ArrayList<OrderVO>();
		
		List<GoodsVO> myGoodsList=(List<GoodsVO>)cartMap.get("myGoodsList");
		
		for(int i=0; i<cartGoodsQty.length;i++){
			String[] cartGoods=cartGoodsQty[i].split(":");
			for(int j = 0; j< myGoodsList.size();j++) {
				GoodsVO goodsVO = myGoodsList.get(j);
				int goodsId = goodsVO.getGoodsId();
				if(goodsId==Integer.parseInt(cartGoods[0])) {
					OrderVO _orderVO=new OrderVO();
					String goodsTitle=goodsVO.getGoodsTitle();
					int goodsSalesPrice=goodsVO.getGoodsSalesPrice();
					String goodsFileName=goodsVO.getGoodsFileName();
					int goodsPrice = goodsVO.getGoodsPrice();
					_orderVO.setGoodsId(goodsId);
					_orderVO.setGoodsTitle(goodsTitle);
					_orderVO.setGoodsSalesPrice(goodsSalesPrice);
					_orderVO.setGoodsFileName(goodsFileName);
					_orderVO.setGoodsPrice(goodsPrice);
					_orderVO.setOrderGoodsQty(Integer.parseInt(cartGoods[1]));
					myOrderList.add(_orderVO);
					break;
				}
			}
		}
		
		//최종결제금액, 총주문액, 총배송비,총할인액을 구해서 session에 저장하는 메서드
		calcOrderGoodsInfo(request, myOrderList);
		
		return mav;
	}	
	
	@Override
	@PostMapping("/payToOrderGoods.do")
	public ModelAndView payToOrderGoods(@RequestParam Map<String, String> receiverMap,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/order/orderResult";
		ModelAndView mav = new ModelAndView(viewName);
		
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("orderer");
		String memberId=memberVO.getMemberId();
		String ordererName=memberVO.getMemberName();
		String ordererHp = memberVO.getHp1()+"-"+memberVO.getHp2()+"-"+memberVO.getHp3();
		Map<Integer, List<OrderVO>> myOrderMap=(Map)session.getAttribute("myOrderMap");
		
				
		Iterator ite = myOrderMap.keySet().iterator();
		List<OrderVO >myOrderList= null;
		while(ite.hasNext())
		{
			int orderSeq = (Integer)ite.next();
			myOrderList = myOrderMap.get(orderSeq);
		
			for(int i=0; i<myOrderList.size();i++){
				OrderVO orderVO=(OrderVO)myOrderList.get(i);
				orderVO.setMemberId(memberId);
				orderVO.setOrdererName(ordererName);
				orderVO.setReceiverName(receiverMap.get("receiverName"));
				
				orderVO.setReceiverHp1(receiverMap.get("receiverHp1"));
				orderVO.setReceiverHp2(receiverMap.get("receiverHp2"));
				orderVO.setReceiverHp3(receiverMap.get("receiverHp3"));
				orderVO.setReceiverTel1(receiverMap.get("receiverTel1"));
				orderVO.setReceiverTel2(receiverMap.get("receiverTel2"));
				orderVO.setReceiverTel3(receiverMap.get("receiverTel3"));
				
				orderVO.setDeliveryAddress(receiverMap.get("deliveryAddress"));
				orderVO.setDeliveryMessage(receiverMap.get("deliveryMessage"));
				orderVO.setDeliveryMethod(receiverMap.get("deliveryMethod"));
				orderVO.setGiftWrapping(receiverMap.get("giftWrapping"));
				orderVO.setPayMethod(receiverMap.get("payMethod"));
				orderVO.setCardComName(receiverMap.get("cardComName"));
				orderVO.setCardPayMonth(receiverMap.get("cardPayMonth"));
				orderVO.setPayOrdererHpNum(receiverMap.get("payOrdererHpNum"));	
				orderVO.setOrdererHp(ordererHp);	
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
		
		int finalTotalOrdersPrice = 0;  //최종결제금액
		int totalOrderGoodsPrice = 0; 		//총주문액
		int totalDeliveryPrice = 0;		//총배송비
		int totalDiscountedPrice = 0;	//총할인액
		int totalOrderGoodsQty = 0; 	//총주문개수
		int orderGoodsQty = 0;			//총주문수량
		int goodsDiscountPrice = 0;    //각상품 할인판매금액
		for (OrderVO orderVO : myOrderList) {
			orderGoodsQty = orderVO.getOrderGoodsQty();
			totalOrderGoodsPrice+= orderVO.getGoodsPrice() * orderGoodsQty;
			totalOrderGoodsQty+= orderGoodsQty;
			goodsDiscountPrice = (int)(orderVO.getGoodsPrice() * GOODS_DISCOUNT_RATE);
			orderVO.setGoodsDiscountPrice(goodsDiscountPrice);
		}
		
		totalDiscountedPrice = (int)(totalOrderGoodsPrice * GOODS_DISCOUNT_RATE);  //10프로 할인
		
		if(totalOrderGoodsPrice >=AVAILABLE_DELIVERY_ORDER_PRICE) {
			totalDeliveryPrice = 0;
		}else {
			totalDeliveryPrice = GOODS_DELIVERY_PRICE; //1건당 배송비 1500원
		}
		
		finalTotalOrdersPrice = totalOrderGoodsPrice - totalDiscountedPrice;
		
		session.setAttribute("myOrderMap", myOrderMap);
		session.setAttribute("orderer", orderer);
		session.setAttribute("finalTotalOrdersPrice", finalTotalOrdersPrice);
		session.setAttribute("totalOrderGoodsPrice", totalOrderGoodsPrice);
		session.setAttribute("totalOrderGoodsQty", totalOrderGoodsQty);
		session.setAttribute("totalDeliveryPrice", totalDeliveryPrice);
		session.setAttribute("totalDiscountedPrice", totalDiscountedPrice);
		
	}

}
