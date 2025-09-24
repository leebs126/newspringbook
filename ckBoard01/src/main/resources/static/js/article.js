  function backToList(obj){
    location.href="/article/listArticles.do";
  }



  function fn_remove_article(url, articleNO){
	if (!confirm("정말 이 글을 삭제하시겠습니까?")) return;	
	
      var form = document.createElement("form");
      form.setAttribute("method", "post");
      form.setAttribute("action", url);
      var articleNOInput = document.createElement("input");
      articleNOInput.setAttribute("type","hidden");
      articleNOInput.setAttribute("name","articleNO");
      articleNOInput.setAttribute("value", articleNO);
      form.appendChild(articleNOInput);
      document.body.appendChild(form);
      form.submit();
  }

  function fn_reply_form(isLogOn, url, parentNO, groupNO){
	//alert("groupNO: " + groupNO);
	
     if(isLogOn!= null && isLogOn == 'true'){
          var form = document.createElement("form");
          form.setAttribute("method", "post");
          form.setAttribute("action", url);

          var parentNOInput = document.createElement("input");
          parentNOInput.type="hidden";
          parentNOInput.name="parentNO";
          parentNOInput.value=parentNO;

          var groupNOInput = document.createElement("input");
          groupNOInput.type="hidden";
          groupNOInput.name="groupNO";
          groupNOInput.value=groupNO;

          form.appendChild(parentNOInput);
          form.appendChild(groupNOInput);
          document.body.appendChild(form);
          form.submit();
     }else{
         alert("로그인 후 글쓰기가 가능합니다.");
         location.href="/member/loginForm.do?action=/article/replyForm.do&groupNO=" + groupNO + "&parentNO=" + parentNO;
     }
  }
  

  function fn_modify_enable(){
   
   var div_viewArticle = document.getElementById("div_viewArticle");
   div_viewArticle.style.display = "none";

   var div_mod_article = document.getElementById("div_mod_article");
   div_mod_article.style.display = "block";
  }

  function submitModArticle(button) {
	//  표준 getAttribute 사용
	    var articleNO = button.getAttribute("data-article-no");
	    console.log("getAttribute:", articleNO);
      // CKEditor 본문 HTML 가져오기
      var content = CKEDITOR.instances.ckeditor.getData();

      // 제목 가져오기
      var title = document.querySelector("input[name='title']").value;

      // 글번호(articleNO)는 hidden input이나 data 속성에서 가져오세요.
      //var articleNO =  /*[[${articleMap.article.articleNO}]]*/ '0';

      // 서버에 보낼 데이터 구성
      var articleData = {
          articleNO: articleNO,
          title: title,
          content: content
      };

      // FormData 객체 생성
      var formData = new FormData();
      formData.append("article", new Blob([JSON.stringify(articleData)], {type: "application/json"}));

      // AJAX 요청
      $.ajax({
          url: "/article/modArticleJsonCK.do",
          type: "POST",
          data: formData,
          processData: false,
          contentType: false,
          success: function(response) {
              alert("수정이 완료되었습니다!");
              // 필요시 목록 페이지로 이동
              window.location.href = "/article/viewArticle.do?articleNO=" + articleNO;
          },
          error: function(xhr, status, error) {
              console.error("수정 실패:", error);
              alert("수정 중 오류가 발생했습니다.");
          }
      });
  }
	 
