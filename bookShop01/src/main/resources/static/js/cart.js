function calcGoodsPrice(bookPrice,obj){
	var totalOrderPrice,finalTotalOrderPrice,totalOrderGoodsQty;
	var goods_qty=document.getElementById("select_goods_qty");
	//alert("총 상품금액"+goods_qty.value);
	var p_totalOrderGoodsQty=document.getElementById("p_totalOrderGoodsQty");
	var p_totalOrderPrice=document.getElementById("p_totalOrderPrice");
	var p_final_totalOrderPrice=document.getElementById("p_final_totalOrderPrice");
	var h_totalOrderGoodsQty=document.getElementById("h_totalOrderGoods");
	var h_totalOrderGoodsPrice=document.getElementById("h_totalOrderGoodsPrice");
	var h_totalDeliveryPrice=document.getElementById("h_totalDeliveryPrice");
	var h_finalTotalOrderPrice=document.getElementById("h_final_totalOrderPrice");
	if(obj.checked==true){
	//	alert("체크 했음")
		
		totalOrderGoodsQty=Number(h_totalOrderGoodsQty.value)+Number(goods_qty.value);
		//totalOrderGoods("totalNum:"+totalNum);
		totalOrderPrice=Number(h_totalOrderGoodsPrice.value)+Number(goods_qty.value * bookPrice);
		//alert("totalPrice:"+totalPrice);
		finalTotalOrderPrice=totalOrderPrice+Number(h_totalDeliveryPrice.value);
		//alert("final_total_price:"+final_total_price);

	}else{
	//	alert("h_totalNum.value:"+h_totalNum.value);
		totalOrderGoodsQty=Number(h_totalOrderGoodsQty.value)-Number(goods_qty.value);
	//	alert("totalNum:"+ totalNum);
		totalOrderPrice=Number(h_totalOrderGoodsPrice.value)-Number(goods_qty.value) * bookPrice;
	//	alert("totalPrice="+totalPrice);
		finalTotalOrderPrice = totalOrderPrice - Number(h_totalDeliveryPrice.value);
	//	alert("final_total_price:"+final_total_price);
	}
	
	h_totalOrderGoodsQty.value=totalNum;
	
	h_totalPrice.value=totalPrice;
	h_finalTotalOrderPrice.value=final_total_price;
	
	p_totalOrderGoodsQty.innerHTML=totalOrderGoodsQty;
	p_totalOrderPrice.innerHTML=totalOrderPrice;
	p_final_totalOrderPrice.innerHTML=finalTotalOrderPrice;
}

function modify_cart_qty(goodsId, bookPrice, index){
	//alert(index); 
   var length=document.frm_order_all_cart.cartGoodsQty.length;
   var _cart_goods_qty=0;
	if(length>1){ //카트에 제품이 한개인 경우와 여러개인 경우 나누어서 처리한다.
		_cart_goods_qty=document.frm_order_all_cart.cartGoodsQty[index].value;		
	}else{
		_cart_goods_qty=document.frm_order_all_cart.cartGoodsQty.value;
	}
		
	var cartGoodsQty=Number(_cart_goods_qty);
	//alert("cart_goods_qty:"+cart_goods_qty);
	//console.log(cart_goods_qty);
	$.ajax({
		type : "post",
		async : false, //false인 경우 동기식으로 처리한다.
		url : "/cart/modifyCartQty.do",
		data : {
			goodsId:goodsId,
			cartGoodsQty:cartGoodsQty
		},
		
		success : function(data, textStatus) {
			//alert(data);
			if(data.trim()=='modifySuccess'){
				alert("수량을 변경했습니다!!");	
			}else{
				alert("다시 시도해 주세요!!");	
			}
			
		},
		error : function(data, textStatus) {
			alert("에러가 발생했습니다."+data);
		},
		complete : function(data, textStatus) {
			//alert("작업을완료 했습니다");
			
		}
	}); //end ajax	
}

function delete_cart_goods(cartId){
	var cart_id=Number(cartId);
	var formObj=document.createElement("form");
	var i_cart = document.createElement("input");
	i_cart.name="cartId";
	i_cart.value=cartId;
	
	formObj.appendChild(i_cart);
    document.body.appendChild(formObj); 
    formObj.method="post";
    formObj.action="/cart/removeCartGoods.do";
    formObj.submit();
}

function fn_order_each_goods(goodsId,goodsTitle,goodsPrice, goodsSalesPrice, goodsFileName){
	var total_price,final_total_price,_goods_qty;
	var cartGoodsQty=document.getElementById("cartGoodsQty");
	
	var _orderGoodsQty=cartGoodsQty.value; //장바구니에 담긴 개수 만큼 주문한다.
	var formObj=document.createElement("form");
	var i_goods_id = document.createElement("input"); 
    var i_goods_title = document.createElement("input");
	var i_goods_price=document.createElement("input");
    var i_goods_sales_price=document.createElement("input");
    var i_fileName=document.createElement("input");
    var i_order_goods_qty=document.createElement("input");
    
    i_goods_id.name="goodsId";
    i_goods_title.name="goodsTitle";
	i_goods_price.name="goodsPrice";
	i_goods_sales_price.name="goodsSalesPrice";
    i_fileName.name="goodsFileName";
    i_order_goods_qty.name="orderGoodsQty";
    
    i_goods_id.value=goodsId;
    i_order_goods_qty.value=_orderGoodsQty;
    i_goods_title.value=goodsTitle;
	i_goods_price.value=goodsPrice;
	i_goods_sales_price.value=goodsSalesPrice;
    i_fileName.value=goodsFileName;
    
    formObj.appendChild(i_goods_id);
    formObj.appendChild(i_goods_title);
	formObj.appendChild(i_goods_price);
    formObj.appendChild(i_goods_sales_price);
    formObj.appendChild(i_fileName);
    formObj.appendChild(i_order_goods_qty);

    document.body.appendChild(formObj); 
    formObj.method="post";
    formObj.action="/order/orderEachGoods.do";
    formObj.submit();
}

function fn_order_all_cart_goods(){
//	alert("모두 주문하기");
	var order_goods_qty;
	var order_goods_id;
	var objForm=document.frm_order_all_cart;
	var cartGoodsQty=objForm.cartGoodsQty;
	var h_order_each_goods_qty=objForm.h_order_each_goods_qty;
	var checked_goods=objForm.checked_goods;
	var length=checked_goods.length;
	
	
	//alert(length);
	if(length > 1){
		for(var i=0; i<length;i++){
			if(checked_goods[i].checked==true){
				order_goods_id=checked_goods[i].value;
				order_goods_qty=cartGoodsQty[i].value;
				cartGoodsQty[i].value="";
				cartGoodsQty[i].value=order_goods_id+":"+order_goods_qty;
				//alert(select_goods_qty[i].value);
				console.log(cartGoodsQty[i].value);
			}
		}	
	}else{
		order_goods_id=checked_goods.value;
		order_goods_qty=cartGoodsQty.value;
		cartGoodsQty.value=order_goods_id+":"+order_goods_qty;
		//alert(select_goods_qty.value);
	}
		
 	objForm.method="post";
 	objForm.action="/order/orderAllCartGoods.do";
	objForm.submit();
}