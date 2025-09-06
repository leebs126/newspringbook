package backup;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.board01.article.repository.ArticleRepository;
import com.board01.article.vo.ArticleVO;
import com.board01.article.vo.ImageVO;




@Repository("articleRepository")
public class ArticleRepositoryImpl implements ArticleRepository {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<ArticleVO> selectAllArticlesList(Map<String, Integer> pagingMap) throws DataAccessException {
		List<ArticleVO> articlesList  = sqlSession.selectList("mapper.article.selectAllArticlesList", pagingMap);
		return articlesList;
	}
	

	@Override
	public int selectTotalArticles() throws DataAccessException {
		int totArticles = sqlSession.selectOne("mapper.article.selectTotArticles");
		return totArticles;
	}

	
	@Override
	public int insertNewArticle(Map articleMap) throws DataAccessException {
		int groupNO  = selectNewGroupNO();
		articleMap.put("groupNO", groupNO);
		int articleNO = selectNewArticleNO();
		articleMap.put("articleNO", articleNO);
		sqlSession.insert("mapper.article.insertNewArticle",articleMap);
		return articleNO;
	}
	
	@Override
	public int insertReplyArticle(Map articleMap) throws DataAccessException {
		int articleNO = selectNewArticleNO();
		articleMap.put("articleNO", articleNO);
		sqlSession.insert("mapper.article.insertReplyArticle",articleMap);
		return articleNO;
	}
	
    
	//다중 파일 업로드
	@Override
	public void insertNewImage(Map articleMap) throws DataAccessException {
		List<ImageVO> imageFileList = (ArrayList)articleMap.get("imageFileList");
		int articleNO = (Integer)articleMap.get("articleNO");
		int imageFileNO = selectNewImageFileNO();
		
		if(imageFileList != null && imageFileList.size() != 0) {
			for(ImageVO imageVO : imageFileList){
				imageVO.setImageFileNO(++imageFileNO);
				imageVO.setArticleNO(articleNO);
			}
			sqlSession.insert("mapper.article.insertNewImage",imageFileList);
		}
		
	}
	
	@Override
	public ArticleVO selectArticle(int articleNO) throws DataAccessException {
		return sqlSession.selectOne("mapper.article.selectArticle", articleNO);
	}

	@Override
	public void updateArticle(Map articleMap) throws DataAccessException {
		sqlSession.update("mapper.article.updateArticle", articleMap);
	}
	
	@Override
	public void updateImageFile(Map articleMap) throws DataAccessException {
		
		List<ImageVO> imageFileList = (ArrayList)articleMap.get("imageFileList");
		int articleNO = Integer.parseInt((String)articleMap.get("articleNO"));
		
		for(int i = imageFileList.size()-1; i >= 0; i--){
			ImageVO imageVO = imageFileList.get(i);
			String imageFileName = imageVO.getImageFileName();
			if(imageFileName == null) {   //기존에 이미지를 수정하지 않는 경우 파일명이 null 이므로  수정할 필요가 없다.
				imageFileList.remove(i);
			}else {
				imageVO.setArticleNO(articleNO);
			}
		}
		
		if(imageFileList != null && imageFileList.size() != 0) {
			sqlSession.update("mapper.article.updateImageFile", imageFileList);
		}
		
	}

	
	

	@Override
	public void deleteArticle(int articleNO) throws DataAccessException {
		sqlSession.delete("mapper.article.deleteArticle", articleNO);
		
	}
	
	@Override
	public List selectImageFileList(int articleNO) throws DataAccessException {
		List<ImageVO> imageFileList = null;
		imageFileList = sqlSession.selectList("mapper.article.selectImageFileList",articleNO);
		return imageFileList;
	}
	
	private int selectNewArticleNO() throws DataAccessException {
		return sqlSession.selectOne("mapper.article.selectNewArticleNO");
	}
	
	private int selectNewImageFileNO() throws DataAccessException {
		return sqlSession.selectOne("mapper.article.selectNewImageFileNO");
	}


	@Override
	public void deleteModImage(ImageVO imageVO) throws DataAccessException {
		sqlSession.delete("mapper.article.deleteModImage", imageVO );
		
	}


	@Override
	public void insertModNewImage(Map articleMap) throws DataAccessException {
		List<ImageVO> modAddimageFileList = (ArrayList<ImageVO>)articleMap.get("modAddimageFileList");
		int articleNO = Integer.parseInt((String)articleMap.get("articleNO"));
		
		int imageFileNO = selectNewImageFileNO();
		
		for(ImageVO imageVO : modAddimageFileList){
			imageVO.setArticleNO(articleNO);
			imageVO.setImageFileNO(++imageFileNO);
		}
		
//		sqlSession.delete("mapper.board.insertModNewImage", modAddimageFileList );
		sqlSession.insert("mapper.article.insertModNewImage", modAddimageFileList );
		
	}
	

	@Override
	public int selectNewGroupNO() throws DataAccessException {
			int maxGroupNO = sqlSession.selectOne("mapper.article.selectNewGroupNO");
			return maxGroupNO;
	}


	@Override
	public void updateViewCounts(int articleNO) throws DataAccessException {
		sqlSession.update("mapper.article.updateViewCounts", articleNO);
	}

		
}



