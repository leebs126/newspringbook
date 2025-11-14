package com.springboot.ckb.comment.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.springboot.ckb.comment.domain.Comment;


@Mapper
public interface CommentRepository {
	// 댓글 기능
	public List<Comment> selectAllCommentsList(Map<String, Integer> pagingMap) throws DataAccessException;

	public void insertNewComment(Comment comment) throws DataAccessException;

	public Comment selectCommentById(int commentNO) throws DataAccessException;

	public int selectNewCommentNO() throws DataAccessException;

	// 댓글 수정
	public void updateComment(Comment comment) throws DataAccessException;

	public void deleteComment(Comment comment) throws DataAccessException;

	//댓글 그룹번호
	public int selectNewCGroupNO() throws DataAccessException;
}
