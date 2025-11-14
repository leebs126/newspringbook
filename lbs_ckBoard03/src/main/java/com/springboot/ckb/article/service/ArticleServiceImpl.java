package com.springboot.ckb.article.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.ckb.article.repository.ArticleRepository;
import com.springboot.ckb.article.domain.Article;
import com.springboot.ckb.comment.repository.CommentRepository;
import com.springboot.ckb.comment.domain.Comment;


@Service
@Primary
@Transactional(propagation = Propagation.REQUIRED)
public class ArticleServiceImpl  implements ArticleService{
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	public Map<String, Object> listArticles(Map  pagingMap) throws Exception{
		Map<String, Object> articlesMap = new HashMap<>();
		List<Article> articlesList = articleRepository.selectAllArticlesList(pagingMap);
		int totalArticles = articleRepository.selectTotalArticles();
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totalArticles", totalArticles);
		return articlesMap;
	}

	@Override
	public int addNewArticle(Map articleMap) throws Exception{
		int _articleNO = articleRepository.selectNewArticleNO();
		int _groupNO = articleRepository.selectNewGroupNO();
		articleMap.put("articleNO", _articleNO);
		articleMap.put("groupNO", _groupNO);
		articleRepository.insertNewArticle(articleMap);
		return _articleNO;
	}
	
	@Override
	public int addReplyArticle(Map articleMap) throws Exception{
		int _articleNO = articleRepository.selectNewArticleNO();
		articleMap.put("articleNO", _articleNO);
		articleRepository.insertReplyArticle(articleMap);
		return _articleNO;
	}
	
	@Override
	public Map viewArticle(Map viewMap) throws Exception {
		Map<String, Object> articleMap = new HashMap<String, Object>();
		
		int articleNO = (Integer)viewMap.get("articleNO");
		
		//조회수를 갱신하기 전 먼저 글번호에 해당되는 글정보를 조회한 후, 로그인 아이디와 글쓴이 아이디를 비교한다.
		Article article = articleRepository.selectArticleByNO(articleNO); 
		
		articleMap.put("article", article);
		
		//해당 글의 댓글 조회
		Map<String, Integer> pagingMap = new  HashMap<String, Integer>();
		pagingMap.put("articleNO", articleNO);
		pagingMap.put("pageNum", 1);  
		pagingMap.put("section", 1);
		List<Comment> commentsList = commentRepository.selectAllCommentsList(pagingMap); 
		Map<Integer, List<Comment>> grouped = commentsList.stream()
			    .collect(Collectors.groupingBy(
			        Comment::getCGroupNO,
			        () -> new TreeMap<>(Comparator.reverseOrder()), // 키 내림차순 정렬된 TreeMap 사용
			        Collectors.toList()
			    ));

		articleMap.put("commentsGroupMap", grouped);
		return articleMap;
	}
	
	
	
	@Override
	public void modArticle(Map articleMap) throws Exception {
		articleRepository.updateArticle(articleMap);
	}
	
	@Override
	public void removeArticle(int articleNO) throws Exception {
		articleRepository.deleteArticle(articleNO);
	}


}
