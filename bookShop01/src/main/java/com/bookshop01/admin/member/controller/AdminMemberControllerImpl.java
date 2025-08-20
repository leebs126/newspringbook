package com.bookshop01.admin.member.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.admin.member.service.AdminMemberService;
import com.bookshop01.common.base.BaseController;
import com.bookshop01.member.vo.MemberVO;

@Controller
@Primary
@RequestMapping(value="/admin/member")
public class AdminMemberControllerImpl extends BaseController  implements AdminMemberController{
	@Autowired
	private AdminMemberService adminMemberService;
	
	@RequestMapping(value="/adminMemberMain.do" ,method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView adminGoodsMain(@RequestParam Map<String, String> dateMap,
			                           HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String section = dateMap.get("section");
		String pageNum = dateMap.get("pageNum");
		String beginDate=null,endDate=null;
		
		String [] tempDate=calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate=tempDate[0];
		endDate=tempDate[1];
		dateMap.put("beginDate", beginDate);
		dateMap.put("endDate", endDate);
		
		
		HashMap<String,Object> condMap=new HashMap<String,Object>();
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
		ArrayList<MemberVO> memberList=adminMemberService.listMember(condMap);
		mav.addObject("memberList", memberList);
		
		//페이징 기능 구현 코드 추가
		int totalMemberCount = memberList.size();
		int totalPage = (int) Math.ceil((double)totalMemberCount / ORDERS_PER_PAGE);
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
	@RequestMapping(value="/adminMemberDetail.do" ,method={RequestMethod.POST,RequestMethod.GET})
	public ModelAndView memberDetail(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		String memberId=request.getParameter("memberId");
		MemberVO memberInfo=adminMemberService.memberDetail(memberId);
		
		int memberBirthY = Integer.parseInt(memberInfo.getMemberBirthY());
		int memberBirthM = Integer.parseInt(memberInfo.getMemberBirthM());
		int memberBirthD = Integer.parseInt(memberInfo.getMemberBirthD());
		mav.addObject("memberBirthY", memberBirthY);  //생년월일의 년,월,일을 정수로 변환해서 상세페이지로 전달한다.
		mav.addObject("memberBirthM", memberBirthM);
		mav.addObject("memberBirthD", memberBirthD);
		mav.addObject("memberInfo",memberInfo);
		return mav;
	}
	
	@RequestMapping(value="/modifyMemberInfo.do" ,method={RequestMethod.POST,RequestMethod.GET})
	public void modifyMemberInfo(@RequestParam Map<String, String> memDataMap,
								HttpServletRequest request, 
								HttpServletResponse response)  throws Exception{
		HashMap<String,String> memberMap=new HashMap<String,String>();
		String val[]=null;
		PrintWriter pw=response.getWriter();
		
		String memberId=memDataMap.get("memberId");
		String modType=memDataMap.get("modType");
		String value =memDataMap.get("value");
		
		if(modType.equals("memberBirth")){
			val=value.split(",");
			memberMap.put("memberBirthY",val[0]);
			memberMap.put("memberBirthM",val[1]);
			memberMap.put("memberBirthD",val[2]);
			memberMap.put("memberBirthGn",val[3]);
		}else if(modType.equals("tel")){
			val=value.split(",");
			memberMap.put("tel1",val[0]);
			memberMap.put("tel2",val[1]);
			memberMap.put("tel3",val[2]);
			
		}else if(modType.equals("hp")){
			val=value.split(",");
			memberMap.put("hp1",val[0]);
			memberMap.put("hp2",val[1]);
			memberMap.put("hp3",val[2]);
			memberMap.put("smsstsYn", val[3]);
		}else if(modType.equals("email")){
			val=value.split(",");
			memberMap.put("email1",val[0]);
			memberMap.put("email2",val[1]);
			memberMap.put("emailstsYn", val[2]);
		}else if(modType.equals("address")){
			val=value.split(",");
			memberMap.put("zipcode",val[0]);
			memberMap.put("roadAddress",val[1]);
			memberMap.put("jibunAddress", val[2]);
			memberMap.put("namujiAddress", val[3]);
		}else {
			memberMap.put(modType, value);
		}
		
		memberMap.put("memberId", memberId);
		adminMemberService.modifyMemberInfo(memberMap);
		pw.print("modSuccess");
		pw.close();		
		
	}
	
	@PostMapping("/deleteOrRecoverMembership.do")
	public ModelAndView deleteMember(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView mav = new ModelAndView();
		HashMap<String,String> memberMap=new HashMap<String,String>();
		String memberId=request.getParameter("memberId");
		String delYn=request.getParameter("delYn");
		memberMap.put("delYn", delYn);
		memberMap.put("memberId", memberId);
		
		adminMemberService.modifyMemberInfo(memberMap);
		mav.setViewName("redirect:/admin/member/adminMemberMain.do");
		return mav;
		
	}
		
}
