package com.bookshop01.order.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.order.vo.OrderVO;

public interface OrderController {
	public ModelAndView orderEachGoods( OrderVO _orderVO, HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public ModelAndView orderAllCartGoods(String[] cart_goods_qty, HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public ModelAndView payToOrderGoods(Map<String, String> orderMap, HttpServletRequest request, HttpServletResponse response)  throws Exception;
}
