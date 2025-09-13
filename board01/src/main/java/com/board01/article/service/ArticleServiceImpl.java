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
	
	public Map<String, Object> listArticles(Map  pagingMap) throws Exception{
		Map<String, Object> articlesMap = new HashMap<>();
		List<ArticleVO> articlesList = articleRepository.selectAllArticlesList(pagingMap);
		int totalArticles = articleRepository.selectTotalArticles();
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totalArticles", totalArticles);
		return articlesMap;
		
	}

	 //다중 이미지 글 추가하기
	@Override
	public int addNewArticle(Map articleMap) throws Exception{
		int _articleNO = articleRepository.selectNewArticleNO();
		int _groupNO = articleRepository.selectNewGroupNO();
		articleMap.put("articleNO", _articleNO);
		articleMap.put("groupNO", _groupNO);
		
		articleRepository.insertNewArticle(articleMap);
		List<ImageVO> imageFileList = (List<ImageVO>)articleMap.get("imageFileList");
		if(imageFileList!= null) {
			for(ImageVO imageVO : imageFileList) {
				int imageFileNO = articleRepository.selectNewImageFileNO();
				imageVO.setImageFileNO(imageFileNO);
				imageVO.setArticleNO(_articleNO);
				articleRepository.insertNewImage(imageVO);
			}	
		}
		return _articleNO;
	}
	
	//다중 이미지 답글추가하기
		@Override
		public int addReplyArticle(Map articleMap) throws Exception{
			
			int _articleNO = articleRepository.selectNewArticleNO();
			articleMap.put("articleNO", _articleNO);
			articleRepository.insertReplyArticle(articleMap);
			
			List<ImageVO> imageFileList = (List<ImageVO>)articleMap.get("imageFileList");
			if(imageFileList!= null) {
				for(ImageVO imageVO : imageFileList) {
					int imageFileNO = articleRepository.selectNewImageFileNO();
					imageVO.setImageFileNO(imageFileNO);
					imageVO.setArticleNO(_articleNO);
					articleRepository.insertNewImage(imageVO);
				}	
			}
			
			return _articleNO;
		}
	
		//다중 파일 보이기
	@Override
	public Map viewArticle(Map viewMap) throws Exception {
		Map articleMap = new HashMap();
		
		int articleNO = (Integer)viewMap.get("articleNO");
		String memId = (String) viewMap.get("memId");
		
		//조회수를 갱신하기 전 먼저 글번호에 해당되는 글정보를 조회한 후, 로그인 아이디와 글쓴이 아이디를 비교한다.
		ArticleVO articleVO = articleRepository.selectArticle(articleNO); 
		String writerId = articleVO.getMemId();
		
//		if(id == null || !(id.equals(writerId))) {  //로그인 아이디와 글쓴이 아이디가 같지 않으면 조회수를 1증가 시킴
//			articleRepository.updateViewCounts(articleNO);
//			articleVO = articleRepository.selectArticle(articleNO);
//		}
		
		List<ImageVO> imageFileList = articleRepository.selectImageFileList(articleNO);
		articleMap.put("article", articleVO);
		articleMap.put("imageFileList", imageFileList);
		
		//해당 글의 댓글 조회
		List<CommentVO> commentsList = null;
//		List<CommentVO> commentsList = commentRepository.selectAllCommentsList(articleNO);
		articleMap.put("commentsList", commentsList);
		return articleMap;
	}
	
	
	
	@Override
	public void modArticle(Map articleMap) throws Exception {
		articleRepository.updateArticle(articleMap);
		
		List<ImageVO> imageFileList = (List<ImageVO>)articleMap.get("imageFileList");
		List<ImageVO> modAddImageFileList = (List<ImageVO>)articleMap.get("modAddImageFileList");
		
		@SuppressWarnings("unchecked")
		List<String> oldFileNamesList = (List<String>) articleMap.get("oldFileNames");
	    List<String> newFileNamesList = (List<String>) articleMap.get("newFileNames");
	    
	    

		if(imageFileList != null && imageFileList.size() != 0) {
			int added_img_counts = newFileNamesList.size();
			int pre_img_counts = oldFileNamesList.size();

			if(pre_img_counts < added_img_counts) {  
				articleRepository.updateImageFile(articleMap);     //기존 이미지도 수정하고 새 이미지도 추가한 경우  
				for(ImageVO imageVO : modAddImageFileList) {
					int imageFileNO = articleRepository.selectNewImageFileNO();
					imageVO.setImageFileNO(imageFileNO);
					articleRepository.insertModNewImage(imageVO);
				}
			}else {
//				articleRepository.updateImageFile(imageFileList);  //기존의 이미지를 수정만 한 경우
				articleRepository.updateImageFile(articleMap);  //기존의 이미지를 수정만 한 경우
			}
		}else if(modAddImageFileList != null && modAddImageFileList.size() != 0) {  //새 이미지를 추가한 경우
			
			for(ImageVO imageVO : modAddImageFileList) {
				int imageFileNO = articleRepository.selectNewImageFileNO();
				imageVO.setImageFileNO(imageFileNO);
				articleRepository.insertModNewImage(imageVO);
			}
//			articleMap.put("modAddImageFileList", modAddImageFileList);
			
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
