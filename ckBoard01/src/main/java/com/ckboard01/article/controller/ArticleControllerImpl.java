package com.ckboard01.article.controller;



import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.ckboard01.article.service.ArticleService;
import com.ckboard01.article.vo.ArticleVO;
import com.ckboard01.article.vo.ImageVO;
import com.ckboard01.member.vo.MemberVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@Primary
public class ArticleControllerImpl implements ArticleController {
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\ck_upload";
	private static final int ARTICLES_PER_PAGE = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private ArticleVO articleVO;

	@Override
	@RequestMapping(value = "/article/listArticles.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listArticles(@RequestParam(value="section", required=false) String _section, 
									@RequestParam(value="pageNum", required=false) String _pageNum, 
									HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		int section = Integer.parseInt(((_section==null)? "1":_section) );
		int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
		Map<String, Integer> pagingMap=new HashMap<>();
		pagingMap.put("section", section);
		pagingMap.put("pageNum", pageNum);
		Map _articlesMap = articleService.listArticles(pagingMap);
		List<ArticleVO> _articlesList = (List<ArticleVO>)_articlesMap.get("articlesList");
		
		Set<Integer> groupNOSet = new HashSet<>();
		for (ArticleVO articleVO : _articlesList) {
		    groupNOSet.add(articleVO.getGroupNO());
		}
		
		List<Integer> groupNOList = new ArrayList<>(groupNOSet);
		Map<Integer, List<ArticleVO>> articlesMap = new TreeMap<>(Comparator.reverseOrder());
		
		for (int groupNO : groupNOList) {
			List<ArticleVO> articlesList = new ArrayList<>();
		    for (ArticleVO articleVO : _articlesList) {
		        if (groupNO == articleVO.getGroupNO()) {
		        	articlesList.add(articleVO);
		        }
		    }
		    articlesMap.put(groupNO, articlesList);
		}
		
		//페이징 기능 구현 코드 추가
		int totalArticles = (Integer)_articlesMap.get("totalArticles");
		int totalPages = (int) Math.ceil((double)totalArticles / ARTICLES_PER_PAGE);
		
		
		mav.addObject("section", section);
		mav.addObject("pageNum", pageNum);
		mav.addObject("articlesMap", articlesMap);
		mav.addObject("totalPages", totalPages);
		return mav;
		
	}


	// 다중 이미지 보여주기
	@GetMapping("/article/viewArticle.do")
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, 
									@RequestParam(value="removeCompleted", required=false) String removeCompleted,
									HttpServletRequest request, 
									HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO)session.getAttribute("member");
		
		String memId = null;
		if(memberVO != null) {
			memId = memberVO.getMemId();
		}
		
		Map<String, Object> viewMap = new HashMap();
		viewMap.put("articleNO", articleNO);
		viewMap.put("memId", memId);
		
//		Map articleMap = boardService.viewArticle(articleNO);
		Map articleMap = articleService.viewArticle(viewMap);
		articleMap.put("removeCompleted", removeCompleted );
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("articleMap", articleMap);
		return mav;
	}


	@Override
	@PostMapping("/article/removeArticle.do")
	@ResponseBody
	public ResponseEntity removeArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			articleService.removeArticle(articleNO);
			File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('글을 삭제했습니다.');";
			message += " location.href='" + request.getContextPath() + "/article/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('작업중 오류가 발생했습니다.다시 시도해 주세요.');";
			message += " location.href='" + request.getContextPath() + "/article/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 다중 이미지 글 추가하기
	@Override
	@PostMapping("/article/addNewArticle.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addNewArticle(MultipartHttpServletRequest multipartRequest, 
										HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;

		Map articleMap = new HashMap();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}

		// 로그인 시 세션에 저장된 회원 정보에서 글쓴이 아이디를 얻어와서 Map에 저장합니다.
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String memId = memberVO.getMemId();
		articleMap.put("memId", memId);
		String parentNO = (String)session.getAttribute("parentNO")  ;
		articleMap.put("parentNO" , (parentNO == null ? 0 : parentNO));
		session.removeAttribute("parentNO");
		
		String groupNO = (String)session.getAttribute("groupNO")  ;
		articleMap.put("groupNO" ,  groupNO);
		session.removeAttribute("groupNO");

		List<String> fileList = uploadImageFile(multipartRequest);
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if (fileList != null && fileList.size() != 0) {
			for (String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			articleMap.put("imageFileList", imageFileList);
		}

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			int articleNO = articleService.addNewArticle(articleMap);
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}
			
			message = "<script>";
			message += " alert('글을 추가했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/viewArticle.do?articleNO="+ articleMap.get("articleNO") +"';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = "<script>";
			message += " alert('오류가 발생했습니다. 다시 작성해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/articleForm.do'";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

			
//			Map<String, Object> result = new HashMap<>();
//		    result.put("status", "ok");
//		    result.put("message", "오류가 발생했습니다. 다시 시도해 주세요");
//		    result.put("redirectUrl", "/article/articleForm.do");
//			resEnt = new ResponseEntity(result, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}
	
	
	// 다중 답글 추가하기
	@Override
	@RequestMapping(value = "/article/addReplyArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addReplyArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;

		Map articleMap = new HashMap();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}

		// 로그인 시 세션에 저장된 회원 정보에서 글쓴이 아이디를 얻어와서 Map에 저장합니다.
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String memId = memberVO.getMemId();
		articleMap.put("memId", memId);
		
		//부모글번호와 글그룹번호를 articleMap에 저장한다.
//		articleMap.put("parentNO", parentNO);
//		articleMap.put("groupNO", groupNO);
		

		List<String> fileList = uploadImageFile(multipartRequest);
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if (fileList != null && fileList.size() != 0) {
			for (String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			articleMap.put("imageFileList", imageFileList);
		}

		String message;
		ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			int articleNO = articleService.addReplyArticle(articleMap);
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}

			message = "<script>";
			message += " alert('답글을 추가했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/viewArticle.do?articleNO=" + articleMap.get("articleNO") + "';" ;
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);


		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = "<script>";
			message += " alert('오류발생했습니다. 다시 작성해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/articleForm.do'";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	

	// 다중 이미지 수정 기능
	@PostMapping("/article/modArticleJson.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> modArticleJson(
									        @RequestPart("article") String articleJson,
									        @RequestPart(value = "files", required = false) List<MultipartFile> extraFiles
									) throws Exception {
		
		
		 // JSON → Map 변환
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> articleMap = mapper.readValue(articleJson, new TypeReference<Map<String, Object>>() {});

	 // oldFileName 배열 처리
		List<String> oldFileNamesList = (List<String>) articleMap.get("oldFileNames");
	    System.out.println("기존 파일들: " + oldFileNamesList);
	    
	    List<String> newFileNamesList = (List<String>) articleMap.get("newFileNames");
	    System.out.println("새 파일들: " + newFileNamesList.size());
	    
	    if(extraFiles!= null) {
	    	uploadModImageFile(extraFiles); //수정한 이미지 파일을 업로드한다.
	    }
		
		
		int added_img_counts = newFileNamesList.size();  //새로 추가된 이미지 파일 수 
		int pre_img_counts = oldFileNamesList.size();    //기존 이미지 파일 수 
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		List<ImageVO> modAddImageFileList = new ArrayList<ImageVO>();

		//기존이 이미지만 수정한 경우
		if(pre_img_counts == added_img_counts && newFileNamesList != null && newFileNamesList.size() != 0) {
			List<String> imageFileNOList = (List<String>) articleMap.get("imageFileNO");
			for (int i = 0; i < newFileNamesList.size(); i++) {
				String newFileName = newFileNamesList.get(i);
				if(newFileName!= null && newFileName.length()!= 0) {
					ImageVO imageVO = new ImageVO();
					if (i < pre_img_counts) {
						imageVO.setImageFileName(newFileName);
						imageVO.setImageFileNO(Integer.parseInt(imageFileNOList.get(i)));
						imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
						imageFileList.add(imageVO);
						articleMap.put("imageFileList", imageFileList);
					} else {
						imageVO.setImageFileName(newFileName);
						imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
						modAddImageFileList.add(imageVO);
						articleMap.put("modAddImageFileList", modAddImageFileList);
					}
				}
			}
		}else if(pre_img_counts < added_img_counts && newFileNamesList != null && newFileNamesList.size() != 0) {  //새로 이미지를 추가한 경우
			if (newFileNamesList != null && newFileNamesList.size() != 0) {
				List<String> imageFileNOList = (List<String>) articleMap.get("imageFileNO");
				for (int i = 0; i < added_img_counts; i++) {
					String newFileName = newFileNamesList.get(i);
					if(newFileName!= null && newFileName.length()!= 0) {
						ImageVO imageVO = new ImageVO();
						if (i < pre_img_counts) {
							imageVO.setImageFileName(newFileName);
							imageVO.setImageFileNO(Integer.parseInt(imageFileNOList.get(i)));
							imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
							imageFileList.add(imageVO);
							articleMap.put("imageFileList", imageFileList);
						} else {
							imageVO.setImageFileName(newFileName);
							imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
							modAddImageFileList.add(imageVO);
							articleMap.put("modAddImageFileList", modAddImageFileList);
						}
					}
				}
			}
		
		}else {
//			if (newFileNamesList != null && newFileNamesList.size() != 0) {
//				List<String> imageFileNOList = (List<String>) articleMap.get("imageFileNO");
//				for (int i = 0; i < added_img_counts; i++) {
//					String newFileName = newFileNamesList.get(i);
//					if(newFileName!= null && newFileName.length()!= 0) {
//						ImageVO imageVO = new ImageVO();
//						if (i < pre_img_counts) {
//							imageVO.setImageFileName(newFileName);
//							imageVO.setImageFileNO(Integer.parseInt(imageFileNOList.get(i)));
//							imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
//							imageFileList.add(imageVO);
//							articleMap.put("imageFileList", imageFileList);
//						} else {
//							imageVO.setImageFileName(newFileName);
//							imageVO.setArticleNO(Integer.parseInt((String)articleMap.get("articleNO")));
//							modAddImageFileList.add(imageVO);
//							articleMap.put("modAddImageFileList", modAddImageFileList);
//						}
//					}
//				}
//			}	
		}
		


		String articleNO = (String) articleMap.get("articleNO");
		String message;
		ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			articleService.modArticle(articleMap);
			if (newFileNamesList != null && newFileNamesList.size() != 0) { // 수정한 파일들을 차례대로 업로드한다.
				for (int i = 0; i < newFileNamesList.size(); i++) {
					String newFileName = newFileNamesList.get(i);
					if (i  < pre_img_counts) {  //기존 이미지를 수정한 경우
						if (newFileName != null && newFileName.length()!= 0) {
							File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + newFileName);
							File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
							FileUtils.moveFileToDirectory(srcFile, destDir, true);

							String oldFileName = oldFileNamesList.get(i);
//							String oldFileName = oldFileNames[i];

							File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + oldFileName);
							oldFile.delete();
						}
					}else {  //새로운 이미지를 추가한 경우
						if (newFileName != null && newFileName.length()!= 0) {
							File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + newFileName);
							File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
							FileUtils.moveFileToDirectory(srcFile, destDir, true);
						}
					}
				}
			}

			Map<String, Object> result = new HashMap<>();
		    result.put("status", "ok");
		    result.put("message", "글을 수정했습니다.");
		    result.put("redirectUrl", "/article/viewArticle.do?articleNO=" + articleMap.get("articleNO"));

		    return ResponseEntity.ok(result);
		} catch (Exception e) {

			if (newFileNamesList != null && newFileNamesList.size() != 0) { // 오류 발생 시 temp 폴더에 업로드된 이미지 파일들을 삭제한다.
				for (int i = 0; i < newFileNamesList.size(); i++) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + newFileNamesList.get(i));
					srcFile.delete();
				}

				e.printStackTrace();
			}

			
			Map<String, Object> result = new HashMap<>();
		    result.put("status", "ERROR");
		    result.put("message", "오류 발생. 다시 수정해 주세요.");
		    result.put("redirectUrl", "/article/viewArticle.do?articleNO=" + articleMap.get("articleNO"));
			resEnt = new ResponseEntity(result, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	// 수정하기에서 이미지 삭제 기능
	@PostMapping("/article/removeModImage.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeModImage(@RequestBody Map<String, Object> imageMap,
															HttpServletRequest request, 
															HttpServletResponse response) throws Exception {

	    ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		Map<String, Object> res = new HashMap<>();
		try {
		    
			String imageFileNO = (String) imageMap.get("imageFileNO");
			String imageFileName = (String) imageMap.get("imageFileName");
			String articleNO = (String)imageMap.get("articleNO");;
			
			System.out.println("imageFileNO = " + imageFileNO);
			System.out.println("articleNO = " + articleNO);

			ImageVO imageVO = new ImageVO();
			imageVO.setArticleNO(Integer.parseInt(articleNO));
			imageVO.setImageFileNO(Integer.parseInt(imageFileNO));
			articleService.removeModImage(imageVO);
			
			File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName);
			oldFile.delete();
			
			Map<String, Object> result = new HashMap<>();
		    result.put("status", "FILE_REMOVE_SUCCESS");
		    result.put("message", "이미지를 삭제했습니다.");
		    return ResponseEntity.ok(result);
		} catch (Exception e) {
			Map<String, Object> result = new HashMap<>();
		    result.put("status", "ERROR");
		    result.put("message", "이미지 삭제 중 오류가 발생했습니다.");
		    resEnt = new ResponseEntity(result, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;

	}

	
	@RequestMapping(value = "/article/articleForm.do", method = {RequestMethod.GET , RequestMethod.POST})
	private ModelAndView articleForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}
	
	
	@RequestMapping(value = "/article/replyForm.do", method = {RequestMethod.GET , RequestMethod.POST})
	private ModelAndView replyForm(@RequestParam(value="parentNO",  required=false) String parentNO,
												@RequestParam(value="groupNO",  required=false) String groupNO,
												HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		String _parentNO = (String)session.getAttribute("parentNO");
		String _groupNO = (String)session.getAttribute("groupNO");
		
		parentNO = (parentNO!= null)? parentNO : _parentNO;
		groupNO = (groupNO != null) ? parentNO : _groupNO;
		
		if(parentNO != null) {  
			mav.addObject("parentNO", parentNO);
		}
		
		if(groupNO != null) {
			mav.addObject("groupNO", groupNO);
		}
		mav.setViewName(viewName);
		return mav;
	}
	


	// 새 글 쓰기 시 다중 이미지 업로드하기
	private List<String> uploadImageFile(MultipartHttpServletRequest multipartRequest) throws Exception {
		List<String> fileList = new ArrayList<String>();
		Iterator<String> fileNames = multipartRequest.getFileNames();
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			String originalFileName = mFile.getOriginalFilename();
			if (originalFileName != "" && originalFileName != null) {
				fileList.add(originalFileName);
				File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName);
				if (mFile.getSize() != 0) { // File Null Check
					if (!file.exists()) { // 경로상에 파일이 존재하지 않을 경우
						file.getParentFile().mkdirs(); // 경로에 해당하는 디렉토리들을 생성
						mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + originalFileName)); // 임시로
																													// 전송
					}
				}
			}

		}
		return fileList;
	}

	
	// 수정 시 다중 이미지 업로드하기
	private void uploadModImageFile(List<MultipartFile> multipartFiles) throws Exception {

	    for (MultipartFile mFile : multipartFiles) {
	        if (mFile == null || mFile.isEmpty()) {
	            continue;
	        }

	        String originalFileName = mFile.getOriginalFilename();
	        // 저장 경로
	        File file = new File(ARTICLE_IMAGE_REPO + File.separator + "temp" + File.separator + originalFileName);

	        // 디렉토리 없으면 생성
	        if (!file.getParentFile().exists()) {
	            file.getParentFile().mkdirs();
	        }

	        // 파일 저장
	        mFile.transferTo(file);
	    }

	}

	@PostMapping("/article/imageUpload.do")
	public void articleImageUpload(HttpServletRequest request,
	                               HttpServletResponse response,
	                               @RequestParam("upload") MultipartFile upload) throws Exception {

	    System.out.println("이미지업로드 호출됨: " + upload.getOriginalFilename());
	   // File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName);
	    // 저장 경로 ARTICLE_IMAGE_REPO
	    //String uploadDir = request.getServletContext().getRealPath("/ckupload/");
	    //String uploadDir =
	    File uploadDir = new File(ARTICLE_IMAGE_REPO + "\\" + "temp");
	    if (!uploadDir.exists()) {
	    	uploadDir.mkdirs();
	    }

	    // 저장 파일명 (UUID 적용)
	    String uuid = UUID.randomUUID().toString();
	    String savedName = uuid + "_" + upload.getOriginalFilename();

	    File savedFile = new File(uploadDir, savedName);
	    upload.transferTo(savedFile);

	    // JSON 응답 작성
	    response.setContentType("application/json; charset=UTF-8");
	    PrintWriter out = response.getWriter();

	    Map<String, Object> json = new HashMap<>();
	    json.put("uploaded", 1);
	    json.put("fileName", upload.getOriginalFilename());
	    json.put("uuidAddedFileName", savedName);
	    json.put("url", request.getContextPath() + "/ckImageDownload.do?imageFileName=" + savedName);

	    out.print(new ObjectMapper().writeValueAsString(json));
	    out.flush();
	}


//	// 수정 시 다중 이미지 업로드하기
//	private List<String> uploadModImageFile(MultipartHttpServletRequest multipartRequest) throws Exception {
//		List<String> fileList = new ArrayList<String>();
//		Iterator<String> fileNames = multipartRequest.getFileNames();
//		while (fileNames.hasNext()) {
//			String fileName = fileNames.next();
//			MultipartFile mFile = multipartRequest.getFile(fileName);
//			String originalFileName = mFile.getOriginalFilename();
//			if (originalFileName != "" && originalFileName != null) {
//				fileList.add(originalFileName);
//				File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName);
//				if (mFile.getSize() != 0) { // File Null Check
//					if (!file.exists()) { // 경로상에 파일이 존재하지 않을 경우
//						file.getParentFile().mkdirs(); // 경로에 해당하는 디렉토리들을 생성
//						mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + originalFileName)); // 임시로
//					}
//				}
//			} else {
//				fileList.add(null);
//			}
//
//		}
//		return fileList;
//	}



}
