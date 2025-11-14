package com.springboot.ckb.article.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.ckb.article.domain.Article;
import com.springboot.ckb.article.service.ArticleService;
import com.springboot.ckb.member.domain.Member;
import com.springboot.ckb.member.dto.SessionUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@Primary
public class ArticleControllerImpl implements ArticleController {
	private static final String ARTICLE_CK_IMAGE_REPO = "C:\\board\\ck_upload";
	private static final int ARTICLES_PER_PAGE = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private Article article;

	@Override
	@RequestMapping(value = "/article/listArticles.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listArticles(@RequestParam(value = "section", required = false) String _section,
			@RequestParam(value = "pageNum", required = false) String _pageNum, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		int section = Integer.parseInt(((_section == null) ? "1" : _section));
		int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
		Map<String, Integer> pagingMap = new HashMap<>();
		pagingMap.put("section", section);
		pagingMap.put("pageNum", pageNum);
		Map _articlesMap = articleService.listArticles(pagingMap);
		List<Article> _articlesList = (List<Article>) _articlesMap.get("articlesList");

		Set<Integer> groupNOSet = new HashSet<>();
		for (Article article : _articlesList) {
			groupNOSet.add(article.getGroupNO());
		}

		List<Integer> groupNOList = new ArrayList<>(groupNOSet);
		Map<Integer, List<Article>> articlesMap = new TreeMap<>(Comparator.reverseOrder());

		for (int groupNO : groupNOList) {
			List<Article> articlesList = new ArrayList<>();
			for (Article article : _articlesList) {
				if (groupNO == article.getGroupNO()) {
					articlesList.add(article);
				}
			}
			articlesMap.put(groupNO, articlesList);
		}

		// 페이징 기능 구현 코드 추가
		int totalArticles = (Integer) _articlesMap.get("totalArticles");
		int totalPages = (int) Math.ceil((double) totalArticles / ARTICLES_PER_PAGE);

		mav.addObject("section", section);
		mav.addObject("pageNum", pageNum);
		mav.addObject("articlesMap", articlesMap);
		mav.addObject("totalPages", totalPages);
		return mav;

	}

	// 다중 이미지 보여주기
	@GetMapping("/article/viewArticle.do")
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO,
			@RequestParam(value = "removeCompleted", required = false) String removeCompleted,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		
		Map<String, Object> viewMap = new HashMap();
		viewMap.put("articleNO", articleNO);

		Map articleMap = articleService.viewArticle(viewMap);
		articleMap.put("removeCompleted", removeCompleted);
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
			File destDir = new File(ARTICLE_CK_IMAGE_REPO + "\\" + articleNO);
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
		SessionUser loginUser = (SessionUser) session.getAttribute("loginUser");
		if (loginUser == null) {
		    throw new RuntimeException("로그인이 필요합니다.");
		}

		// ✅ 이메일 기반으로 저장
		String writerEmail = loginUser.getEmail();
		String writerName = loginUser.getNickname();
		
		articleMap.put("writerEmail", writerEmail);
		articleMap.put("writerName", writerName);
		String parentNO = (String) session.getAttribute("parentNO");
		articleMap.put("parentNO", (parentNO == null ? 0 : parentNO));
		session.removeAttribute("parentNO");

		String groupNO = (String) session.getAttribute("groupNO");
		articleMap.put("groupNO", groupNO);
		session.removeAttribute("groupNO");

		String message = null;
		ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			int articleNO = articleService.addNewArticle(articleMap);
			message = "<script>";
			message += " alert('글을 추가했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/viewArticle.do?articleNO="
					+ articleMap.get("articleNO") + "';";
			message += " </script>";
		} catch (Exception e) {
			message = "<script>";
			message += " alert('오류가 발생했습니다. 다시 작성해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/articleForm.do'";
			message += " </script>";
			e.printStackTrace();
		} finally {
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	@Override
	@PostMapping("/article/addReplyArticle.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addReplyArticle(MultipartHttpServletRequest multipartRequest,
			HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;

		Map<String, Object> articleMap = new HashMap();
		Enumeration<String> enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}

		// 로그인 시 세션에 저장된 회원 정보에서 글쓴이 아이디를 얻어와서 Map에 저장합니다.
		HttpSession session = multipartRequest.getSession();
		SessionUser loginUser = (SessionUser) session.getAttribute("loginUser");
		String memId = null;
		if (loginUser == null) {
		    throw new RuntimeException("로그인이 필요합니다.");
		}

		// ✅ 이메일 기반으로 저장
		String writerEmail = loginUser.getEmail();
		String writerName = loginUser.getNickname();
		
		articleMap.put("writerEmail", writerEmail);
		articleMap.put("writerName", writerName);
		
//		Member memberVO = (Member) session.getAttribute("member");
//		String memId = memberVO.getMemId();
//		articleMap.put("memId", memId);

		// 부모글번호와 글그룹번호를 articleMap에 저장한다.
//		articleMap.put("parentNO", parentNO);
//		articleMap.put("groupNO", groupNO);

		String message = null;
		ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			int articleNO = articleService.addReplyArticle(articleMap);

			message = "<script>";
			message += " alert('답글을 추가했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/viewArticle.do?articleNO="
					+ articleMap.get("articleNO") + "';";
			message += " </script>";
		} catch (Exception e) {
			message = "<script>";
			message += " alert('오류발생했습니다. 다시 작성해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/article/articleForm.do'";
			message += " </script>";
			e.printStackTrace();
		} finally {
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		}
		return resEnt;
	}

	@PostMapping("/article/modArticleJsonCK.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> modArticleJsonCE(@RequestPart("article") String articleJson)
			throws Exception {
		// JSON → Map 변환
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> articleMap = mapper.readValue(articleJson, new TypeReference<Map<String, Object>>() {
		});

		String articleNO = (String) articleMap.get("articleNO");
		ResponseEntity<Map<String, Object>> resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			articleService.modArticle(articleMap);

			Map<String, Object> result = new HashMap<>();
			result.put("status", "ok");
			result.put("message", "글을 수정했습니다.");
			result.put("redirectUrl", "/article/viewArticle.do?articleNO=" + articleMap.get("articleNO"));

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> result = new HashMap<>();
			result.put("status", "ERROR");
			result.put("message", "오류 발생. 다시 수정해 주세요.");
			result.put("redirectUrl", "/article/viewArticle.do?articleNO=" + articleMap.get("articleNO"));
			resEnt = new ResponseEntity(result, responseHeaders, HttpStatus.CREATED);
		}

		return resEnt;
	}

	@RequestMapping(value = "/article/articleForm.do", method = { RequestMethod.GET, RequestMethod.POST })
	private ModelAndView articleForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping(value = "/article/replyForm", method = { RequestMethod.GET, RequestMethod.POST })
	private ModelAndView replyForm(@RequestParam(value = "parentNO", required = false) String parentNO,
									@RequestParam(value = "groupNO", required = false) String groupNO, HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		String _parentNO = (String) session.getAttribute("parentNO");
		String _groupNO = (String) session.getAttribute("groupNO");

		parentNO = (parentNO != null) ? parentNO : _parentNO;
		groupNO = (groupNO != null) ? groupNO : _groupNO;

		if (parentNO != null) {
			mav.addObject("parentNO", parentNO);
		}

		if (groupNO != null) {
			mav.addObject("groupNO", groupNO);
		}
		mav.setViewName(viewName);
		return mav;
	}

	@PostMapping("/article/imageUpload.do")
	public void articleImageUpload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("upload") MultipartFile upload) throws Exception {

		System.out.println("이미지업로드 호출됨: " + upload.getOriginalFilename());
		File uploadDir = new File(ARTICLE_CK_IMAGE_REPO + "\\" + "temp");
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

}
