  function backToList(obj){
    location.href="/article/listArticles.do";
  }

  function fn_enable(){
      document.getElementById("i_title").disabled=false;
      document.getElementById("i_content").disabled=false;
      document.getElementById("tr_btn_modify").style.display="block";
      document.getElementById("tr_btn").style.display="none";
      $(".tr_modEnable").css('visibility', 'visible');
  }
  
  function submitArticle() {
	const formData = new FormData();
	
	const oldFileNames = [];
	document.querySelectorAll("input[name='oldFileName']").forEach(el => {
	  oldFileNames.push(el.value);
	});
	
	const newFileNames = [];
	document.querySelectorAll("input[name='newFileName']").forEach(el => newFileNames.push(el.value));
	
	const imageFileNO = [];
	document.querySelectorAll("input[name='imageFileNO']").forEach(el => {
	  imageFileNO.push(el.value);
	});


    // JSON 데이터를 Blob으로 추가
    const articleData = {
      articleNO : $("#i_articleNO").val(), 
	  title: $("#i_title").val(),
      content: $("#i_content").val(),
	  oldFileNames : oldFileNames, // 배열로 전달
	  newFileNames: newFileNames,
	  imageFileNO : imageFileNO
    };
    formData.append(
      "article",
      new Blob([JSON.stringify(articleData)], { type: "application/json" })
    );
    // 파일 input 수집 (폼 안의 모든 file input)
	 document.querySelectorAll("input[type='file']").forEach(fileInput => {
      if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
          formData.append("files", fileInput.files[i]);
        }
      }
    });
	 
    // JSON + 파일을 동시에 전송
	fetch("/article/modArticleJson.do", {
      method: "POST",
      body: formData  // multipart/form-data 자동 설정됨
    })
      .then(res => res.json())
      .then(data => {
        if (data.status === "ok") {
          alert(data.message);
          location.href = data.redirectUrl; // 서버에서 내려준 redirect URL
        } else {
          alert("글 수정 실패. 다시 시도해주세요.");
        }
      })
      .catch(err => console.error("Error:", err));
  }

  /*
  function fn_modify_article(obj){
      obj.action="/article/modArticle.do";
      obj.submit();
  }
  */
  

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
  
  function handleFileSelect(input, index) {
    if (input.files && input.files.length > 0) {
      const fileName = input.files[0].name;
      document.getElementById("newFileName" + index).value = fileName;
      
      // 미리보기 이미지 변경
      const reader = new FileReader();
      reader.onload = function(e) {
        document.getElementById("preview" + index).src = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }

  var pre_img_counts = 0;  // 수정 이전의 이미지 수
  var img_counts = 0;      // 수정 후 이미지 수
  var isFirstAddImage = true;
  const newFileNames = []; // 새 이미지 파일명 배열

  function fn_addModImage() {
    var _img_counts = $("#img_counts").val() || 0;
    pre_img_counts = _img_counts;

    if (isFirstAddImage) {
      img_counts = ++_img_counts;
      isFirstAddImage = false;
    } else {
      ++img_counts;
    }

    var innerHtml = "";
    innerHtml += '<tr width="200px" align="center">';
    innerHtml += '<td colspan="2">';
    innerHtml +=
      "<img id='preview" + img_counts + "' height='500' width='500' /><br/>";
    innerHtml +=
      "<input type='file' name='imageFileName" +
      img_counts +
      "' onchange='handleNewFileName(this," +
      img_counts +
      ")' />";
    innerHtml +=
      "<input type='hidden' name='newFileName' id='newFileName" +
      img_counts +
      "'/>" +
      "<input type='hidden' name='imageFileNO' value='0'/>"; // 신규이미지는 imageFileNO=0
    innerHtml += "<input type='button' value='이미지 삭제하기' />";
    innerHtml += "</td>";
    innerHtml += "</tr>";

    $("#tb_addImage").append(innerHtml);

    $("#added_img_counts").val(img_counts); // 추가된 이미지 수 갱신
  }
  
  function handleNewFileName(input, index) {
    if (input.files && input.files[0]) {
      const fileName = input.files[0].name;

      // hidden input에 파일명 저장
      $("#newFileName" + index).val(fileName);

      // 미리보기 이미지 표시
      const reader = new FileReader();
      reader.onload = function (e) {
        $("#preview" + index).attr("src", e.target.result);
      };
      reader.readAsDataURL(input.files[0]);

      // newFileNames 배열에 push
      newFileNames.push(fileName);

      console.log("추가된 파일명:", fileName);
      console.log("현재 newFileNames 배열:", newFileNames);
    }
  }
  		  
  // 이벤트 위임 방식으로 버튼 클릭 처리
  document.addEventListener("click", function(e) {
    if (e.target.classList.contains("btn-remove-image")) {
      const imageFileNO = e.target.dataset.imagefileno;
      const articleNO = e.target.dataset.articleno;
      const imageFileName = e.target.dataset.imagefilename;
      const count = e.target.dataset.count;

      fn_removeModImage(imageFileNO, articleNO, imageFileName, count);
    }
  });


  function fn_removeModImage(_imageFileNO, _articleNO, _imageFileName, rowNum) {
    img_counts = $("#img_counts").val();
	//alert("img_counts: "  +img_counts);
	
    const imageData = {
      articleNO: _articleNO,
      imageFileNO: _imageFileNO,
      imageFileName: _imageFileName,
    };

    fetch("/article/removeModImage.do", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(imageData)
    })
	  .then(res => res.json())
	  .then(data => {
	    if (data.status === "FILE_REMOVE_SUCCESS") {
	      alert(data.message);

		  // ✅ 삭제된 이미지 tr 제거
	       const deletedRow = document.getElementById("tr_" + rowNum);
	       if (deletedRow) {
	         deletedRow.remove();
	       }
   
   // ✅ 삭제 후 이미지 개수 갱신
         let img_counts = parseInt($("#img_counts").val(), 10) || 0;
         img_counts = img_counts - 1; // 삭제했으니 -1
		 newIndex = 1;
         $("#img_counts").val(img_counts); // hidden 값 업데이트
		 
		 // ✅ 남아있는 tr 들만 다시 정렬
		 const imageRows = document.querySelectorAll("tr[id^='tr_']");
			   
		 imageRows.forEach(row => {
			//	alert("newIndex : " + newIndex);
		         if (newIndex > img_counts) return; // 남은 개수까지만

		         // id 재설정
		        row.id = "tr_" + newIndex;

		         // 왼쪽 td 텍스트 수정
		         const tdLabel = row.querySelector("td:first-child");
		         if (tdLabel) {
		           tdLabel.textContent = "이미지" + newIndex;
		         }

	 		// preview 이미지 태그 id 수정
	          const img = row.querySelector("img[id^='preview']");
	          if (img) {
	            img.id = "preview" + (newIndex - 1);
	          }

	        // hidden input (newFileName 등) id 갱신
	        const hidden = row.querySelector("input[name='newFileName']");
	        if (hidden) {
	          hidden.id = "newFileName" + (newIndex - 1);
	        }
			
			// 삭제 버튼 data-count 수정
	        const btn = row.querySelector(".btn-remove-image");
	        if (btn) {
	          btn.dataset.count = newIndex;
	        }

	        newIndex++;
					
	      });
	    } else {
	      alert("이미지 삭제 실패. 다시 시도해주세요.");
	    }
	  })
	  .catch(err => console.error("Error:", err));
	  
  }
	 
