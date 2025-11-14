  function backToList(obj){
    location.href="/article/listArticles.do";
  }


document.addEventListener('DOMContentLoaded', function() {
  const guestBtn = document.getElementById('create-btn-guest');
  if (!guestBtn) return;

  guestBtn.addEventListener('click', function() {
    if (isLogOn === true) {
      // 로그인 상태면 바로 글쓰기 페이지 이동
      location.href = '/article/articleForm.do';
      return;
    }

    // 로그아웃 상태일 때 로그인 방식 선택
    Swal.fire({
      title: '로그인 후 글 작성이 가능합니다.',
      text: '로그인 방식을 선택해주세요.',
      icon: 'info',
      showCancelButton: true,   // Google 로그인
      showDenyButton: true,     // 취소 버튼 추가
      confirmButtonText: '일반 로그인',
      cancelButtonText: 'Google 로그인',
      denyButtonText: '취소',
      reverseButtons: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#db4437',
      denyButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        // ✅ 일반 로그인
        window.location.href = '/member/loginForm?action=/article/articleForm.do';
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        // ✅ Google 로그인
        window.location.href = '/oauth2/authorize/google?redirectURI=/article/articleForm.do';
      } else if (result.isDenied) {
        // ✅ 취소 (아무 동작 안 함)
        Swal.fire({
          title: '글 작성을 취소했습니다.',
          icon: 'warning',
          timer: 1000,
          showConfirmButton: false
        });
      }
    });
  });
});


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


  function fn_reply_form(isLogOn, url, parentNO, groupNO) {

    if (isLogOn === 'true') {
      // 🔹 로그인 상태 → 기존 답글 작성 로직 그대로 수행
      const form = document.createElement("form");
      form.method = "post";
      form.action = url;

      const parentNOInput = document.createElement("input");
      parentNOInput.type = "hidden";
      parentNOInput.name = "parentNO";
      parentNOInput.value = parentNO;

      const groupNOInput = document.createElement("input");
      groupNOInput.type = "hidden";
      groupNOInput.name = "groupNO";
      groupNOInput.value = groupNO;

      form.appendChild(parentNOInput);
      form.appendChild(groupNOInput);
      document.body.appendChild(form);
      form.submit();

    } else {
      // 🔹 로그아웃 상태 → SweetAlert 로 로그인 방식 선택
      Swal.fire({
        title: '로그인 후 답글 작성이 가능합니다.',
        text: '로그인 방식을 선택해주세요.',
        icon: 'info',
        showCancelButton: true,     // Google 로그인
        showDenyButton: true,       // 취소
        confirmButtonText: '일반 로그인',
        cancelButtonText: 'Google 로그인',
        denyButtonText: '취소',
        reverseButtons: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#db4437',
        denyButtonColor: '#6c757d'
      }).then((result) => {

        if (result.isConfirmed) {
          // 🟦 일반 로그인
          window.location.href =
            `/member/loginForm?action=/article/replyForm&groupNO=${groupNO}&parentNO=${parentNO}`;

        } else if (result.dismiss === Swal.DismissReason.cancel) {
          // 🟥 Google 로그인
          window.location.href =
            `/oauth2/authorize/google?redirectURI=/article/replyForm&groupNO=${groupNO}&parentNO=${parentNO}`;

        } else if (result.isDenied) {
          // 🟨 취소
          Swal.fire({
            title: '답글 작성을 취소했습니다.',
            icon: 'warning',
            timer: 1000,
            showConfirmButton: false
          });
        }

      });
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
	 
