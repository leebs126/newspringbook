package com.springboot.ckb.comment.domain;

import java.sql.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Component("comment")
public class Comment {
    @JsonProperty("cGroupNO")
    private int cGroupNO;

    @JsonProperty("commentNO")
    private int commentNO;

    private String contents;
    private String replyId;

    @JsonProperty("articleNO")
    private int articleNO;

    private int level;

    @JsonProperty("pCommentNO")
    private int pCommentNO;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date updatedAt;

    
	

}
