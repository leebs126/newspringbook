package com.springboot.ckb.article.service;

import java.util.Map;


public interface ArticleService {
	public Map listArticles(Map<String, Integer> pagingMap) throws Exception;
	public int addNewArticle(Map articleMap) throws Exception;
	public int addReplyArticle(Map articleMap) throws Exception;
	public Map viewArticle(Map viewMap) throws Exception;
	public void modArticle(Map articleMap) throws Exception;
	public void removeArticle(int articleNO) throws Exception;
	
}
