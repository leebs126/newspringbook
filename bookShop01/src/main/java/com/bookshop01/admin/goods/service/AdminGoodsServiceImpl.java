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
		int goods_id = Integer.parseInt( String.valueOf(newGoodsMap.get("goods_id")));
		ArrayList<ImageFileVO> imageFileList = (ArrayList)newGoodsMap.get("imageFileList");
		for(ImageFileVO imageFileVO : imageFileList) {
			imageFileVO.setGoods_id(goods_id);
		}
		
		addNewGoodsImage(imageFileList);
		return goods_id;
	}
	
	@Override
	public Map<String, Object> listNewGoods(Map<String, String> condMap) throws Exception{
		Map<String, Object> newGoodsMap =new HashMap<String, Object>();
		List<GoodsVO> newGoodsList = adminGoodsDAO.selectNewGoodsList(condMap);
		int totalItemCount = adminGoodsDAO.selectTotalNewGoods(condMap);
		newGoodsMap.put("newGoodsList", newGoodsList);
		newGoodsMap.put("totalItemCount", totalItemCount);
		return  newGoodsMap;
	}
	@Override
	public Map goodsDetail(int goods_id) throws Exception {
		Map goodsMap = new HashMap();
		GoodsVO goodsVO=adminGoodsDAO.selectGoodsDetail(goods_id);
		List imageFileList =adminGoodsDAO.selectGoodsImageFileList(goods_id);
		goodsMap.put("goods", goodsVO);
		goodsMap.put("imageFileList", imageFileList);
		return goodsMap;
	}
	@Override
	public List goodsImageFile(int goods_id) throws Exception{
		List imageList =adminGoodsDAO.selectGoodsImageFileList(goods_id);
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
	public List<OrderVO> listOrderGoods(Map condMap) throws Exception{
		return adminGoodsDAO.selectOrderGoodsList(condMap);
	}
	@Override
	public void modifyOrderGoods(Map orderMap) throws Exception{
		adminGoodsDAO.updateOrderGoods(orderMap);
	}
	
	@Override
	public void removeGoodsImage(int image_id) throws Exception{
		adminGoodsDAO.deleteGoodsImage(image_id);
	}
	
	@Override
	public void addNewGoodsImage(List<ImageFileVO> imageFileList) throws Exception{
		 for (ImageFileVO imageFile : imageFileList) {
			 adminGoodsDAO.insertGoodsImageFile(imageFile);
		 }
	}
	

	
}
