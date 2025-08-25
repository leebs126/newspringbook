package com.bookshop01.admin.goods.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.admin.goods.service.AdminGoodsService;
import com.bookshop01.common.base.BaseController;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.goods.vo.ImageFileVO;
import com.bookshop01.member.vo.MemberVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // ✅ 수정

@Controller
@Primary
@RequestMapping(value="/admin/goods")
public class AdminGoodsControllerImpl extends BaseController  implements AdminGoodsController{
	private static final String CURR_IMAGE_REPO_PATH = "C:\\shopping\\file_repo";
	@Autowired
	private AdminGoodsService adminGoodsService;
	
	@RequestMapping(value="/adminGoodsMain.do" ,method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView adminGoodsMain(@RequestParam Map<String, String> dateMap,
			                           HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		session.setAttribute("side_menu", "admin_mode"); //관리자 사이드 메뉴를 표시한다.
		
		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String section = dateMap.get("section");
		String pageNum = dateMap.get("pageNum");
		String beginDate=null,endDate=null;
		
		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate=tempDate[0];
		endDate=tempDate[1];
		dateMap.put("beginDate", beginDate);
		dateMap.put("endDate", endDate);
		
		Map<String,String> condMap=new HashMap<String, String>();
		if(section== null) {
			section = "1";
		}
		condMap.put("section",section);
		if(pageNum== null) {
			pageNum = "1";
		}
		condMap.put("pageNum",pageNum);
		condMap.put("beginDate",beginDate);
		condMap.put("endDate", endDate);
//		List<GoodsVO> newGoodsList = adminGoodsService.listNewGoods(condMap);
		Map<String, Object> goodsMap = adminGoodsService.listGoods(condMap);
		List<GoodsVO> goodsList = (List<GoodsVO>)goodsMap.get("goodsList");
		mav.addObject("goodsList", goodsList);
		
		//페이징 기능 구현 코드 추가
		int totalGoodsCount = (Integer)goodsMap.get("totalGoodsCount");
		int totalPage = (int) Math.ceil((double)totalGoodsCount / ORDERS_PER_PAGE);
		mav.addObject("totalPage", totalPage);
		
		String beginDate1[]=beginDate.split("-");
		String endDate2[]=endDate.split("-");
		mav.addObject("beginYear", Integer.parseInt(beginDate1[0]));
		mav.addObject("beginMonth", Integer.parseInt(beginDate1[1]));
		mav.addObject("beginDay",  Integer.parseInt(beginDate1[2]));
		mav.addObject("endYear",  Integer.parseInt(endDate2[0]));
		mav.addObject("endMonth",  Integer.parseInt(endDate2[1]));
		mav.addObject("endDay",  Integer.parseInt(endDate2[2]));
		
		mav.addObject("section",  Integer.parseInt(section));
		mav.addObject("pageNum",  Integer.parseInt(pageNum));
		
		
		
		return mav;
		
	}
	
	@PostMapping("/addNewGoods.do")
	public ResponseEntity addNewGoods(@RequestParam("goodsData") String goodsDataJson, // JSON 문자열
								        MultipartHttpServletRequest multipartRequest,
								        HttpServletResponse response) throws Exception {

	    multipartRequest.setCharacterEncoding("utf-8");
	    response.setContentType("text/html; charset=UTF-8");

	    String imageFileName = null;

	    // 1) JSON → Map 변환
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> newGoodsMap = mapper.readValue(goodsDataJson, Map.class);

	    // 2) 개행문자 변환 대상 키 목록
	    Set<String> newlineKeys = Set.of(
	        "goodsContentsOrder",
	        "goodsWriterIntro",
	        "goodsIntro",
	        "goodsPublisherComment",
	        "goodsRecommendation"
	    );

	    // 3) Map 데이터 후처리
	    for (Map.Entry<String, Object> entry : newGoodsMap.entrySet()) {
	        String key = entry.getKey();
	        Object rawValue = entry.getValue();

	        if (rawValue instanceof String value) {
	            // 개행 변환 대상이면 줄바꿈을 <br/>로 변환
	            if (newlineKeys.contains(key)) {
	                value = value.replaceAll("(\r\n|\n|\r)", "<br/>");
	            }
	            newGoodsMap.put(key, value);
	        }
	    }
	    
	    // 2) 로그인 회원 정보
	    HttpSession session = multipartRequest.getSession();
	    MemberVO memberVO = (MemberVO) session.getAttribute("memberInfo");
	    String regId = memberVO.getMemberId();  //등록자 아이디

	    // 3) 파일 업로드 처리
	    List<ImageFileVO> imageFileList = upload(multipartRequest);

	    // 업로드 파일 개수 제한 검사
	    if (imageFileList != null && imageFileList.size() > 10) {
	        throw new IllegalArgumentException("최대 10개 파일만 업로드할 수 있습니다.");
	    }

	    if (imageFileList != null && !imageFileList.isEmpty()) {
	        for (ImageFileVO imageFileVO : imageFileList) {
	            imageFileVO.setRegId(regId);
	        }
	        newGoodsMap.put("imageFileList", imageFileList);
	    }

	    String message;
	    ResponseEntity resEntity;
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/html; charset=utf-8");

	    try {
	        // 4) DB 저장
	        int goods_id = adminGoodsService.addNewGoods(newGoodsMap);

	        // 5) 파일 이동
	        if (imageFileList != null && !imageFileList.isEmpty()) {
	            for (ImageFileVO imageFileVO : imageFileList) {
	                imageFileName = imageFileVO.getFileName();
	                File srcFile = new File(CURR_IMAGE_REPO_PATH + "\\" + "temp" + "\\" + imageFileName);
	                File destDir = new File(CURR_IMAGE_REPO_PATH + "\\" + goods_id);
	                FileUtils.moveFileToDirectory(srcFile, destDir, true);
	            }
	        }

	        message = "<script>";
	        message += " alert('새상품을 추가했습니다.');";
	        message += " location.href='" + multipartRequest.getContextPath() + "/admin/goods/addNewGoodsForm.do';";
	        message += "</script>";

	    } catch (Exception e) {
	        // 업로드 실패 시 temp 파일 삭제
	        if (imageFileList != null && !imageFileList.isEmpty()) {
	            for (ImageFileVO imageFileVO : imageFileList) {
	                imageFileName = imageFileVO.getFileName();
	                File srcFile = new File(CURR_IMAGE_REPO_PATH + "\\" + "temp" + "\\" + imageFileName);
	                srcFile.delete();
	            }
	        }

	        message = "<script>";
	        message += " alert('오류가 발생했습니다. 다시 시도해 주세요');";
	        message += " location.href='" + multipartRequest.getContextPath() + "/admin/goods/addNewGoodsForm.do';";
	        message += "</script>";
	        e.printStackTrace();
	    }

	    resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
	    return resEntity;
	}

	
	@RequestMapping(value="/modifyGoodsForm.do" ,method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView modifyGoodsForm(@RequestParam("goodsId") int goodsId,
			                            HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		Map goodsMap=adminGoodsService.goodsDetail(goodsId);
		mav.addObject("goodsMap", goodsMap);
		
		return mav;
	}
	
	@PostMapping("/modifyGoodsInfo.do")
	public ResponseEntity modifyGoodsInfo( @RequestParam("goodsId") String goodsId,
			                     @RequestParam("attribute") String attribute,
			                     @RequestParam("value") String value,
			HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		Map<String,String> goodsMap=new HashMap<String,String>();
		goodsMap.put("goodsId", goodsId);
		
		// 모든 줄바꿈을 <br/>로 직접 변환
		if(attribute.equals("goodsContentsOrder") 
			||attribute.equals("goodsWriterIntro")
			||attribute.equals("goodsIntro")
			||attribute.equals("goodsPublisherComment")
			||attribute.equals("goodsRecommendation")) {
			value = value.replaceAll("(\r\n|\n|\r)", "<br/>");
		}
		goodsMap.put(attribute, value);
		adminGoodsService.modifyGoodsInfo(goodsMap);
		
		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		message  = "modSuccess";
		resEntity =new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
	}
	

	@PostMapping("/modifyGoodsImageInfo.do")
	public void modifyGoodsImageInfo(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)  throws Exception {
		System.out.println("modifyGoodsImageInfo");
		multipartRequest.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		String imageFileName=null;
		
		Map goodsMap = new HashMap();
		Enumeration enu=multipartRequest.getParameterNames();
		while(enu.hasMoreElements()){
			String name=(String)enu.nextElement();
			String value=multipartRequest.getParameter(name);
			goodsMap.put(name,value);
		}
		
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("memberInfo");
		String regId = memberVO.getMemberId();
		
		List<ImageFileVO> imageFileList=null;
		int goodsId=0;
		int imageId=0;
		try {
			imageFileList =upload(multipartRequest);
			if(imageFileList!= null && imageFileList.size()!=0) {
				for(ImageFileVO imageFileVO : imageFileList) {
					goodsId = Integer.parseInt((String)goodsMap.get("goodsId"));
					imageId = Integer.parseInt((String)goodsMap.get("imageId"));
					imageFileVO.setGoodsId(goodsId);
					imageFileVO.setImageId(imageId);
					imageFileVO.setRegId(regId);
				}
				
			    adminGoodsService.modifyGoodsImage(imageFileList);
				for(ImageFileVO  imageFileVO:imageFileList) {
					imageFileName = imageFileVO.getFileName();
					File srcFile = new File(CURR_IMAGE_REPO_PATH+"\\"+"temp"+"\\"+imageFileName);
					File destDir = new File(CURR_IMAGE_REPO_PATH+"\\"+goodsId);
					FileUtils.moveFileToDirectory(srcFile, destDir,true);
				}
			}
		}catch(Exception e) {
			if(imageFileList!=null && imageFileList.size()!=0) {
				for(ImageFileVO  imageFileVO:imageFileList) {
					imageFileName = imageFileVO.getFileName();
					File srcFile = new File(CURR_IMAGE_REPO_PATH+"\\"+"temp"+"\\"+imageFileName);
					srcFile.delete();
				}
			}
			e.printStackTrace();
		}
		
	}
	

	@Override
	@PostMapping("/addNewGoodsImage.do")
	public void addNewGoodsImage(MultipartHttpServletRequest multipartRequest, 
									HttpServletResponse response)
									throws Exception {
		System.out.println("addNewGoodsImage");
		multipartRequest.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		String imageFileName=null;
		
		Map goodsMap = new HashMap();
		Enumeration enu=multipartRequest.getParameterNames();
		while(enu.hasMoreElements()){
			String name=(String)enu.nextElement();
			String value=multipartRequest.getParameter(name);
			goodsMap.put(name, value);
		}
		
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("memberInfo");
		String regId = memberVO.getMemberId();
		
		List<ImageFileVO> imageFileList=null;
		int goodsId=0;
		try {
			imageFileList =upload(multipartRequest);
			if(imageFileList!= null && imageFileList.size()!=0) {
				for(ImageFileVO imageFileVO : imageFileList) {
					goodsId = Integer.parseInt((String)goodsMap.get("goodsId"));
					imageFileVO.setGoodsId(goodsId);
					imageFileVO.setRegId(regId);
				}
				
			    adminGoodsService.addNewGoodsImage(imageFileList);
				for(ImageFileVO  imageFileVO:imageFileList) {
					imageFileName = imageFileVO.getFileName();
					File srcFile = new File(CURR_IMAGE_REPO_PATH+"\\"+"temp"+"\\"+imageFileName);
					File destDir = new File(CURR_IMAGE_REPO_PATH+"\\"+goodsId);
					FileUtils.moveFileToDirectory(srcFile, destDir,true);
				}
			}
		}catch(Exception e) {
			if(imageFileList!=null && imageFileList.size()!=0) {
				for(ImageFileVO  imageFileVO:imageFileList) {
					imageFileName = imageFileVO.getFileName();
					File srcFile = new File(CURR_IMAGE_REPO_PATH+"\\"+"temp"+"\\"+imageFileName);
					srcFile.delete();
				}
			}
			e.printStackTrace();
		}
	}

	@Override
	@PostMapping("/removeGoodsImage.do")
	public void  removeGoodsImage(@RequestParam("goodsId") int goodsId,
			                      @RequestParam("imageId") int imageId,
			                      @RequestParam("imageFileName") String imageFileName,
			                      HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		adminGoodsService.removeGoodsImage(imageId);
		try{
			File srcFile = new File(CURR_IMAGE_REPO_PATH+"\\"+goodsId+"\\"+imageFileName);
			srcFile.delete();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
