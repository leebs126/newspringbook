package com.bookshop01.goods.vo;

import java.sql.Date;

import org.springframework.stereotype.Component;


import lombok.Data;


@Data
@Component("goodsVO")
public class GoodsVO {
	private int goodsId;
	private String goodsTitle;
	private String goodsWriter;
	private int    goodsPrice;
	private String goodsPublisher;
	private String goodsSort;
	private int    goodsSalesPrice;
	private int    goodsPoint;
	private Date   goodsPublishedDate;
	private int    goodsTotalPage;
	private String goodsIsbn;
	private String goodsDeliveryPrice;
	private Date   goodsDeliveryDate;
	private String goodsFileName;
	private String goodsStatus;
	private String goodsWriterIntro;
	private String goodsContentsOrder;
	private String goodsIntro;
	private String goodsPublisherComment;
	private String goodsRecommendation;
	private Date   goodsCredate;
	
}
