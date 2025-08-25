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
	public List<GoodsVO>selectGoodsList(Map condMap) throws DataAccessException;
	public GoodsVO selectGoodsDetail(int goodsId) throws DataAccessException;
	public List selectGoodsImageFileList(int goodsId) throws DataAccessException;
	public void insertGoodsImageFile(ImageFileVO imageFileVO)  throws DataAccessException;
	public void updateGoodsInfo(Map goodsMap) throws DataAccessException;
	public void updateGoodsImage(List<ImageFileVO> imageFileList) throws DataAccessException;
	public void deleteGoodsImage(int imageId) throws DataAccessException;
	public void deleteGoodsImage(List fileList) throws DataAccessException;
	public void updateOrderGoods(Map orderMap) throws DataAccessException;
	
	//검색 기간내의 총 상품 건수 반환
	public int selectTotalGoods(Map condMap) throws Exception;
}
