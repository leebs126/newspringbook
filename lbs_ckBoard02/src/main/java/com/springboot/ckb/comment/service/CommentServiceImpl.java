package com.springboot.ckb.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.springboot.ckb.comment.repository.CommentRepository;
import com.springboot.ckb.comment.domain.Comment;


@Service
@Primary
public class CommentServiceImpl implements CommentService {
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public Comment addNewComment(Comment comment) throws Exception {
		int commentNO = commentRepository.selectNewCommentNO();
		int cGroupNO = commentRepository.selectNewCGroupNO();
		comment.setCommentNO(commentNO);
		comment.setCGroupNO(cGroupNO);
		commentRepository.insertNewComment(comment);
		Comment newComment =commentRepository.selectCommentById(commentNO);
		return newComment;
	}

	@Override
	public Comment modComment(Comment comment) throws Exception {
		commentRepository.updateComment(comment);
		int commentNO = comment.getCommentNO();
		Comment modComment = commentRepository.selectCommentById(commentNO);
		return modComment;
	}

	@Override
	public void removeComment(Comment comment) throws Exception {
		commentRepository.deleteComment(comment);
		
	}

	@Override
	public Comment addReplyComment(Comment comment) throws Exception {
		int commentNO = commentRepository.selectNewCommentNO();
		comment.setCommentNO(commentNO);
		commentRepository.insertNewComment(comment);
		Comment newComment =commentRepository.selectCommentById(commentNO);
		return newComment;

	}
}
