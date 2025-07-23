	var array_index=0;
	var SERVER_URL="/thumbnails.do";
	function fn_show_next_goods() {
	    var img_sticky = document.getElementById("img_sticky");
	    var cur_goods_num = document.getElementById("cur_goods_num");
	    var _h_goods_id = document.frm_sticky.h_goods_id;
	    var _h_goods_fileName = document.frm_sticky.h_goods_fileName;

	    // 상품 개수
	    var totalGoods = _h_goods_id.length;

	    // 다음 인덱스로 이동, 마지막이면 다시 처음으로 (챗gpt 이용해서 수정))
	    array_index = (array_index + 1) % totalGoods;

	    var goods_id = _h_goods_id[array_index].value;
	    var fileName = _h_goods_fileName[array_index].value;

	    img_sticky.src = SERVER_URL + "?goods_id=" + goods_id + "&fileName=" + fileName;
	    cur_goods_num.innerHTML = array_index + 1;
	}


 function fn_show_previous_goods(){
	var img_sticky=document.getElementById("img_sticky");
	var cur_goods_num=document.getElementById("cur_goods_num");
	var _h_goods_id=document.frm_sticky.h_goods_id;
	var _h_goods_fileName=document.frm_sticky.h_goods_fileName;
	
	if(array_index >0)
		array_index--;
	
	var goods_id=_h_goods_id[array_index].value;
	var fileName=_h_goods_fileName[array_index].value;
	img_sticky.src=SERVER_URL+"?goods_id="+goods_id+"&fileName="+fileName;
	cur_goods_num.innerHTML=array_index+1;
}

function goodsDetail(){
	var cur_goods_num=document.getElementById("cur_goods_num");
	arrIdx=cur_goods_num.innerHTML-1;
	
	var img_sticky=document.getElementById("img_sticky");
	var h_goods_id=document.frm_sticky.h_goods_id;
	var len=h_goods_id.length;
	
	if(len>1){
		goods_id=h_goods_id[arrIdx].value;
	}else{
		goods_id=h_goods_id.value;
	}
	
	
	var formObj=document.createElement("form");
	var i_goods_id = document.createElement("input"); 
    
	i_goods_id.name="goods_id";
	i_goods_id.value=goods_id;
	
    formObj.appendChild(i_goods_id);
    document.body.appendChild(formObj); 
    formObj.method="get";
    formObj.action="/goods/goodsDetail.do?goods_id="+goods_id;
    formObj.submit();
	
	
}