package com.ckboard01.article.service;

import java.util.Map;

import com.ckboard01.article.vo.ImageVO;


public interface ArticleService {
	public Map listArticles(Map<String, Integer> pagingMap) throws Exception;
	public int addNewArticle(Map articleMap) throws Exception;
	public int addReplyArticle(Map articleMap) throws Exception;
	public Map viewArticle(Map viewMap) throws Exception;
	public void modArticle(Map articleMap) throws Exception;
	public void removeArticle(int articleNO) throws Exception;
	
}
