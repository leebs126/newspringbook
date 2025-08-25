package com.bookshop01.admin.goods.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bookshop01.admin.goods.dao.AdminGoodsDAO;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.goods.vo.ImageFileVO;
import com.bookshop01.order.vo.OrderVO;


@Service
@Primary
@Transactional(propagation=Propagation.REQUIRED)
public class AdminGoodsServiceImpl implements AdminGoodsService {
	@Autowired
	private AdminGoodsDAO adminGoodsDAO;
	
	@Override
	public int addNewGoods(Map newGoodsMap) throws Exception{
		adminGoodsDAO.insertNewGoods(newGoodsMap);
		int goodsId = Integer.parseInt( String.valueOf(newGoodsMap.get("goodsId")));
		ArrayList<ImageFileVO> imageFileList = (ArrayList)newGoodsMap.get("imageFileList");
		for(ImageFileVO imageFileVO : imageFileList) {
			imageFileVO.setGoodsId(goodsId);
		}
		
		addNewGoodsImage(imageFileList);
		return goodsId;
	}
	
	@Override
	public Map<String, Object> listGoods(Map<String, String> condMap) throws Exception{
		Map<String, Object> goodsMap =new HashMap<String, Object>();
		List<GoodsVO> goodsList = adminGoodsDAO.selectGoodsList(condMap);
		int totalGoodsCount = adminGoodsDAO.selectTotalGoods(condMap);
		goodsMap.put("goodsList", goodsList);
		goodsMap.put("totalGoodsCount", totalGoodsCount);
		return  goodsMap;
	}
	@Override
	public Map goodsDetail(int goodsId) throws Exception {
		Map goodsMap = new HashMap();
		GoodsVO goodsVO=adminGoodsDAO.selectGoodsDetail(goodsId);
		List imageFileList =adminGoodsDAO.selectGoodsImageFileList(goodsId);
		goodsMap.put("goods", goodsVO);
		goodsMap.put("imageFileList", imageFileList);
		return goodsMap;
	}
	@Override
	public List goodsImageFile(int goodsId) throws Exception{
		List imageList =adminGoodsDAO.selectGoodsImageFileList(goodsId);
		return imageList;
	}
	
	@Override
	public void modifyGoodsInfo(Map goodsMap) throws Exception{
		adminGoodsDAO.updateGoodsInfo(goodsMap);
		
	}	
	@Override
	public void modifyGoodsImage(List<ImageFileVO> imageFileList) throws Exception{
		adminGoodsDAO.updateGoodsImage(imageFileList); 
	}
	
	@Override
	public void modifyOrderGoods(Map orderMap) throws Exception{
		adminGoodsDAO.updateOrderGoods(orderMap);
	}
	
	@Override
	public void removeGoodsImage(int imageId) throws Exception{
		adminGoodsDAO.deleteGoodsImage(imageId);
	}
	
	@Override
	public void addNewGoodsImage(List<ImageFileVO> imageFileList) throws Exception{
		 for (ImageFileVO imageFile : imageFileList) {
			 adminGoodsDAO.insertGoodsImageFile(imageFile);
		 }
	}
	

	
}
