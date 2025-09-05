package com.board01.comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board01.comment.service.CommentService;
import com.board01.comment.vo.CommentVO;
import com.board01.member.vo.MemberVO;
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
	@PostMapping("/addNewComment.do")
	@Override
	public ResponseEntity<String> addNewComment(@RequestBody CommentVO commentVO,
								HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO)session.getAttribute("member");
		String replyID = member.getId();  //로그인  사용자 아이디를 얻음
		commentVO.setReplyID(replyID);
		CommentVO newCommentVO = commentService.addNewComment(commentVO);

		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(newCommentVO);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}

	@PostMapping("/modComment.do")
	@Override
	public ResponseEntity<String> ModComment(@RequestBody CommentVO commentVO, 
			                                 HttpServletRequest request,
											HttpServletResponse response) throws Exception {
		
		CommentVO modCommentVO = commentService.modComment(commentVO);
		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(modCommentVO);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}

	@PostMapping("/removeComment.do")
	@Override
	public ResponseEntity<String> removeComment(@RequestBody CommentVO commentVO, 
			                                    HttpServletRequest request, 
			                                    HttpServletResponse response)  throws Exception {
		commentService.removeComment(commentVO);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity("REMOVE_SUCCESS", responseHeaders, HttpStatus.CREATED);
		
	}

	@PostMapping("/addReplyComment.do")
	@Override
	public ResponseEntity<String> addReplyComment(@RequestBody CommentVO commentVO, 
			                                      HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
		System.out.println(commentVO.getArticleNO());
		
		HttpSession session = request.getSession();
		MemberVO member = (MemberVO)session.getAttribute("member");
		String replyID = member.getId();  //로그인  사용자 아이디를 얻음
		commentVO.setReplyID(replyID);
		CommentVO newCommentVO = commentService.addReplyComment(commentVO);

		ObjectMapper objectMapper = new ObjectMapper();  //객체를 JSON으로 변경해주는 클래스 객체
		String commentjson = objectMapper.writeValueAsString(newCommentVO);
		System.out.println(commentjson);
	    
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity(commentjson, responseHeaders, HttpStatus.CREATED);
	}
}

