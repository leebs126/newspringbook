package com.board02.comment.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import com.board02.comment.vo.CommentVO;


@Mapper
public interface CommentRepository {
	// 댓글 기능
	public List<CommentVO> selectAllCommentsList(int articleNO) throws DataAccessException;

	public void insertNewComment(CommentVO commentVO) throws DataAccessException;

	public CommentVO selectComment(int commentNO) throws DataAccessException;

	public int selectNewCommentNO() throws DataAccessException;

	// 댓글 수정
	public void updateComment(CommentVO commentVO) throws DataAccessException;

	public void deleteComment(CommentVO commentVO) throws DataAccessException;
}
