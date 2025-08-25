package com.bookshop01.goods.vo;

import java.sql.Date;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("imageFileVO")
public class ImageFileVO {
	private int goodsId;
	private int imageId;
	private String fileName;
	private String fileType;
	private String regId;
	


}
