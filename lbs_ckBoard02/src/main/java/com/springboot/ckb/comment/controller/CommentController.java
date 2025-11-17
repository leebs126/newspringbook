package com.springboot.ckb.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.springboot.ckb.comment.domain.Comment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CommentController {
	public ResponseEntity<String> addNewComment(@RequestBody Comment comment, 
									            HttpServletRequest request, 
									            HttpServletResponse response) throws Exception;

	public ResponseEntity<String> ModComment(@RequestBody Comment comment, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
	
	public ResponseEntity<String> removeComment(@RequestBody Comment comment, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
	
	public ResponseEntity<String> addReplyComment(@RequestBody Comment comment, 
												HttpServletRequest request, 
												HttpServletResponse response) throws Exception;
}
