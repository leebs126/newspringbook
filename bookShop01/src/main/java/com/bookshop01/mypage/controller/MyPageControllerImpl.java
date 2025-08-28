package com.bookshop01.mypage.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.common.base.BaseController;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.mypage.service.MyPageService;
import com.bookshop01.order.vo.OrderVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@Primary
@RequestMapping(value="/mypage")
public class MyPageControllerImpl extends BaseController  implements MyPageController{
	@Autowired
	private MyPageService myPageService;
	
	@Autowired
	private MemberVO memberVO;
	
	@Override
	@GetMapping(value="/myPageMain.do")
	public ModelAndView myPageMain(@RequestParam(required = false,value="message")  String message,
									@RequestParam Map<String, String> pagingMap,   
									HttpServletRequest request, HttpServletResponse response)  throws Exception {
		HttpSession session=request.getSession();
		session=request.getSession();
		session.setAttribute("side_menu", "my_page"); //마이페이지 사이드 메뉴로 설정한다.
		HashMap<String,String> condMap=new HashMap<String, String>();
		
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String memberId=memberVO.getMemberId();
		condMap.put("memberId", memberId);
		
		String section = pagingMap.get("section");
		String pageNum = pagingMap.get("pageNum");
		
		if(section== null) {
			section = "1";
		}
		condMap.put("section",section);
		if(pageNum== null) {
			pageNum = "1";
		}
		condMap.put("pageNum",pageNum);
//		condMap.put("beginDate",beginDate);
//		condMap.put("endDate", endDate);
		
		
		
		Map<String, Object> _myOrdersMap = myPageService.listMyOrderGoods(condMap);
		List<OrderVO> _myOrdersList = (List<OrderVO>)_myOrdersMap.get("myOrdersList");
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _myOrdersList) {
		    orderIdSet.add(orderVO.getOrderId());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		Map<Integer, List<OrderVO>> myOrdersMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int orderId : orderIdList) {
			List<OrderVO> myOrderList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrdersList) {
		        if (orderId == orderVO.getOrderId()) {
		            myOrderList.add(orderVO);
		        }
		    }
		    myOrdersMap.put(orderId, myOrderList);
		}	
		 
		mav.addObject("message", message);
		mav.addObject("myOrdersMap", myOrdersMap);
		
		//페이징 기능 구현 코드 추가
		int totalOrdersCount = (Integer)_myOrdersMap.get("totalOrdersCount");
		int totalPage = (int) Math.ceil((double)totalOrdersCount / ORDERS_PER_PAGE);
		mav.addObject("totalPage", totalPage);
				

		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
				
		return mav;
	}
	
	@Override
	@GetMapping("/myOrderDetail.do")
	public ModelAndView myOrderDetail(@RequestParam("orderId")  String orderId,
										HttpServletRequest request, 
										HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		MemberVO orderer=(MemberVO)session.getAttribute("memberInfo");
		
		List<OrderVO> _myOrderList = myPageService.findMyOrderInfo(orderId);
		
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _myOrderList) {
		    orderIdSet.add(orderVO.getOrderId());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		Map<Integer, List<OrderVO>> myOrderMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int _orderId : orderIdList) {
			List<OrderVO> myOrderList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrderList) {
		        if (_orderId == orderVO.getOrderId()) {
		            myOrderList.add(orderVO);
		        }
		    }
		    myOrderMap.put(_orderId, myOrderList);
		}	
		
		mav.addObject("myOrderMap", myOrderMap);
		mav.addObject("orderer", orderer);
		
		int finalTotalOrderPrice = 0;  //최종결제금액
		int totalOrderPrice = 0; 		//총주문액
		int totalDeliveryPrice = 0;		//총배송비
		int totalDiscountedPrice = 0;	//총할인액
		int totalOrderGoodsQty = 0; 	//총주문개수
		int orderGoodsQty = 0;			//총주문수량
		int goodsDiscountPrice = 0;    //각상품 할인액
		for (OrderVO orderVO : _myOrderList) {
			orderGoodsQty = orderVO.getOrderGoodsQty();
			totalOrderPrice+= orderVO.getGoodsPrice() * orderGoodsQty;
			totalDeliveryPrice += orderVO.getGoodsDeliveryPrice();
			totalOrderGoodsQty+= orderGoodsQty;
			
			goodsDiscountPrice = (int)(orderVO.getGoodsPrice() * GOODS_DISCOUNT_RATE);
			orderVO.setGoodsDiscountPrice(goodsDiscountPrice);
		}
		
		totalDiscountedPrice = (int)(totalOrderPrice * GOODS_DISCOUNT_RATE);  //10% 할인
		finalTotalOrderPrice = totalOrderPrice - totalDiscountedPrice;
		
		mav.addObject("finalTotalOrderPrice", finalTotalOrderPrice);
		mav.addObject("totalOrderPrice", totalOrderPrice);
		mav.addObject("totalOrderGoodsQty", totalOrderGoodsQty);
		mav.addObject("totalDeliveryPrice", totalDeliveryPrice);
		mav.addObject("totalDiscountedPrice", totalDiscountedPrice);
		
		return mav;
	}
	
	@Override
	@GetMapping(value="/listMyOrderHistory.do")
	public ModelAndView listMyOrderHistory(@RequestParam Map<String, String> dateMap,
			                               HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String  memberId=memberVO.getMemberId();
		
		HashMap<String,String> condMap=new HashMap<String, String>();
		condMap.put("memberId", memberId);
		
		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String beginDate=null,endDate=null;
		
		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate=tempDate[0];
		endDate=tempDate[1];
		condMap.put("beginDate", beginDate);
		condMap.put("endDate", endDate);
		
		String section = dateMap.get("section");
		String pageNum = dateMap.get("pageNum");
		
		
		if(section== null) {
			section = "1";
		}
		condMap.put("section",section);
		
		if(pageNum== null) {
			pageNum = "1";
		}
		condMap.put("pageNum",pageNum);
		
		Map<String, Object> _myOrdersHistMap =myPageService.listMyOrderHistory(condMap);
		String beginDate1[]=beginDate.split("-"); //검색일자를 년,월,일로 분리해서 화면에 전달합니다.
		int[] beginDateInts = new int[beginDate1.length];
		for (int i = 0; i < beginDate1.length; i++) {
		    beginDateInts[i] = Integer.parseInt(beginDate1[i]);
		}
		
		
		String endDate1[]=endDate.split("-");
		int[] endDateInts = new int[endDate1.length];
		for (int i = 0; i < endDate1.length; i++) {
		    endDateInts[i] = Integer.parseInt(endDate1[i]);
		}
		
		mav.addObject("beginYear", beginDateInts[0]);
		mav.addObject("beginMonth", beginDateInts[1]);
		mav.addObject("beginDay", beginDateInts[2]);
		mav.addObject("endYear", endDateInts[0]);
		mav.addObject("endMonth", endDateInts[1]);
		mav.addObject("endDay", endDateInts[2]);
		
		List<OrderVO> _myOrdersHistList = (List<OrderVO>)_myOrdersHistMap.get("myOrdersHistList");
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _myOrdersHistList) {
		    orderIdSet.add(orderVO.getOrderId());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		Map<Integer, List<OrderVO>> myOrderHistMap = new TreeMap<>(Comparator.reverseOrder());
		for (int orderId : orderIdList) {
			List<OrderVO> myOrderHistList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrdersHistList) {
		        if (orderId == orderVO.getOrderId()) {
		        	myOrderHistList.add(orderVO);
		        }
		    }
		    myOrderHistMap.put(orderId, myOrderHistList);
		}	
		 
		mav.addObject("myOrderHistMap", myOrderHistMap);
		
		
		//페이징 기능 구현 코드 추가
		int totalOrdersCount = (Integer)_myOrdersHistMap.get("totalOrdersCount");
		int totalPage = (int) Math.ceil((double)totalOrdersCount / ORDERS_PER_PAGE);
		mav.addObject("totalPage", totalPage);
		
		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
		return mav;
	}	
	
	@Override
	@PostMapping(value="/cancelMyOrder.do")
	public ModelAndView cancelMyOrder(@RequestParam("orderId")  String orderId,
			                         HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView mav = new ModelAndView();
		myPageService.cancelOrder(orderId);
		mav.addObject("message", "cancelOrder");
		mav.setViewName("redirect:/mypage/myPageMain.do");
		return mav;
	}
	
	@Override
	@GetMapping("/myDetailInfo.do")
	public ModelAndView myDetailInfo(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		HttpSession session=request.getSession();
		MemberVO memberVO =(MemberVO)session.getAttribute("memberInfo");
		int memberBirthY = Integer.parseInt(memberVO.getMemberBirthY());
		int memberBirthM = Integer.parseInt(memberVO.getMemberBirthM());
		int memberBirthD = Integer.parseInt(memberVO.getMemberBirthD());
		int memberBirthGn = Integer.parseInt(memberVO.getMemberBirthGn());
		mav.addObject("memberBirthY", memberBirthY);  //생년월일의 년,월,일을 정수로 변환해서 상세페이지로 전달한다.
		mav.addObject("memberBirthM", memberBirthM);
		mav.addObject("memberBirthD", memberBirthD);
		mav.addObject("memberBirthGn", memberBirthGn);
		return mav;
	}	
	
	@Override
	@PostMapping("/modifyMyInfo.do")
	public ResponseEntity modifyMyInfo(@RequestParam("attribute")  String attribute,
			                 @RequestParam("value")  String value,
			               HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Map<String,String> memberMap=new HashMap<String,String>();
		String val[]=null;
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String  memberId=memberVO.getMemberId();
		if(attribute.equals("memberBirth")){
			val=value.split(",");
			memberMap.put("memberBirthY",val[0]);
			memberMap.put("memberBirthM",val[1]);
			memberMap.put("memberBirthD",val[2]);
			memberMap.put("memberBirthGn",val[3]);
		}else if(attribute.equals("tel")){
			val=value.split(",");
			memberMap.put("tel1",val[0]);
			memberMap.put("tel2",val[1]);
			memberMap.put("tel3",val[2]);
		}else if(attribute.equals("hp")){
			val=value.split(",");
			memberMap.put("hp1",val[0]);
			memberMap.put("hp2",val[1]);
			memberMap.put("hp3",val[2]);
			memberMap.put("smsstsYn", val[3]);
		}else if(attribute.equals("email")){
			val=value.split(",");
			memberMap.put("email1",val[0]);
			memberMap.put("email2",val[1]);
			memberMap.put("emailstsYn", val[2]);
		}else if(attribute.equals("address")){
			val=value.split(",");
			memberMap.put("zipcode",val[0]);
			memberMap.put("roadAddress",val[1]);
			memberMap.put("jibunAddress", val[2]);
			memberMap.put("namujiAddress", val[3]);
		}else {
			memberMap.put(attribute,value);	
		}
		
		memberMap.put("memberId", memberId);
		
		//수정된 회원 정보를 다시 세션에 저장한다.
		memberVO=(MemberVO)myPageService.modifyMyInfo(memberMap);
		session.removeAttribute("memberInfo");
		session.setAttribute("memberInfo", memberVO);
		
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		message  = "mod_success";
		resEntity =new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
	}	
	
}
