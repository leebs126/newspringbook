package com.bookshop01.admin.goods.service;

import java.util.List;
import java.util.Map;

import com.bookshop01.goods.vo.ImageFileVO;

public interface AdminGoodsService {
	public int  addNewGoods(Map newGoodsMap) throws Exception;
	public Map<String, Object> listGoods(Map<String, String> condMap) throws Exception;
	public Map goodsDetail(int goodsId) throws Exception;
	public List goodsImageFile(int goodsId) throws Exception;
	public void modifyGoodsInfo(Map goodsMap) throws Exception;
	public void modifyGoodsImage(List<ImageFileVO> imageFileList) throws Exception;
	public void modifyOrderGoods(Map orderMap) throws Exception;
	public void removeGoodsImage(int imageId) throws Exception;
	public void addNewGoodsImage(List<ImageFileVO> imageFileList) throws Exception;
	
	
	
}
