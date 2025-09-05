package com.board01.article.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.board01.article.repository.ArticleRepository;
import com.board01.article.vo.ArticleVO;
import com.board01.article.vo.ImageVO;
import com.board01.comment.repository.CommentRepository;
import com.board01.comment.vo.CommentVO;

@Service
@Primary
@Transactional(propagation = Propagation.REQUIRED)
public class ArticleServiceImpl  implements ArticleService{
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	public Map listArticles(Map  pagingMap) throws Exception{
		Map articlesMap = new HashMap();
		List<ArticleVO> articlesList = articleRepository.selectAllArticlesList(pagingMap);
		int totArticles = articleRepository.selectTotArticles();
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totArticles", totArticles);
		//articlesMap.put("totArticles", 170);
		return articlesMap;
		
	}

	 //다중 이미지 글 추가하기
	@Override
	public int addNewArticle(Map articleMap) throws Exception{
		int articleNO = articleRepository.insertNewArticle(articleMap);
		articleMap.put("articleNO", articleNO);
		articleRepository.insertNewImage(articleMap);
		return articleNO;
	}
	
	//다중 이미지 답글추가하기
		@Override
		public int addReplyArticle(Map articleMap) throws Exception{
			int articleNO = articleRepository.insertReplyArticle(articleMap);
			articleMap.put("articleNO", articleNO);
			articleRepository.insertNewImage(articleMap);
			return articleNO;
		}
	
		//다중 파일 보이기
	@Override
//	public Map viewArticle(int articleNO) throws Exception {
	public Map viewArticle(Map viewMap) throws Exception {
		Map articleMap = new HashMap();
		
		int articleNO = (Integer)viewMap.get("articleNO");
		String id = (String) viewMap.get("id");
		
		//조회수를 갱신하기 전 먼저 글번호에 해당되는 글정보를 조회한 후, 로그인 아이디와 글쓴이 아이디를 비교한다.
		ArticleVO articleVO = articleRepository.selectArticle(articleNO); 
		String writerId = articleVO.getId();
		
		if(id == null || !(id.equals(writerId))) {  //로그인 아이디와 글쓴이 아이디가 같지 않으면 조회수를 1증가 시킴
			articleRepository.updateViewCounts(articleNO);
			articleVO = articleRepository.selectArticle(articleNO);
		}
		
		List<ImageVO> imageFileList = articleRepository.selectImageFileList(articleNO);
		articleMap.put("article", articleVO);
		articleMap.put("imageFileList", imageFileList);
		
		//해당 글의 댓글 조회
		List<CommentVO> commentsList = commentRepository.selectAllCommentsList(articleNO);
		articleMap.put("commentsList", commentsList);
		return articleMap;
	}
	
	
	
	@Override
	public void modArticle(Map articleMap) throws Exception {
		articleRepository.updateArticle(articleMap);
		
		List<ImageVO> imageFileList = (List<ImageVO>)articleMap.get("imageFileList");
		List<ImageVO> modAddimageFileList = (List<ImageVO>)articleMap.get("modAddimageFileList");

		if(imageFileList != null && imageFileList.size() != 0) {
			int added_img_num = Integer.parseInt((String)articleMap.get("added_img_num"));
			int pre_img_num = Integer.parseInt((String)articleMap.get("pre_img_num"));

			if(pre_img_num < added_img_num) {  
				articleRepository.updateImageFile(articleMap);     //기존 이미지도 수정하고 새 이미지도 추가한 경우  
				articleRepository.insertModNewImage(articleMap);
			}else {
				articleRepository.updateImageFile(articleMap);  //기존의 이미지를 수정만 한 경우
			}
		}else if(modAddimageFileList != null && modAddimageFileList.size() != 0) {  //새 이미지를 추가한 경우
			articleRepository.insertModNewImage(articleMap);
		}
	}
	
	@Override
	public void removeArticle(int articleNO) throws Exception {
		articleRepository.deleteArticle(articleNO);
	}


	@Override
	public void removeModImage(ImageVO imageVO) throws Exception {
		articleRepository.deleteModImage(imageVO);
	}
	
	
}
