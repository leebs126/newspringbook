package com.bookshop01.order.vo;


import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component("orderVO")
public class OrderVO {
	private int orderSeqNum;
	private String memberId;
	private int orderId;
	private int goodsId;
	private String goodsTitle;
	private int goodsSalesPrice;
	private int totalGoodsPrice;
	private int cartGoodsQty; //장바구니에 담긴 상품수
	private int orderGoodsQty; //최종 주문 상품수
	private String ordererName;   //주문자 이름
	private String receiverName;   //상품수령자 이름
	private String receiverHp1;
	private String receiverHp2;
	private String receiverHp3;
	private String receiverTel1;
	private String receiverTel2;
	private String receiverTel3;
	
	private String deliveryAddress;  //배송 주소
	private String deliveryMessage;
	private String deliveryMethod;
	private String giftWrapping;
	private String payMethod;
	private String cardComName;
	private String cardPayMonth;  //할부개월수
	private String payOrdererHpNum;  //주문자 휴대폰 전화번호
	private String payOrderTime;       //최종 결제 시간
	private String deliveryState;  //현재 주문 상품 배송 상태
	
	private String finalTotalPrice;  //최종 결제 금액
	private int goodsQty;                 
	private String goodsFileName;  //상품 이미지파일명
	private String ordererHp;  //주문자 전화번호
	private int goodsDeliveryPrice; //상품 배송료
	private int goodsPrice;  //상품 정가

}
