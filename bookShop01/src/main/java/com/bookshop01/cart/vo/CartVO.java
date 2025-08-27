package com.bookshop01.cart.vo;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("cartVO")
public class CartVO {
	private int cartId;
	private int goodsId;
	private String memberId;
	private int cartGoodsQty;
	private String creDate;
	private int goodsPrice;
	private int goodsDiscountPrice;
}
