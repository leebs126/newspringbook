package com.bookshop01.cart.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.cart.service.CartService;
import com.bookshop01.cart.vo.CartVO;
import com.bookshop01.common.base.BaseController;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@Primary
@RequestMapping(value="/cart")
public class CartControllerImpl extends BaseController implements CartController{
	@Autowired
	private CartService cartService;
	@Autowired
	private CartVO cartVO;
	@Autowired
	private MemberVO memberVO;
	
	
	@Override
	@GetMapping(value="/myCartMain.do")
	public ModelAndView myCartMain(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("memberInfo");
		String memberId=memberVO.getMemberId();
		cartVO.setMemberId(memberId);
		Map<String ,List> cartMap = cartService.myCartList(cartVO);
		
		int totalOrderGoodsQty = 0;  //총주문개수
		int totalDeliveryPrice = 0;  //총배송비
		int totalOrderGoodsPrice = 0; //총상품주문액
		int finalTotalOrderPrice = 0;    //최종결제비
		int totalDiscountedPrice = 0; //총할인금액
		int totalNormalGoodsPrice = 0; //주문 정가 총액
		int OrderGoodsQty = 0;  //각상품별 주문수량개수
		int goodsDiscountPrice = 0;    //각상품 할인액
		if(cartMap ==null) {
			session.setAttribute("cartMap", null);
		}else {
			session.setAttribute("cartMap", cartMap);  				//장바구니 목록 화면에서 상품 주문 시 사용하기 위해서 장바구니 목록을 세션에 저장한다.
			List<CartVO> myCartList = cartMap.get("myCartList");
			
			
			for(CartVO cartVO : myCartList) {
				OrderGoodsQty = cartVO.getCartGoodsQty();
				totalOrderGoodsPrice += (OrderGoodsQty * cartVO.getGoodsPrice());
				totalOrderGoodsQty += OrderGoodsQty;
				goodsDiscountPrice = (int)(cartVO.getGoodsPrice() * GOODS_DISCOUNT_RATE);
				cartVO.setGoodsDiscountPrice(goodsDiscountPrice);
			}
			totalNormalGoodsPrice = totalOrderGoodsPrice - (int)(totalOrderGoodsPrice * GOODS_DISCOUNT_RATE);
		}
		if(totalOrderGoodsPrice >=AVAILABLE_DELIVERY_ORDER_PRICE) {
			totalDeliveryPrice = 0;
		}else {
			totalDeliveryPrice = GOODS_DELIVERY_PRICE; //1건당 배송비 1500원
		}
		
		finalTotalOrderPrice = totalNormalGoodsPrice + totalDeliveryPrice;
		totalDiscountedPrice = (int)(totalOrderGoodsPrice * GOODS_DISCOUNT_RATE);
		
		mav.addObject("totalOrderGoodsQty", totalOrderGoodsQty);
		mav.addObject("totalDeliveryPrice", totalDeliveryPrice);
		mav.addObject("totalDiscountedPrice", totalDiscountedPrice);
		mav.addObject("totalOrderGoodsPrice", totalOrderGoodsPrice);
		mav.addObject("finalTotalOrderPrice", finalTotalOrderPrice);
		return mav;	
		
		
	}
	
	@Override
	@PostMapping(value="/addGoodsInCart.do" ,produces = "application/text; charset=utf8")
	public  @ResponseBody String addGoodsInCart(@ModelAttribute("cartVO") CartVO _cartVO,
			                    HttpServletRequest request, HttpServletResponse response)  throws Exception{
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String memberId=memberVO.getMemberId();
		int goodsId = _cartVO.getGoodsId();
		int goodsPrice = _cartVO.getGoodsPrice();
		
		//카트 등록전에 이미 등록된 제품인지 판별한다.
		cartVO.setGoodsId(goodsId);
		cartVO.setMemberId(memberId);
		cartVO.setGoodsPrice(goodsPrice);
		boolean isAreadyExisted=cartService.findCartGoods(cartVO);
		System.out.println("isAreadyExisted:"+isAreadyExisted);
		if(isAreadyExisted==true){
			return "alreadyExisted";
		}else{
			cartService.addGoodsInCart(cartVO);
			return "addSuccess";
		}
	}
	
	
	@Override
	@PostMapping(value="/modifyCartQty.do")
	public @ResponseBody String  modifyCartQty(@RequestParam("goodsId") int goodsId,
			                                   @RequestParam("cartGoodsQty") int cartGoodsQty,
			                                    HttpServletRequest request, HttpServletResponse response)  throws Exception{
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String memberId=memberVO.getMemberId();
		cartVO.setGoodsId(goodsId);
		cartVO.setMemberId(memberId);
		cartVO.setCartGoodsQty(cartGoodsQty);
		boolean result=cartService.modifyCartQty(cartVO);
		
		if(result==true){
		   return "modifySuccess";
		}else{
			  return "modifyFailed";	
		}
		
	}
	
	
	@Override
	@PostMapping(value="/removeCartGoods.do")
	public ModelAndView removeCartGoods(@RequestParam("cartId") int cartId,
			                          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView mav=new ModelAndView();
		cartService.removeCartGoods(cartId);
		mav.setViewName("redirect:/cart/myCartMain.do");
		return mav;
	}
	
}
