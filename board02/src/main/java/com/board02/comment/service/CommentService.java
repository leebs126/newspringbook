package com.board02.comment.service;

import com.board02.comment.vo.CommentVO;

public interface CommentService {
	public CommentVO  addNewComment(CommentVO commentVO) throws Exception;
	public CommentVO  modComment(CommentVO commentVO) throws Exception;
	public void  removeComment(CommentVO commentVO) throws Exception;
	public CommentVO  addReplyComment(CommentVO commentVO) throws Exception;  //대댓글 추가
}
