package com.board02.comment.vo;

import java.sql.Date;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component("commentVO")
public class CommentVO {
//	private int cGroupNO;
	
	private int commentNO; //댓글번호
	private String contents;  //댓글 내용
	private String replyId;  //댓글 작성자 아이디
	private Date creDate;  //댓글 생성 일자
	private int articleNO;  // 부모글번호
	private int level;    //레벨
	private int pCommentNO;  //부모댓글 번호
	
	public CommentVO() {
	}
	
	public CommentVO(int commentNO, String contents, String replyId, Date creDate, int articleNO) {
		this.commentNO = commentNO;
		this.contents = contents;
		this.replyId = replyId;
		this.creDate = creDate;
		this.articleNO = articleNO;
	}
	
	public CommentVO(int commentNO, String contents, String replyId, Date creDate, int articleNO, int level, int pCommentNO) {
		this.commentNO = commentNO;
		this.contents = contents;
		this.replyId = replyId;
		this.creDate = creDate;
		this.articleNO = articleNO;
		this.level = level;
		this.pCommentNO = pCommentNO;
	}

	

}
