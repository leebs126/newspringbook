package com.bookshop01.cart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.cart.service.CartService;
import com.bookshop01.cart.vo.CartVO;
import com.bookshop01.common.base.BaseController;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.member.vo.MemberVO;

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
	
	@GetMapping(value="/myCartList.do")
	public ModelAndView myCartMain(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("memberInfo");
		String member_id=memberVO.getMember_id();
		cartVO.setMember_id(member_id);
		Map<String ,List> cartMap = cartService.myCartList(cartVO);
		
		int totalOrderGoodsQty = 0;  //총주문개수
		int totalDeliveryPrice = 0;  //총배송비
		int totalOrderGoodsPrice = 0; //총상품주문액
		int totalDiscountedPrice = 0; //총할인비
		int finalTotalOrderPrice = 0;    //최종결제비
		
		if(cartMap ==null) {
			session.setAttribute("cartMap", null);
		}else {
			session.setAttribute("cartMap", cartMap);  				//장바구니 목록 화면에서 상품 주문 시 사용하기 위해서 장바구니 목록을 세션에 저장한다.
			List<GoodsVO> myGoodsList = cartMap.get("myGoodsList");
			List<CartVO> myCartList = cartMap.get("myCartList");
			
			
			
			for(CartVO cartVO : myCartList) {
				totalOrderGoodsQty += cartVO.getCart_goods_qty();
			}
			
			
			for(GoodsVO goodsVO : myGoodsList) {
				totalOrderGoodsPrice+= goodsVO.getGoods_price();
				totalDeliveryPrice += Integer.parseInt(goodsVO.getGoods_delivery_price());
			}
			
			totalDiscountedPrice = (int)(totalOrderGoodsPrice * 0.1);
			finalTotalOrderPrice = totalOrderGoodsPrice + totalDeliveryPrice - totalDiscountedPrice;
		}
		session.setAttribute("totalOrderGoodsQty", totalOrderGoodsQty);
		session.setAttribute("totalDeliveryPrice", totalDeliveryPrice);
		session.setAttribute("totalOrderGoodsPrice", totalOrderGoodsPrice);
		session.setAttribute("totalDiscountedPrice", totalDiscountedPrice);
		session.setAttribute("finalTotalOrderPrice", finalTotalOrderPrice);
		return mav;	
		
		
	}
	@RequestMapping(value="/addGoodsInCart.do" ,method = RequestMethod.POST,produces = "application/text; charset=utf8")
	public  @ResponseBody String addGoodsInCart(@RequestParam("goods_id") int goods_id,
			                    HttpServletRequest request, HttpServletResponse response)  throws Exception{
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String member_id=memberVO.getMember_id();
		
		cartVO.setMember_id(member_id);
		
		//카트 등록전에 이미 등록된 제품인지 판별한다.
		cartVO.setGoods_id(goods_id);
		cartVO.setMember_id(member_id);
		boolean isAreadyExisted=cartService.findCartGoods(cartVO);
		System.out.println("isAreadyExisted:"+isAreadyExisted);
		if(isAreadyExisted==true){
			return "already_existed";
		}else{
			cartService.addGoodsInCart(cartVO);
			return "add_success";
		}
	}
	
	@PostMapping(value="/modifyCartQty.do")
	public @ResponseBody String  modifyCartQty(@RequestParam("goods_id") int goods_id,
			                                   @RequestParam("cart_goods_qty") int cart_goods_qty,
			                                    HttpServletRequest request, HttpServletResponse response)  throws Exception{
		HttpSession session=request.getSession();
		memberVO=(MemberVO)session.getAttribute("memberInfo");
		String member_id=memberVO.getMember_id();
		cartVO.setGoods_id(goods_id);
		cartVO.setMember_id(member_id);
		cartVO.setCart_goods_qty(cart_goods_qty);
		boolean result=cartService.modifyCartQty(cartVO);
		
		if(result==true){
		   return "modify_success";
		}else{
			  return "modify_failed";	
		}
		
	}
	
	@PostMapping(value="/removeCartGoods.do")
	public ModelAndView removeCartGoods(@RequestParam("cart_id") int cart_id,
			                          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView mav=new ModelAndView();
		cartService.removeCartGoods(cart_id);
		mav.setViewName("redirect:/cart/myCartList.do");
		return mav;
	}
}
