package com.springboot.ckb.article.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse;

public interface ArticleController {
	public ModelAndView listArticles(@RequestParam(value="section", required=false) String _section, 
									@RequestParam(value="pageNum", required=false) String _pageNum, 
									HttpServletRequest request, HttpServletResponse response) throws Exception; 
	
	public ResponseEntity<Map<String, Object>> addNewArticle(MultipartHttpServletRequest multipartRequest,HttpServletResponse response) throws Exception;
	
	public ResponseEntity<Map<String, Object>> addReplyArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception; 
	
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO,
									@RequestParam(value="removeCompleted", required=false) String removeCompleted,
									HttpServletRequest request, 
									HttpServletResponse response) throws Exception;
	
	public ResponseEntity<Map<String, Object>>  removeArticle(@RequestParam("articleNO") int articleNO,
                              			HttpServletRequest request, 
                              			HttpServletResponse response) throws Exception;
	

	@PostMapping("/article/modArticleJsonCE.do")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> modArticleJsonCE(@RequestPart("article") String articleJson) throws Exception;
		
	
	

}
