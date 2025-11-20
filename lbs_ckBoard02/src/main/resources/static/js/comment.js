function fn_addNewComment(isLogOn, loginForm, articleNO, pCommentNO){
    if(isLogOn){  
        // -----------------------------
        // 👉 로그인 상태: 기존 로직 그대로
        // -----------------------------
        var textarea_comment = document.getElementById("textarea_comment" + articleNO);
        _contents = textarea_comment.value;

        if(!_contents){
            alert("댓글을 입력해주세요.");
            return;
        }

        var commentInfo ={
            "contents": _contents,
            "articleNO": articleNO
        };

        $.ajax({
            type:"POST",
            url:"/comm/addNewComment",
            contentType:"application/json;charset=UTF-8",
            data: JSON.stringify(commentInfo),
            success:function (result){
                var jsonComment = JSON.parse(result);

                var commentInfo = "";
                commentInfo += jsonComment.replyId +"&nbsp; ";
                commentInfo += jsonComment.contents +"&nbsp; ";
                commentInfo += jsonComment.createdAt +"&nbsp; ";
				commentInfo += jsonComment.updatedAt +"&nbsp; ";
				
                var commentNO = jsonComment.commentNO;
                var contents = jsonComment.contents;
                var level = jsonComment.level;
                var pCommentNO = jsonComment.pCommentNO;
                var cGroupNO = jsonComment.cGroupNO;
                level++;

                // 새 댓글 동적 추가
                var tbody = $("#table_comment tbody");
                var newRow = $("<tr id=comment_tr" + cGroupNO + " align=left style='color: green;'>");
                newRow.append($('<td>').html(
                    "<div   id=div_comment_"+commentNO +" " +  
                            "data-parent="+pCommentNO+ " " + 
                            "style='margin-bottom:5px;'>" +
                      " <div class=comment-body> " + 
                        "<span style='padding-left:10px'/>"+commentInfo + " " +
                        "<a href='javascript:showTextarea(" + commentNO+ ")'>답글작성</a> " +
                        "<a href='javascript:showModtextarea(" + commentNO + ")'>수정</a> " +
                        "<a href='javascript:fn_removeComment("+commentNO+", "+pCommentNO+","+cGroupNO+")'>삭제</a> " +

                        "<div id='div_replytextarea"+commentNO+"' style='display:none'>" + 
                        "<textarea id='replytextarea_comment" + commentNO+"' rows='4' cols='50'></textarea>" + 
                        "<br>" + 
                        "<button onclick='fn_addReplyComment("+isLogOn+",\""+loginForm+"\"," + commentNO + "," + level + ","+ cGroupNO +","+ articleNO+ ")'>댓글반영하기</button> " +
                        "<button onclick='hideTextarea(" + commentNO + ")'>취소</button>" + 
                        "</div>" +

                        "<div id='div_modtextarea" + commentNO + "' style='display:none'>" +
                        "<textarea id='modtextarea_comment" + commentNO + "' rows=4 cols=50>" + contents +"</textarea>" +
                        "<br>" +
                        "<button onclick='fn_modComment("+commentNO+","+level+")'>댓글수정하기</button> " +
                        "<button onclick='hideTextarea("+commentNO+")'>취소</button>" +
                        "</div>" +
                      "</div>" + 
                      "<div class=replies id=replies_" + commentNO+"></div>" + 
                    "</div>"
                ));
                tbody.prepend(newRow);

                const textareaId = "textarea_comment" + articleNO;
                document.getElementById(textareaId).value = "";
            },
            error:function(){
                alert("에러가 발생했습니다.");
            }
        });

    }else{
        // -----------------------------
        // ❌ 비로그인 시 팝업 띄우기
        // -----------------------------
        openLoginSelectModal(articleNO);
    }
}

function openLoginSelectModal(articleNO, commentNO) {
    const modal = document.getElementById("loginSelectModal");
    modal.style.display = "block";

    // 일반 로그인 클릭
    document.getElementById("btn-normal-login").onclick = function () {
        window.location.href = "/member/loginForm?action=/article/viewArticle&articleNO=" + articleNO +"&commentNO= " + commentNO;
    };

    // Google 로그인 클릭
    document.getElementById("btn-google-login").onclick = function () {
        window.location.href = "/oauth2/authorization/google?action=/article/viewArticle.do&articleNO=" + articleNO + "&commentNO= " + commentNO;
    };
}


function showTextarea(isLogOn, articleNO, commentNO) {
//	alert(commentNO);
//  alert("isLogOn: " + isLogOn);
	if(isLogOn){  
		var textareaDiv = document.getElementById("div_replytextarea"+commentNO);
				textareaDiv.style.display = "block";
    }else{
		openLoginSelectModal(articleNO, commentNO);	
	}
}

/*
function fn_addNewComment(isLogOn, loginForm, articleNO, pCommentNO){
	console.log(loginForm);
	if(isLogOn == 'true'){  //로그인 상태일 때
		 var textarea_comment = document.getElementById("textarea_comment"+ articleNO);
		 _contents = textarea_comment.value;
		 
		 if(_contents == null || _contents.length==0){
			 alert("댓글을 입력해주세요.");
			 return;
		 }
			 
		 var commentInfo ={
				 "contents": _contents,
				  "articleNO": articleNO	 
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
				  var pCommentNO = jsonComment.pCommentNO;
				  var cGroupNO = jsonComment.cGroupNO;
				  level++;
				//  alert("level: " + level);
		          //ajax로 받아온 새 댓글을 테이블 최상단에 동적으로 추가합니다.
				  var tbody = $("#table_comment tbody");
				  		       // 새로운 tr 태그를 만듭니다.
				  	       	  var newRow = $("<tr id=comment_tr" + cGroupNO + " align=left style='color: green;'>");
				  	       	  newRow.append($('<td>').html(
								"<div   id=div_comment_"+commentNO +" " +  
										"data-parent="+pCommentNO+ " " + 
										"style='margin-bottom:5px;'>" +
								  " <div class=comment-body> " + 
								    "<span style='padding-left:10px'/>"+commentInfo + " " +
				  	       			"<a href='javascript:showTextarea(" + commentNO+ ")' style='text-decoration:none;'>답글작성</a>" + " "+ 
				  	       			"<a href='javascript:showModtextarea(" + commentNO + ")'  style='text-decoration: none;' >수정</a>" +" "+ 
				  			 		"<a href='javascript:fn_removeComment("+commentNO+", "+ pCommentNO +"," + cGroupNO+")' style='text-decoration: none;' >삭제</a>" +" "+
				  			 		
									"<div id='div_replytextarea"+commentNO+ "' style='display: none'>" + 
				  		 		 	"<textarea id='replytextarea_comment" + commentNO+"' rows='4' cols='50'  placeholder='답글은 200자까지 가능합니다.' ></textarea>" + 
				  		 			"<br>" + 
				  		 			"<button onclick='fn_addReplyComment("+isLogOn+",\""+loginForm+"\"," + commentNO + "," + level + ","+ cGroupNO +","+ articleNO+ ")'>" + 
				  		 			  " 댓글반영하기</button> <button onclick='hideTextarea(" + commentNO + ")'>취소</button>" + 
				  		 		     "</div>" +
				  			 		
									 "<div id='div_modtextarea" + commentNO + "'  style='display:none'> " +
				  			 		 "<textarea id='modtextarea_comment" + commentNO + "' rows=4 cols=50  placeholder='수정글은 200자까지 가능합니다.' >" + contents +"</textarea>" +
				  			 		 "<br>" +
				  			 		"<button  onclick='fn_modComment(" + commentNO  + ", " +level+")'>댓글수정하기</button> <button onclick=hideTextarea("+commentNO +")>취소</button>" +
				  			 		"</div>" +
								  "</div>" + 
								  "<div class=replies id=replies_" + commentNO+"></div>" + 
								  "</div>" 
								  
				  	       	  ));
				  	        	// 테이블의 첫 번째 행으로 새로운 행을 추가합니다.
				  	       	  tbody.prepend(newRow);
				  const textareaId = "textarea_comment" + articleNO;
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
		    location.href=loginForm+'?action=/article/viewArticle.do&articleNO=' + articleNO;
	 }
}
*/




function hideTextarea(commentNO) {
  //alert(commentNO);
  var textareaDiv = document.getElementById("div_replytextarea"+commentNO);
  textareaDiv.style.display = "none";
}

//수정텍스트에어리어 표시
function showModtextarea(isLogOn, commentNO) {
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
      url:"/comm/modComment",
      contentType:"application/json;charset=UTF-8",
      data: JSON.stringify(modCommentInfo),
      success:function (result, textStatus){
         var jsonComment = JSON.parse(result);
         var commentInfo = "";
         
         commentInfo += jsonComment.replyId +" ";
         commentInfo += jsonComment.contents +" ";
		 commentInfo += jsonComment.createdAt;
		 commentInfo += jsonComment.updatedAt;
		 
		 var commentNO = jsonComment.commentNO;
         var contents = jsonComment.contents;
         var articleNO = jsonComment.articleNO;
		 var cGroupNO = jsonComment.cGroupNO;
		 var indentedPx = 10;
         //수정한 글을 levl 만큼 들여쓰기합니다.
         if(_level == 0){
         	indentedPx = 10;  //수정한 댓글을 다시 자신의 level 만큼 들여쓰기를 합니다.
         }else {
			indentedPx = 10 * (_level);
		 }
		 //alert("_level :"  +_level);
		 // 수정한 댓글 div에 접근
		 var currentDiv = $("#div_comment_" + _commentNO);
		 var updatedHtml =
		   "<span style='padding-left:" + indentedPx + "px; color:green;'>" +commentInfo + "</span> " +
		   "<a href='javascript:showTextarea(" + commentNO + ")' style='text-decoration:none;'>답글작성</a> " +
		   "<a href='javascript:showModtextarea(" + commentNO + ")' style='text-decoration:none;'>수정</a> " +
		   "<a href='javascript:fn_removeComment(" + commentNO + "," + _level + "," + cGroupNO + ")' style='text-decoration:none;'>삭제</a>" +

		   // 답글 textarea
		   "<div id='div_replytextarea" + commentNO + "' style='display:none; margin-left:20px;'>" +
		     "<textarea id='replytextarea_comment" + commentNO + "' rows='4' cols='50' placeholder='답글은 200자까지 가능합니다.'></textarea>" +
		     "<br>" +
		     "<button onclick='fn_addReplyComment(true,\"\", " + commentNO + ", " + (_level+1) + ", " + cGroupNO + ", " + articleNO + ")'>댓글반영하기</button> " +
		     "<button onclick='hideTextarea(" + commentNO + ")'>취소</button>" +
		   "</div>" +

		   // 수정 textarea
		   "<div id='div_modtextarea" + commentNO + "' style='display:none; margin-left:20px;'>" +
		     "<textarea id='modtextarea_comment" + commentNO + "' rows='4' cols='50' placeholder='수정글은 200자까지 가능합니다.'>" +
		       contents +"</textarea>" +
		     "<br>" +
		     "<button onclick='fn_modComment(" + commentNO + ", " + _level + ")'>수정반영하기</button> " +
		     "<button onclick='hideModtextarea(" + commentNO + ")'>취소</button>" +
		   "</div>" 
		   ;

		   // replies 분리 (DOM에서 제거되지만 이벤트 핸들러는 유지됨)
		   var replies = currentDiv.find(".replies").detach();
		   
		   // 기존 div의 내용을 갱신
		   currentDiv.find(".comment-body").html(updatedHtml);
		   // replies 다시 붙이기 (원래 순서대로 부모의 마지막으로 붙음)
		   currentDiv.append(replies)
      	  
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

function fn_removeComment(commentNO, pCommentNO, cGroupNO){
	//alert("pCommentNO : " + pCommentNO);
	//alert("cGroupNO: " + cGroupNO);
	
	if (!confirm("정말 이 댓글을 삭제하시겠습니까?")) return;	
	var commentInfo={
			  "commentNO": commentNO
	      };
	  
	  $.ajax({
	      type:"POST",
	      url:"/comm/removeComment",
	      contentType:"application/json;charset=UTF-8",
	      data: JSON.stringify(commentInfo),
	      success:function (result, textStatus){
			if(pCommentNO==0){
			 var currentRow= $("#comment_tr" + cGroupNO);
			 currentRow.remove();
			}else{
				// ✅ 댓글과 그 하위 자식 댓글들을 DOM에서 삭제
				removeCommentWithChildren(commentNO);	
			}
	      },
	     error:function(result,textStatus){
	        alert("에러가 발생했습니다.");
	        alert(result);
	  	  },
	     	complete:function(result,textStatus){
	     }
	  });  //end ajax	
	
}

// 자식 댓글 재귀적으로 삭제하는 함수
function removeCommentWithChildren(commentNO) {
  // 현재 댓글 div 제거
  const currentDiv = document.getElementById("div_comment_" + commentNO);
  if (currentDiv) {
    currentDiv.remove();
  }

  // 자식 댓글들 찾아서 재귀적으로 제거
  const childDivs = document.querySelectorAll(`[data-parent='${commentNO}']`);
  childDivs.forEach(div => {
    const childCommentNO = div.id.replace("div_comment_", "");
    removeCommentWithChildren(childCommentNO);
  });
}

function fn_addReplyComment(isLogOn, loginForm, _pCommentNO, _pLevel, _cGroupNO, _articleNO){
//	alert("_pLevel: " + _pLevel);
	
	var replyCommentInfo = null;
	if(isLogOn == 'true' || isLogOn == true){  //로그인 상태일 때
		var replytextarea = document.getElementById("replytextarea_comment"+ _pCommentNO);
		var _replyContents = replytextarea.value;
			 
		 if(_replyContents == null || _replyContents.length==0){
			 alert("댓글을 입력해주세요.");
			 return;
		 }
		 
	    replyCommentInfo ={
			  "contents": _replyContents,
			  "articleNO": _articleNO,
			  "pCommentNO": _pCommentNO,
			  "cGroupNO" : _cGroupNO
	 	 };
	 
 	   $.ajax({
       type:"POST",
	       url:"/comm/addReplyComment",
	       contentType:"application/json;charset=UTF-8",
	       data: JSON.stringify(replyCommentInfo),
	       success:function (result, textStatus){
			var jsonComment = JSON.parse(result);

			var articleNO = jsonComment.articleNO;
			var commentNO = jsonComment.commentNO;
			var replyId   = jsonComment.replyId;
			var contents  = jsonComment.contents;
			var creDate   = jsonComment.createdAt;
			var pCommentNO = jsonComment.pCommentNO;
			var cGroupNO = jsonComment.cGroupNO;
			var indent = 10 * _pLevel; // level 만큼 들여쓰기
		//	alert("indent: " + indent);
			// 새로운 대댓글 div 생성
			//th:attr="data-parent=${comment.pCommentNO}"
			var newReplyCommentDiv = `
			  <div id="div_comment_${commentNO}" data-parent=${pCommentNO}"  style="margin-left:10px; color:green;">
			  <div class="comment-body">
				  <span  style="padding-left:${indent}px"></span>
				    <span>${replyId}</span>&nbsp;
				    <span>${contents}</span>&nbsp;
				    <span>${creDate}</span>
				    <a href="javascript:showTextarea(${commentNO})" style="text-decoration:none;">답글작성</a>
				    <a href="javascript:showModtextarea(${commentNO})" style="text-decoration:none;">수정</a>
				    <a href="javascript:fn_removeComment(${commentNO}, ${pCommentNO}, ${cGroupNO})" style="text-decoration:none;">삭제</a>
	
				    <!-- 답글 작성 textarea -->
				    <div id="div_replytextarea${commentNO}" style="display:none; margin-left:20px;">
				      <textarea id="replytextarea_comment${commentNO}" rows="4" cols="50"
				        placeholder="답글은 200자까지 가능합니다."></textarea>
				      <br>
				      <button onclick="fn_addReplyComment(true, '/member/loginForm.do', ${commentNO}, ${_pLevel+1}, ${cGroupNO}, ${articleNO})">
				        댓글반영하기
				      </button>
				      <button onclick="hideTextarea(${commentNO})">취소</button>
				    </div>
	
				    <!-- 수정 textarea -->
				    <div id="div_modtextarea${commentNO}" style="display:none; margin-left:20px;">
				      <textarea id="modtextarea_comment${commentNO}" rows="4" cols="50"
				        placeholder="수정글은 200자까지 가능합니다.">${contents}</textarea>
				      <br>
				      <button onclick="fn_modComment(${commentNO}, ${_pLevel})">수정반영하기</button>
				      <button onclick="hideModtextarea(${commentNO})">취소</button>
				    </div>
				</div>
				<div class="replies" id="replies_${commentNO}"></div>
			  
			  </div>
			`;
			//alert("pCommentNO: " + pCommentNO);
			// 부모 댓글 div 밑에 append
			$("#replies_" + pCommentNO).prepend(newReplyCommentDiv);
			
			//*/ 필요 시 시간 기준으로 정렬
			/*
			var children = parentDiv.children("div[id^='div_comment_']");
			children.sort(function(a, b) {
			    return $(b).data("credate") - $(a).data("credate");
			});
			parentDiv.html(children);*/

	        var div_replytextarea = document.getElementById("div_replytextarea"+pCommentNO);
	        replytextarea.value="";
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
	    location.href=loginForm+'?action=/article/viewArticle&articleNO=' + articleNO;
 }
	 
}


