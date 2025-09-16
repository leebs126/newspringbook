
function fn_addNewComment(isLogOn, loginForm, _articleNO){
	console.log(loginForm);
	if(isLogOn == 'true'){  //로그인 상태일 때
		 var textarea_comment = document.getElementById("textarea_comment"+ _articleNO);
		 _contents = textarea_comment.value;
		 
		 if(_contents == null || _contents.length==0){
			 alert("댓글을 입력해주세요.");
			 return;
		 }
			 
		 var commentInfo ={
				 "contents": _contents,
				  "articleNO": _articleNO	 
		 };
		 $.ajax({
	       type:"POST",
		       url:"/comm/addNewComment.do",
		       contentType:"application/json;charset=UTF-8",
		       data: JSON.stringify(commentInfo),
		       success:function (result, textStatus){
		          var jsonComment = JSON.parse(result);
		          var commentInfo = "";
		          
		          commentInfo += jsonComment.replyId +"&nbsp; ";
		          commentInfo += jsonComment.contents +"&nbsp; ";
		          commentInfo += jsonComment.creDate +"&nbsp; ";
		         // alert(jsonComment.creDate);
		          var commentNO = jsonComment.commentNO;
		          var contents = jsonComment.contents;
		          var level = jsonComment.level;
			//	  alert("level: " + level);
		          //ajax로 받아온 새 댓글을 테이블 최상단에 동적으로 추가합니다.
		          var tbody = $("#table_comment tbody");
			       // 새로운 tr 태그를 만듭니다.
		       	  var newRow = $("<tr id=comment_tr" + commentNO + " align=left style='color: green;'>");
		       	  newRow.append($('<td>').html("<span th:each='i : ${#numbers.sequence(1, item.level)}' style='padding-left:10px'/>"+commentInfo + " " +
		       			"<a href='javascript:showTextarea(" + commentNO+ ")' style='text-decoration:none;'>답글작성</a>" + " "+ 
		       			"<a href='javascript:showModtextarea(" + commentNO + ")'  style='text-decoration: none;' >수정</a>" +" "+ 
				 		"<a href='javascript:fn_removeComment("+commentNO +")' style='text-decoration: none;' >삭제</a>" +" "+
				 		"<div id='div_replytextarea"+commentNO+ "' style='display: none'>" + 
			 		 	"<textarea id='replytextarea_comment" + commentNO+"' rows='4' cols='50'  placeholder='답글은 200자까지 가능합니다.' ></textarea>" + 
			 			"<br>" + 
			 			"<button onclick='fn_addReplyComment("+isLogOn+",\""+loginForm+"\"," + commentNO + "," + level + "," + _articleNO+ ")'>" + 
			 			  " 댓글반영하기</button> <button onclick='hideTextarea(" + commentNO + ")'>취소</button>" + 
			 		     "</div>" +
				 		"<div id='div_modtextarea" + commentNO + "'  style='display:none'> " +
				 		 "<textarea id='modtextarea_comment" + commentNO + "' rows=4 cols=50  placeholder='수정글은 200자까지 가능합니다.' >" + contents +"</textarea>" +
				 		 "<br>" +
				 		"<button  onclick='fn_modComment(" + commentNO  + ", " +level+")'>댓글수정하기</button> <button onclick=hideTextarea("+commentNO +")>취소</button>" +
				 		"</div>" 
		       	  ));
		        	// 테이블의 첫 번째 행으로 새로운 행을 추가합니다.
		       	  tbody.prepend(newRow);
				  const textareaId = "textarea_comment" + _articleNO;
		          document.getElementById(textareaId).value = "";
		          
		       },
		      error:function(data,textStatus){
		         alert("에러가 발생했습니다.");
		         alert(data);
		   	  },
		      	complete:function(data,textStatus){
		      }
	   	});  //end ajax	
	 }else{  //비로그인 상태일 때
		 alert("로그인 후 댓글 작성이 가능합니다.")
		    //alert(${article.articleNO});
		    location.href=loginForm+'?action=/article/viewArticle.do&articleNO=' + _articleNO;
	 }
}

function showTextarea(commentNO) {
	//alert(commentNO);
  var textareaDiv = document.getElementById("div_replytextarea"+commentNO);
  textareaDiv.style.display = "block";
}


function hideTextarea(commentNO) {
  //alert(commentNO);
  var textareaDiv = document.getElementById("div_replytextarea"+commentNO);
  textareaDiv.style.display = "none";
}

//수정텍스트에어리어 표시
function showModtextarea(commentNO) {
//	alert(commentNO);
  var textareaDiv = document.getElementById("div_modtextarea"+commentNO);
  textareaDiv.style.display = "block";
}



//수정텍스트에어리어  취소
function hideModtextarea(commentNO) {
  //alert(commentNO);
  var textareaDiv = document.getElementById("div_modtextarea"+commentNO);
  textareaDiv.style.display = "none";
}


function fn_modComment(_commentNO, _level) {
 //alert("_level: " +_level);
  var _modtextarea = document.getElementById("modtextarea_comment"+_commentNO);
  var _contents = _modtextarea.value;
  
  var modCommentInfo ={
		 "contents": _contents,
		  "commentNO": _commentNO
      };
  
  $.ajax({
      type:"POST",
      url:"/comm/modComment.do",
      contentType:"application/json;charset=UTF-8",
      data: JSON.stringify(modCommentInfo),
      success:function (result, textStatus){
         var jsonComment = JSON.parse(result);
         var commentInfo = "";
         
         commentInfo += jsonComment.replyId +" ";
         commentInfo += jsonComment.contents +" ";
         var commentNO = jsonComment.commentNO;
         var contents = jsonComment.contents;
         var articleNO = jsonComment.articleNO;
		 var indentedPx = 0;
         //수정한 글을 levl 만큼 들여쓰기합니다.
         if(_level == 0){
         	indentedPx = 10;  //수정한 댓글을 다시 자신의 level 만큼 들여쓰기를 합니다.
         }else {
			indentedPx = 10 * (_level);
		 }
         //alert("indentedPx: " + indentedPx);
         //수정한 댓글의 tr 태그에 접근합니다.
          var currentRow = $("#comment_tr" + _commentNO);
         // align 속성 추가
          currentRow.attr('align', 'left');
          // style 속성 추가
          currentRow.attr('style', 'color: green;');
      	  currentRow.find('td:first').html("<span  style='padding-left:" + indentedPx+"px; color:green;'>" +commentInfo +  
     			"<a href='javascript:showTextarea(" + commentNO+ ")' style='text-decoration:none;'>답글작성</a>" + " "+ 
     			"<a href='javascript:showModtextarea(" + commentNO + ")'  style='text-decoration: none;' >수정</a>" +" "+
		 		"<a href='javascript:fn_removeComment("+commentNO +")' style='text-decoration: none;' >삭제</a>" +" "+
		 		"<div id='div_replytextarea"+commentNO+ "' style='display: none'>" + 
	 		 	"<textarea id='replytextarea_comment" + commentNO+"' rows='4' cols='50'  placeholder='답글은 200자까지 가능합니다.' ></textarea>" + 
	 			"<br>" + 
	 			"<button onclick='fn_addReplyComment("+true+",\""+"\"," + commentNO + "," + _level + "," + articleNO+ ")'>" + 
	 			  " 댓글반영하기</button> <button onclick='hideTextarea(" + commentNO + ")'>취소</button>" + 
	 		     "</div>" +
		 		"<div id='div_modtextarea" + commentNO + "'  style='display:none'> " +
		 		 "<textarea id='modtextarea_comment" + commentNO + "' rows=4 cols=50  placeholder='수정글은 200자까지 가능합니다.' >" + contents +"</textarea>" +
		 		 "<br>" +
		 		"<button  onclick=fn_modComment(" + commentNO  + "," + _level + ")>댓글반영하기</button> <button onclick=hideTextarea("+commentNO +")>취소</button>" +
		 		"<span></div>" 
      	  );
      	  
      	  hideModtextarea(commentNO);  //수정 후 수정 텍스트에어리어를 닫습니다.
      },
     error:function(data,textStatus){
        alert("에러가 발생했습니다.");
        alert(data);
  	  },
     	complete:function(data,textStatus){
     }
  });  //end ajax	
}

function fn_removeComment(_commentNO){
//	alert(_commentNO);	
	var commentInfo={
			  "commentNO": _commentNO
	      };
	  
	  $.ajax({
	      type:"POST",
	      url:"/comm/removeComment.do",
	      contentType:"application/json;charset=UTF-8",
	      data: JSON.stringify(commentInfo),
	      success:function (result, textStatus){
	    	  //삭제한 댓글의 tr태그를 삭제합니다.
	          var currentRow = $("#comment_tr" + _commentNO);
	    	   currentRow.remove();
	      },
	     error:function(data,textStatus){
	        alert("에러가 발생했습니다.");
	        alert(data);
	  	  },
	     	complete:function(data,textStatus){
	     }
	  });  //end ajax	
	
}

function fn_addReplyComment(isLogOn,loginForm, _pCommentNO, _level, _articleNO){
//	 alert("_pCommentNO: " +_pCommentNO);
	if(isLogOn == 'true' || isLogOn == true){  //로그인 상태일 때
		var _replytextarea = document.getElementById("replytextarea_comment"+_pCommentNO);
		var _replyContents = _replytextarea.value;
			 
		 if(_replyContents == null || _replyContents.length==0){
			 alert("댓글을 입력해주세요.");
			 return;
		 }
		
		 
	 var commentInfo ={
			 "contents": _replyContents,
			  "articleNO": _articleNO,
			  pCommentNO: _pCommentNO
	 	 };
	 $.ajax({
       type:"POST",
	       url:"/comm/addReplyComment.do",
	       contentType:"application/json;charset=UTF-8",
	       data: JSON.stringify(commentInfo),
	       success:function (result, textStatus){
	          var jsonComment = JSON.parse(result);
	          var commentInfo = "";
	          
	          commentInfo += jsonComment.replyID +" ";
	          commentInfo += jsonComment.contents +" ";
	          commentInfo += jsonComment.creDate ;
	    //      alert(commentInfo);
	          var commentNO = jsonComment.commentNO;
	          var comments = jsonComment.contents;
	         
	          var indentedPx = 10 * (++_level);  //답글의 level 만큼 들여쓰기를 합니다.
	          //추가한 댓글을 기존 댓글 아래에 추가합니다.
	          var targetRow  = $("#comment_tr" + _pCommentNO);
	          var newRow = $("<tr id=comment_tr" + commentNO + " align=left style='color: green;'>");
	        newRow.append($("<td><span  style='padding-left:" + indentedPx+"px; color:green;'>" + commentInfo + 
				     			"<a href='javascript:showTextarea(" + commentNO+ ")' style='text-decoration:none;'>답글작성</a>" + " "+ 
				     			"<a href='javascript:showModtextarea(" + commentNO + ")'  style='text-decoration: none;' >수정</a>" +" "+
						 		"<a href='javascript:fn_removeComment("+commentNO +")' style='text-decoration: none;' >삭제</a>" +" "+
						 		"<div id='div_replytextarea"+commentNO+ "' style='display: none'>" + 
					 		 	"<textarea id='replytextarea_comment" + commentNO+"' rows='4' cols='50'  placeholder='답글은 200자까지 가능합니다.' ></textarea>" + 
					 			"<br>" + 
					 			"<button onclick='fn_addReplyComment("+isLogOn+",\""+loginForm+"\"," + commentNO + "," + _level + "," + _articleNO+ ")'>" + 
					 			  " 댓글반영하기</button> <button onclick='hideTextarea(" + commentNO + ")'>취소</button>" + 
					 		     "</div>" +
						 		"<div id='div_modtextarea" + commentNO + "'  style='display:none'> " +
						 		 "<textarea id='modtextarea_comment" + commentNO + "' rows=4 cols=50  placeholder='수정글은 200자까지 가능합니다.' >"+comments +"</textarea>" +
						 		 "<br>" +
						 		"<button  onclick=fn_modComment(" + commentNO  + "," + _level +")>댓글수정하기</button> <button onclick=hideTextarea("+commentNO +")>취소</button>" +
						 		"<span></div></td></tr>" 		
	        ));
	        targetRow.after(newRow);

	        var div_replytextarea = document.getElementById("div_replytextarea"+_pCommentNO);
	        _replytextarea.value="";
	        div_replytextarea.style.display = "none";
	          
	       },
	      error:function(data,textStatus){
	         alert("에러가 발생했습니다.");
	         alert(data);
	   	  },
	      	complete:function(data,textStatus){
	      }
   	});  //end ajax	
 }else {  //비로그인 상태일 때
	 alert("로그인 후 댓글 작성이 가능합니다.")
	 alert(loginForm);
	    //alert(${article.articleNO});
	    location.href=loginForm+'?action=/article/viewArticle.do&articleNO=' + _articleNO;
 }
	 
}


