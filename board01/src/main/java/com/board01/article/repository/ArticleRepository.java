package com.board01.article.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.board01.article.vo.ArticleVO;
import com.board01.article.vo.ImageVO;

@Mapper
public interface ArticleRepository {
	public List<ArticleVO> selectAllArticlesList(Map<String, Integer> pagingMap) throws DataAccessException;
	public int selectTotalArticles() throws DataAccessException;
	
	public int insertNewArticle(Map articleMap) throws DataAccessException;
	public void insertNewImage(ImageVO imageVO) throws DataAccessException;
	
	public void insertReplyArticle(Map articleMap) throws DataAccessException;
	
	public ArticleVO selectArticle(int articleNO) throws DataAccessException;
	public void updateArticle(Map articleMap) throws DataAccessException;
//	public void updateImageFile(ImageVO imageVO) throws DataAccessException;
	public void updateImageFile(Map articleMap) throws DataAccessException;
	public void deleteArticle(int articleNO) throws DataAccessException;
	public List selectImageFileList(int articleNO) throws DataAccessException;
	
	public void deleteModImage(ImageVO imageVO) throws DataAccessException;
	
	public void insertModNewImage(ImageVO imageVO) throws DataAccessException;

	public int selectNewArticleNO() throws DataAccessException;
	public int selectNewGroupNO() throws DataAccessException;
	
	public void updateViewCounts(int articleNO) throws DataAccessException;
	
	public int selectNewImageFileNO() throws DataAccessException;
	
}
