package com.bookshop01.admin.order.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public interface AdminOrderController {
	public ModelAndView adminOrderMain(@RequestParam Map<String, String> dateMap,
            HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public ResponseEntity modifyDeliveryState(@RequestParam Map<String, String> deliveryMap, 
            HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public ModelAndView orderDetail(@RequestParam("order_id") int order_id, 
            HttpServletRequest request, HttpServletResponse response)  throws Exception;
	
	
}
