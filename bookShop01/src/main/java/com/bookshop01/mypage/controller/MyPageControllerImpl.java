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
import org.springframework.web.bind.annotation.RequestMethod;
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
		String member_id=memberVO.getMember_id();
		condMap.put("member_id", member_id);
		
		String section = pagingMap.get("section");
		String pageNum = pagingMap.get("pageNum");
//		String beginDate=pagingMap.get("beginDate");
//		String endDate=pagingMap.get("beginDate");
		
//		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
//		beginDate= tempDate[0];
//		endDate =tempDate[1];
		
		
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
		    orderIdSet.add(orderVO.getOrder_id());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		
		Map<Integer, List<OrderVO>> myOrdersMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int orderId : orderIdList) {
			List<OrderVO> myOrderList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrdersList) {
		        if (orderId == orderVO.getOrder_id()) {
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
				
		
		
//		String beginDate1[]=beginDate.split("-");
//		String endDate2[]=endDate.split("-");
//		mav.addObject("beginYear", Integer.parseInt(beginDate1[0]));
//		mav.addObject("beginMonth", Integer.parseInt(beginDate1[1]));
//		mav.addObject("beginDay",  Integer.parseInt(beginDate1[2]));
//		mav.addObject("endYear",  Integer.parseInt(endDate2[0]));
//		mav.addObject("endMonth",  Integer.parseInt(endDate2[1]));
//		mav.addObject("endDay",  Integer.parseInt(endDate2[2]));
		
		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
				
		return mav;
	}
	
	@Override
	@RequestMapping(value="/myOrderDetail.do" ,method = RequestMethod.GET)
	public ModelAndView myOrderDetail(@RequestParam("order_id")  String order_id,HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		MemberVO orderer=(MemberVO)session.getAttribute("memberInfo");
		
		List<OrderVO> _myOrderList = myPageService.findMyOrderInfo(order_id);
		
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _myOrderList) {
		    orderIdSet.add(orderVO.getOrder_id());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		Map<Integer, List<OrderVO>> myOrderMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int orderId : orderIdList) {
			List<OrderVO> myOrderList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrderList) {
		        if (orderId == orderVO.getOrder_id()) {
		            myOrderList.add(orderVO);
		        }
		    }
		    myOrderMap.put(orderId, myOrderList);
		}	
		
		mav.addObject("myOrderMap", myOrderMap);
		mav.addObject("orderer", orderer);
		
		int finalTotalOrderPrice = 0;  //최종결제금액
		int totalOrderPrice = 0; 		//총주문액
		int totalDeliveryPrice = 0;		//총배송비
		int totalDiscountedPrice = 0;	//총할인액
		int totalOrderGoodsQty = 0; 	//총주문개수
		int orderGoodsQty = 0;			//총주문수량
		for (OrderVO orderVO : _myOrderList) {
			orderGoodsQty = orderVO.getOrder_goods_qty();
			totalOrderPrice+= orderVO.getGoods_sales_price() * orderGoodsQty;
			totalDeliveryPrice += orderVO.getGoods_delivery_price();
			totalOrderGoodsQty+= orderGoodsQty;
		}
		
		totalDiscountedPrice = (int)(totalOrderPrice * 0.1);  //10프로 할인
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
		String  member_id=memberVO.getMember_id();
		
		HashMap<String,String> condMap=new HashMap<String, String>();
		condMap.put("member_id", member_id);
		
		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String beginDate=null,endDate=null;
		
		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate=tempDate[0];
		endDate=tempDate[1];
		condMap.put("beginDate", beginDate);
		condMap.put("endDate", endDate);
		condMap.put("member_id", member_id);
		
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
		
//		List<OrderVO> _myOrderHistList=myPageService.listMyOrderHistory(dateMap);
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
		    orderIdSet.add(orderVO.getOrder_id());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		Map<Integer, List<OrderVO>> myOrderHistMap = new TreeMap<>(Comparator.reverseOrder());
		for (int orderId : orderIdList) {
			List<OrderVO> myOrderHistList = new ArrayList<>();
		    for (OrderVO orderVO : _myOrdersHistList) {
		        if (orderId == orderVO.getOrder_id()) {
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
	public ModelAndView cancelMyOrder(@RequestParam("order_id")  String order_id,
			                         HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView mav = new ModelAndView();
		myPageService.cancelOrder(order_id);
		mav.addObject("message", "cancel_order");
		mav.setViewName("redirect:/mypage/myPageMain.do");
		return mav;
	}
	
	@Override
	@RequestMapping(value="/myDetailInfo.do" ,method = RequestMethod.GET)
	public ModelAndView myDetailInfo(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		HttpSession session=request.getSession();
		MemberVO memberVO =(MemberVO)session.getAttribute("memberInfo");
		int member_birth_y = Integer.parseInt(memberVO.getMember_birth_y());
		int member_birth_m = Integer.parseInt(memberVO.getMember_birth_m());
		int member_birth_d = Integer.parseInt(memberVO.getMember_birth_d());
		mav.addObject("member_birth_y", member_birth_y);  //생년월일의 년,월,일을 정수로 변환해서 상세페이지로 전달한다.
		mav.addObject("member_birth_m", member_birth_m);
		mav.addObject("member_birth_d", member_birth_d);
		
		return mav;
	}	
	
	@Override
	@RequestMapping(value="/modifyMyInfo.do" ,method = RequestMethod.POST)
	public ResponseEntity modifyMyInfo(@RequestParam("attribute")  String attribute,
			                 @RequestParam("value")  String value,
			               HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Map<String,String> memberMap=new HashMap<String,String>();
		String val[]=null;
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String  member_id=memberVO.getMember_id();
		if(attribute.equals("member_birth")){
			val=value.split(",");
			memberMap.put("member_birth_y",val[0]);
			memberMap.put("member_birth_m",val[1]);
			memberMap.put("member_birth_d",val[2]);
			memberMap.put("member_birth_gn",val[3]);
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
			memberMap.put("smssts_yn", val[3]);
		}else if(attribute.equals("email")){
			val=value.split(",");
			memberMap.put("email1",val[0]);
			memberMap.put("email2",val[1]);
			memberMap.put("emailsts_yn", val[2]);
		}else if(attribute.equals("address")){
			val=value.split(",");
			memberMap.put("zipcode",val[0]);
			memberMap.put("roadAddress",val[1]);
			memberMap.put("jibunAddress", val[2]);
			memberMap.put("namujiAddress", val[3]);
		}else {
			memberMap.put(attribute,value);	
		}
		
		memberMap.put("member_id", member_id);
		
		//������ ȸ�� ������ �ٽ� ���ǿ� �����Ѵ�.
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
