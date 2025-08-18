package com.bookshop01.admin.order.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.admin.order.service.AdminOrderService;
import com.bookshop01.common.base.BaseController;
import com.bookshop01.order.vo.OrderVO;

@Controller
@Primary
@RequestMapping(value="/admin/order")
public class AdminOrderControllerImpl extends BaseController  implements AdminOrderController{
	@Autowired
	private AdminOrderService adminOrderService;
	
	@Override
	@RequestMapping(value="/adminOrderMain.do" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView adminOrderMain(@RequestParam Map<String, String> dateMap,
			                          HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String section = dateMap.get("section");
		String pageNum = dateMap.get("pageNum");
		String beginDate=dateMap.get("beginDate");
		String endDate=dateMap.get("beginDate");
		
		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate= tempDate[0];
		endDate =tempDate[1];
		
		HashMap<String,Object> condMap=new HashMap<String,Object>();
		if(section== null) {
			section = "1";
		}
		condMap.put("section",section);
		if(pageNum== null) {
			pageNum = "1";
		}
		condMap.put("pageNum",pageNum);
		condMap.put("beginDate",beginDate);
		condMap.put("endDate", endDate);
		
		Map<String, Object> _newOrdersMap = adminOrderService.listNewOrders(condMap);
		List<OrderVO> _newOrdersList = (List<OrderVO>)_newOrdersMap.get("newOrdersList");
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _newOrdersList) {
		    orderIdSet.add(orderVO.getOrder_id());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		
		Map<Integer, List<OrderVO>> newOrdersMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int orderId : orderIdList) {
			List<OrderVO> newOrdersList = new ArrayList<>();
		    for (OrderVO orderVO : _newOrdersList) {
		        if (orderId == orderVO.getOrder_id()) {
		        	newOrdersList.add(orderVO);
		        }
		    }
		    newOrdersMap.put(orderId, newOrdersList);
		}	
		mav.addObject("newOrdersMap", newOrdersMap);
		
		//페이징 기능 구현 코드 추가
		int totalOrdersCount = (Integer)_newOrdersMap.get("totalOrdersCount");
		int OrdersPerPage = 10;  //한 페이지당 표시되는 데이터 수 
		int totalPage = (int) Math.ceil((double)totalOrdersCount / OrdersPerPage);
		mav.addObject("totalPage", totalPage);
				
		
		
		String beginDate1[]=beginDate.split("-");
		String endDate2[]=endDate.split("-");
		mav.addObject("beginYear", Integer.parseInt(beginDate1[0]));
		mav.addObject("beginMonth", Integer.parseInt(beginDate1[1]));
		mav.addObject("beginDay",  Integer.parseInt(beginDate1[2]));
		mav.addObject("endYear",  Integer.parseInt(endDate2[0]));
		mav.addObject("endMonth",  Integer.parseInt(endDate2[1]));
		mav.addObject("endDay",  Integer.parseInt(endDate2[2]));
		
		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
		return mav;
		
	}
	
	@Override
	@RequestMapping(value="/modifyDeliveryState.do" ,method={RequestMethod.POST})
	public ResponseEntity modifyDeliveryState(@RequestParam Map<String, String> deliveryMap, 
			                        HttpServletRequest request, HttpServletResponse response)  throws Exception {
		adminOrderService.modifyDeliveryState(deliveryMap);
		
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		message  = "mod_success";
		resEntity =new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
		
	}
	
	@Override
	@RequestMapping(value="/searchOrderDetail.do" ,method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchOrderDetail(@RequestParam Map<String, String> searchCondMap,
									HttpServletRequest request, HttpServletResponse response)  throws Exception {
//		String viewName=(String)request.getAttribute("viewName");
		String viewName = "/admin/order/adminOrderMain";
		ModelAndView mav = new ModelAndView(viewName);
		
		String section = searchCondMap.get("section");
		String pageNum = searchCondMap.get("pageNum");
		String beginDate=searchCondMap.get("beginDate");
		String endDate=searchCondMap.get("beginDate");
		
		
		Map<String, Object> _newOrdersMap =adminOrderService.searchOrderDetail(searchCondMap);
		List<OrderVO> _newOrdersList = (List<OrderVO>)_newOrdersMap.get("newOrdersList");
		Set<Integer> orderIdSet = new HashSet<>();
		for (OrderVO orderVO : _newOrdersList) {
		    orderIdSet.add(orderVO.getOrder_id());
		}		
		
		List<Integer> orderIdList = new ArrayList<>(orderIdSet);
		
		Map<Integer, List<OrderVO>> newOrdersMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int orderId : orderIdList) {
			List<OrderVO> newOrdersList = new ArrayList<>();
		    for (OrderVO orderVO : _newOrdersList) {
		        if (orderId == orderVO.getOrder_id()) {
		        	newOrdersList.add(orderVO);
		        }
		    }
		    newOrdersMap.put(orderId, newOrdersList);
		}	
		 
		
		
		mav.addObject("newOrdersMap", newOrdersMap);
		
		String beginDate1[]=beginDate.split("-");
		String endDate2[]=endDate.split("-");
		mav.addObject("beginYear", Integer.parseInt(beginDate1[0]));
		mav.addObject("beginMonth", Integer.parseInt(beginDate1[1]));
		mav.addObject("beginDay",  Integer.parseInt(beginDate1[2]));
		mav.addObject("endYear",  Integer.parseInt(endDate2[0]));
		mav.addObject("endMonth",  Integer.parseInt(endDate2[1]));
		mav.addObject("endDay",  Integer.parseInt(endDate2[2]));
		
		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
		
		return mav;
	}

	@Override
	@RequestMapping(value="/adminOrderDetail.do" ,method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView adminOrderDetail(int order_id, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		Map orderDataMap =adminOrderService.adminOrderDetail(order_id);
		List<OrderVO> orderList= (List<OrderVO>)orderDataMap.get("orderList");
		Map<String, List<OrderVO>> orderMap = new HashMap<>();
		orderMap.put("orderList", orderList);
		mav.addObject("orderMap", orderMap);
		mav.addObject("orderDataMap", orderDataMap);
		
		
		int finalTotalOrderPrice = 0;  //최종결제금액
		int totalOrderPrice = 0; 		//총주문액
		int totalDeliveryPrice = 0;		//총배송비
		int totalDiscountedPrice = 0;	//총할인액
		int totalOrderGoodsQty = 0; 	//총주문개수
		int orderGoodsQty = 0;			//총주문수량
		for (OrderVO orderVO : orderList) {
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
	
}
