package com.bookshop01.cart.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.cart.vo.CartVO;

public interface CartController {
	public ModelAndView myCartMain(HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public @ResponseBody String addGoodsInCart(@ModelAttribute("cartVO") CartVO _cartVO,
												HttpServletRequest request, 
												HttpServletResponse response)  throws Exception;
	
	public  @ResponseBody String modifyCartQty(@RequestParam("goodsId") int goodsId,
												@RequestParam("cartQoodsQty") int cartGoodsQty,
												HttpServletRequest request, 
												HttpServletResponse response)  throws Exception;
	
	public ModelAndView removeCartGoods(@RequestParam("cartId") int cartId,
										HttpServletRequest request, 
										HttpServletResponse response)  throws Exception;
	
	

}
