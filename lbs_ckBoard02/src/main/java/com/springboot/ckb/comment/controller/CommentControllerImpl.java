package com.springboot.ckb.comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ckb.comment.service.CommentService;
import com.springboot.ckb.comment.domain.Comment;
import com.springboot.ckb.member.domain.Member;
import com.springboot.ckb.member.dto.SessionUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@RestController
@Primary
@RequestMapping("/comm/*")
public class CommentControllerImpl implements CommentController {
	@Autowired
	private CommentService commentService;
	
	//새 댓글 추가 기능
	@PostMapping("/addNewComment")
	@Override
	public ResponseEntity<String> addNewComment(@RequestBody Comment comment,
								HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		SessionUser loginUser = (SessionUser) session.getAttribute("loginUser");
		String memId = null;
		if (loginUser == null) {
		    throw new RuntimeException("로그인이 필요합니다.");
		}
		
		String replyId = loginUser.getMemId();  //로그인  사용자 아이디를 얻음
		comment.setReplyId(replyId);
		Comment newComment = commentService.addNewComment(comment);

		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(newComment);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}

	@PostMapping("/modComment.do")
	@Override
	public ResponseEntity<String> ModComment(@RequestBody Comment comment, 
			                                 HttpServletRequest request,
											HttpServletResponse response) throws Exception {
		
		Comment modComment = commentService.modComment(comment);
		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(modComment);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}

	@PostMapping("/removeComment")
	@Override
	public ResponseEntity<String> removeComment(@RequestBody Comment comment, 
			                                    HttpServletRequest request, 
			                                    HttpServletResponse response)  throws Exception {
		commentService.removeComment(comment);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity("REMOVE_SUCCESS", responseHeaders, HttpStatus.CREATED);
		
	}

	@PostMapping("/addReplyComment")
	@Override
	public ResponseEntity<String> addReplyComment(@RequestBody Comment comment, 
			                                      HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		SessionUser loginUser = (SessionUser) session.getAttribute("loginUser");
		String memId = null;
		if (loginUser == null) {
		    throw new RuntimeException("로그인이 필요합니다.");
		}
		
		String replyId = loginUser.getMemId();  //로그인  사용자 아이디를 얻음
		comment.setReplyId(replyId);
		Comment newComment = commentService.addReplyComment(comment);

		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(newComment);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}
}

