package com.springboot.ckb.comment.service;

import com.springboot.ckb.comment.domain.Comment;

public interface CommentService {
	public Comment  addNewComment(Comment comment) throws Exception;
	public Comment  modComment(Comment comment) throws Exception;
	public void  removeComment(Comment comment) throws Exception;
	public Comment  addReplyComment(Comment comment) throws Exception;  //대댓글 추가
}
