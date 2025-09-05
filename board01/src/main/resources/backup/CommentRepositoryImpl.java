package backup;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.board01.comment.repository.CommentRepository;
import com.board01.comment.vo.CommentVO;

@Repository("commentDAO")
public class CommentRepositoryImpl implements CommentRepository {
	@Autowired
	private SqlSession sqlSession;

	// 댓글 조회
	@Override
	public List<CommentVO> selectAllCommentsList(int articleNO) throws DataAccessException {
		List<CommentVO> commentsList = sqlSession.selectList("mapper.comment.selectAllCommentsList", articleNO);
		return commentsList;
	}

	@Override
	public void insertNewComment(CommentVO commentVO) throws DataAccessException {
		sqlSession.insert("mapper.comment.insertNewComment", commentVO);
	}
	
	@Override
	public int selectNewCommentNO() throws DataAccessException {
			int maxCommentNO = sqlSession.selectOne("mapper.comment.selectNewCommentNO");
			return maxCommentNO;
	}

	@Override
	public CommentVO selectComment(int commentNO) throws DataAccessException {
		CommentVO commentVO = sqlSession.selectOne("mapper.comment.selectComment", commentNO);
		return commentVO;
	}

	@Override
	public void updateComment(CommentVO commentVO) throws DataAccessException {
		sqlSession.update("mapper.comment.updateComment", commentVO);
		
	}

	@Override
	public void deleteComment(CommentVO commentVO) throws DataAccessException {
		sqlSession.delete("mapper.comment.deleteComment", commentVO);
		
	}
}