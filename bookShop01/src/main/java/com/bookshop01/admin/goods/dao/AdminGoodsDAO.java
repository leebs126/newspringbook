package com.bookshop01.admin.goods.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.goods.vo.ImageFileVO;
import com.bookshop01.order.vo.OrderVO;

@Mapper
public interface AdminGoodsDAO {
	public int insertNewGoods(Map newGoodsMap) throws DataAccessException;
	public List<GoodsVO>selectNewGoodsList(Map condMap) throws DataAccessException;
	public GoodsVO selectGoodsDetail(int goods_id) throws DataAccessException;
	public List selectGoodsImageFileList(int goods_id) throws DataAccessException;
	public void insertGoodsImageFile(List fileList)  throws DataAccessException;
	public void updateGoodsInfo(Map goodsMap) throws DataAccessException;
	public void updateGoodsImage(List<ImageFileVO> imageFileList) throws DataAccessException;
	public void deleteGoodsImage(int image_id) throws DataAccessException;
	public void deleteGoodsImage(List fileList) throws DataAccessException;
	public List<OrderVO> selectOrderGoodsList(Map condMap) throws DataAccessException;
	public void updateOrderGoods(Map orderMap) throws DataAccessException;
	
	//검색 기간내의 총 새제품 건수 반환
	public int selectTotalNewGoods(Map condMap) throws Exception;
}
