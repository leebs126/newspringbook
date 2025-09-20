package com.board02.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.board02.comment.repository.CommentRepository;
import com.board02.comment.vo.CommentVO;

@Service
@Primary
public class CommentServiceImpl implements CommentService {
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public CommentVO addNewComment(CommentVO commentVO) throws Exception {
		int commentNO = commentRepository.selectNewCommentNO();
		int cGroupNO = commentRepository.selectNewCGroupNO();
		commentVO.setCommentNO(commentNO);
		commentVO.setCGroupNO(cGroupNO);
		commentRepository.insertNewComment(commentVO);
		CommentVO newCommentVO =commentRepository.selectCommentById(commentNO);
		return newCommentVO;
	}

	@Override
	public CommentVO modComment(CommentVO commentVO) throws Exception {
		commentRepository.updateComment(commentVO);
		int commentNO = commentVO.getCommentNO();
		CommentVO modCommentVO =commentRepository.selectCommentById(commentNO);
		return modCommentVO;
	}

	@Override
	public void removeComment(CommentVO commentVO) throws Exception {
		commentRepository.deleteComment(commentVO);
		
	}

	@Override
	public CommentVO addReplyComment(CommentVO commentVO) throws Exception {
		int commentNO = commentRepository.selectNewCommentNO();
		commentVO.setCommentNO(commentNO);
		commentRepository.insertNewComment(commentVO);
		CommentVO newCommentVO =commentRepository.selectCommentById(commentNO);
		return newCommentVO;

	}
}
