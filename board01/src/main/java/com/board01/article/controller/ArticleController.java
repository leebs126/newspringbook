package com.board01.article.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ArticleController {
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception;
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest,HttpServletResponse response) throws Exception;
	public ResponseEntity addReplyArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception; 
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO,
												@RequestParam(value="removeCompleted", required=false) String removeCompleted,
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
	public ResponseEntity  removeArticle(@RequestParam("articleNO") int articleNO,
                              			HttpServletRequest request, 
                              			HttpServletResponse response) throws Exception;
	public void removeModImage(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	
	

}
