package com.springboot.ckb.article.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component("image")
public class Image {
	private int imageFileNO;
	private String imageFileName;
	private Date regDate;
	private int articleNO;
	
	public String getImageFileName() 
	{
		try {
			if(imageFileName!= null && imageFileName.length()!=0) {
				imageFileName = URLDecoder.decode(imageFileName,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		try {
			if(imageFileName!= null && imageFileName.length()!=0) {
				this.imageFileName = URLEncoder.encode(imageFileName,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
