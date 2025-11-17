package com.springboot.ckb.article.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.springboot.ckb.article.domain.Article;
import com.springboot.ckb.article.domain.Image;


@Mapper
public interface ArticleRepository {
	public List<Article> selectAllArticlesList(Map<String, Integer> pagingMap) throws DataAccessException;
	public int selectTotalArticles() throws DataAccessException;
	
	public int insertNewArticle(Map articleMap) throws DataAccessException;
	public void insertNewImage(Image image) throws DataAccessException;
	
	public void insertReplyArticle(Map articleMap) throws DataAccessException;
	
	public Article selectArticleByNO(int articleNO) throws DataAccessException;
	public void updateArticle(Map articleMap) throws DataAccessException;
//	public void updateImageFile(ImageVO imageVO) throws DataAccessException;
	public void updateImageFile(Map articleMap) throws DataAccessException;
	public void deleteArticle(int articleNO) throws DataAccessException;
	public List selectImageFileList(int articleNO) throws DataAccessException;
	
	public void deleteModImage(Image image) throws DataAccessException;
	
	public void insertModNewImage(Image image) throws DataAccessException;

	public int selectNewArticleNO() throws DataAccessException;
	public int selectNewGroupNO() throws DataAccessException;
	
	public void updateViewCounts(int articleNO) throws DataAccessException;
	
	public int selectNewImageFileNO() throws DataAccessException;
	
}
