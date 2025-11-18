package com.springboot.ckb.admin.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.ckb.member.domain.Member;
import com.springboot.ckb.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/listMembers")
	public ModelAndView listMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setContentType("html/text;charset=utf-8");
		String viewName = (String)request.getAttribute("viewName");
		List<Member> membersList = memberService.listMembers();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("membersList", membersList);
		return mav;
	}
	
	@GetMapping("/removeAccount")
    public String removeAccount(@RequestParam("memId") String memId) {
        memberService.removeAccount(memId);
        return "redirect:/admin/member/listMembers";
    }
	
	@GetMapping("/memberInfo")
	public String myInfo(@RequestParam(value= "memId", required=false) String memId,
			             Model model,  HttpServletRequest request) throws Exception {
	    Member member = memberService.findMemberById(memId);
	    model.addAttribute("member", member);
	    // CSRF 토큰 추가
	    CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
	    model.addAttribute("_csrf", token);
	    return "admin/member/modMemberInfo";
	}
	
	 @PostMapping("/modify")
    public String modifyMember(Member member)  throws Exception {
    	 // 로그인 사용자의 memId로 강제 설정 (보안상 필요)
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String memId = auth.getName();
//        member.setMemId(memId);
        memberService.modifyMember(member);
        return "redirect:/admin/member/listMembers";
    }

}
