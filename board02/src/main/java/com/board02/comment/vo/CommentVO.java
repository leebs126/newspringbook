package com.board02.comment.vo;

import java.sql.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Component("commentVO")
public class CommentVO {
    @JsonProperty("cGroupNO")
    private int cGroupNO;

    @JsonProperty("commentNO")
    private int commentNO;

    private String contents;
    private String replyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date creDate;

    @JsonProperty("articleNO")
    private int articleNO;

    private int level;

    @JsonProperty("pCommentNO")
    private int pCommentNO;

	
//	public CommentVO() {
//	}
	
//	public CommentVO(int commentNO, String contents, String replyId, Date creDate, int articleNO) {
//		this.commentNO = commentNO;
//		this.contents = contents;
//		this.replyId = replyId;
//		this.creDate = creDate;
//		this.articleNO = articleNO;
//	}
//	
//	public CommentVO(int commentNO, String contents, String replyId, Date creDate, int articleNO, int level, int pCommentNO) {
//		this.commentNO = commentNO;
//		this.contents = contents;
//		this.replyId = replyId;
//		this.creDate = creDate;
//		this.articleNO = articleNO;
//		this.level = level;
//		this.pCommentNO = pCommentNO;
//	}

	

}
