package com.board01.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.board01.comment.vo.CommentVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CommentController {
	public ResponseEntity<String> addNewComment(@RequestBody CommentVO commentVO, 
									            HttpServletRequest request, 
									            HttpServletResponse response) throws Exception;

	public ResponseEntity<String> ModComment(@RequestBody CommentVO commentVO, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
	
	public ResponseEntity<String> removeComment(@RequestBody CommentVO commentVO, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
	
	public ResponseEntity<String> addReplyComment(@RequestBody CommentVO commentVO, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
}
