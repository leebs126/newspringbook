package com.bookshop01.admin.goods.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

public interface AdminGoodsController {
	public ModelAndView adminGoodsMain(@RequestParam Map<String, String> dateMap,HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public ResponseEntity addNewGoods(
	        @RequestParam("goodsData") String goodsDataJson, // JSON 문자열
	        MultipartHttpServletRequest multipartRequest,
	        HttpServletResponse response) throws Exception;
	
	public ResponseEntity modifyGoodsInfo( @RequestParam("goodsId") String goodsId,
                                 @RequestParam("modType") String modType,
                                 @RequestParam("value") String value,
			                     HttpServletRequest request, HttpServletResponse response)  throws Exception;
	
	public void  removeGoodsImage(@RequestParam("goodsId") int goodsId,
            @RequestParam("imageId") int imageId,
            @RequestParam("imageFileName") String imageFileName,
            HttpServletRequest request, HttpServletResponse response)  throws Exception;
	public void  addNewGoodsImage(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)  throws Exception;
	public void modifyGoodsImageInfo(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)  throws Exception;
}
