package com.board02.member.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.board02.member.vo.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberController {
	public ModelAndView listMembers(HttpServletRequest request, 
									HttpServletResponse response) throws Exception;
	
	public ModelAndView addMember(@ModelAttribute("info") MemberVO memberVO, 													
								 HttpServletRequest request, 
								 HttpServletResponse response) throws Exception;
	
	public ModelAndView removeMember(@RequestParam("id") String id, 
									HttpServletRequest request, 
									HttpServletResponse response) throws Exception;
	
	public ModelAndView login(@ModelAttribute("member") MemberVO member,
				               RedirectAttributes rAttr,
				               HttpServletRequest request, 
				               HttpServletResponse response) throws Exception;
	
	public ModelAndView logout(HttpServletRequest request, 
								HttpServletResponse response) throws Exception;
}
