package com.springboot.ckb.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.springboot.ckb.member.domain.Member;
import com.springboot.ckb.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/myInfo")
	public String myInfo(Model model, Authentication auth, HttpServletRequest request) throws Exception {
	    String memId = auth.getName();
	    Member member = memberService.findMemberById(memId);
	    model.addAttribute("member", member);

	    // CSRF 토큰 추가
	    CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
	    model.addAttribute("_csrf", token);
	    return "member/myInfo";
	}

    @PostMapping("/modify")
    public String modifyMember(Member member)  throws Exception {
    	 // 로그인 사용자의 memId로 강제 설정 (보안상 필요)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memId = auth.getName();
        member.setMemId(memId);
        memberService.modifyMember(member);
        return "redirect:/member/myInfo";
    }

    @GetMapping("/removeAccount")
    public String removeAccount(Authentication auth)  throws Exception {
        String memId = auth.getName();
        memberService.removeAccount(memId);
        return "redirect:/logout";
    }

}
