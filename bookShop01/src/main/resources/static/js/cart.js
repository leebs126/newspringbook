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

function modify_cart_qty(goods_id, bookPrice, index){
	//alert(index); 
   var length=document.frm_order_all_cart.cart_goods_qty.length;
   var _cart_goods_qty=0;
	if(length>1){ //카트에 제품이 한개인 경우와 여러개인 경우 나누어서 처리한다.
		_cart_goods_qty=document.frm_order_all_cart.cart_goods_qty[index].value;		
	}else{
		_cart_goods_qty=document.frm_order_all_cart.cart_goods_qty.value;
	}
		
	var cart_goods_qty=Number(_cart_goods_qty);
	//alert("cart_goods_qty:"+cart_goods_qty);
	//console.log(cart_goods_qty);
	$.ajax({
		type : "post",
		async : false, //false인 경우 동기식으로 처리한다.
		url : "/cart/modifyCartQty.do",
		data : {
			goods_id:goods_id,
			cart_goods_qty:cart_goods_qty
		},
		
		success : function(data, textStatus) {
			//alert(data);
			if(data.trim()=='modify_success'){
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

function delete_cart_goods(cart_id){
	var cart_id=Number(cart_id);
	var formObj=document.createElement("form");
	var i_cart = document.createElement("input");
	i_cart.name="cart_id";
	i_cart.value=cart_id;
	
	formObj.appendChild(i_cart);
    document.body.appendChild(formObj); 
    formObj.method="post";
    formObj.action="/cart/removeCartGoods.do";
    formObj.submit();
}

function fn_order_each_goods(goods_id,goods_title,goods_sales_price,fileName){
	var total_price,final_total_price,_goods_qty;
	var cart_goods_qty=document.getElementById("cart_goods_qty");
	
	_order_goods_qty=cart_goods_qty.value; //장바구니에 담긴 개수 만큼 주문한다.
	var formObj=document.createElement("form");
	var i_goods_id = document.createElement("input"); 
    var i_goods_title = document.createElement("input");
    var i_goods_sales_price=document.createElement("input");
    var i_fileName=document.createElement("input");
    var i_order_goods_qty=document.createElement("input");
    
    i_goods_id.name="goods_id";
    i_goods_title.name="goods_title";
    i_goods_sales_price.name="goods_sales_price";
    i_fileName.name="goods_fileName";
    i_order_goods_qty.name="order_goods_qty";
    
    i_goods_id.value=goods_id;
    i_order_goods_qty.value=_order_goods_qty;
    i_goods_title.value=goods_title;
    i_goods_sales_price.value=goods_sales_price;
    i_fileName.value=fileName;
    
    formObj.appendChild(i_goods_id);
    formObj.appendChild(i_goods_title);
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
	var cart_goods_qty=objForm.cart_goods_qty;
	var h_order_each_goods_qty=objForm.h_order_each_goods_qty;
	var checked_goods=objForm.checked_goods;
	var length=checked_goods.length;
	
	
	//alert(length);
	if(length > 1){
		for(var i=0; i<length;i++){
			if(checked_goods[i].checked==true){
				order_goods_id=checked_goods[i].value;
				order_goods_qty=cart_goods_qty[i].value;
				cart_goods_qty[i].value="";
				cart_goods_qty[i].value=order_goods_id+":"+order_goods_qty;
				//alert(select_goods_qty[i].value);
				console.log(cart_goods_qty[i].value);
			}
		}	
	}else{
		order_goods_id=checked_goods.value;
		order_goods_qty=cart_goods_qty.value;
		cart_goods_qty.value=order_goods_id+":"+order_goods_qty;
		//alert(select_goods_qty.value);
	}
		
 	objForm.method="post";
 	objForm.action="/order/orderAllCartGoods.do";
	objForm.submit();
}