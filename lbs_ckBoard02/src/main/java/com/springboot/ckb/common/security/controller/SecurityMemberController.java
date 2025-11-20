package com.springboot.ckb.common.security.controller;


import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.springboot.ckb.common.security.service.SercurityMemberService;
import com.springboot.ckb.member.domain.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
//@RequestMapping("/member")
public class SecurityMemberController {

    private final SercurityMemberService sercurityMemberService;
    // 회원가입 처리
    @PostMapping("/member/registerMember")
    public String registerMember(@ModelAttribute Member member, Model model, RedirectAttributes redirectAttributes)  {
    	try {
    		sercurityMemberService.save(member);

            // RedirectAttributes로 메시지 전달
            redirectAttributes.addFlashAttribute("memberCreatedSucceeded", "true");
            return "redirect:/member/loginForm"; // 회원가입 로그인 페이지로 이동
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("memberCreatedSucceeded", "false");
            return "redirect:/member/memberForm"; // 오류 발생 시 다시 회원가입 페이지
        }
    }
    

    /**
     * 구글 로그인 버튼 클릭 시 OAuth2 로그인 시작
     */
    @GetMapping("/member/oauthLogin")
    public void oauthLogin(HttpServletResponse response) throws IOException {
        // Spring Security에서 설정한 OAuth2 로그인 URL로 리다이렉트
        // 기본적으로 Spring Security OAuth2는 /oauth2/authorization/{provider} 경로를 사용
        response.sendRedirect("/oauth2/authorization/google");
    }
    
    
    // 회원가입 폼
    @GetMapping("/member/memberForm")
    public String memberForm(Model model) {
        model.addAttribute("member", new Member());
        return "member/memberForm"; 
    }

    
	@GetMapping("/member/*Form")
	private ModelAndView form(@RequestParam(value= "loginFailed", required=false) String loginFailed,
							  @RequestParam(value= "action", required=false) String action,
							  @RequestParam(value= "parentNO", required=false) String parentNO,
							  @RequestParam(value= "groupNO", required=false) String groupNO,
							  @RequestParam(value= "articleNO", required=false) String articleNO,  //댓글 쓰기시 글번호
							  @RequestParam(value= "commentNO", required=false) String commentNO,
							  @RequestParam(value = "redirect", required = false) String redirect, 
							  HttpServletRequest request, 
						       HttpServletResponse response) throws Exception {
		String viewName = (String)request.getAttribute("viewName");
		HttpSession session = request.getSession();
		session.setAttribute("action", action);   //로그인 성공 후 다른 페이지로 이동 
		if(parentNO != null && !parentNO.isEmpty() ) {  //답글쓰기로 로그인 시 
			session.setAttribute("parentNO", parentNO);
		}
		
		if(groupNO != null && !groupNO.isEmpty() ) {  //답글쓰기로 로그인 시 
			session.setAttribute("groupNO", groupNO);
		}
		
		if(articleNO != null && !articleNO.isEmpty()) {  //댓글 쓰기 시 로그인 후 해당 글번호의 글로 이동
			session.setAttribute("articleNO", articleNO);
		}
		if(commentNO != null && !commentNO.isEmpty()) {  //댓글 쓰기 시 로그인 후 해당 글번호의 글로 이동
			session.setAttribute("commentNO", commentNO);
		}
		
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("loginFailed",Boolean.parseBoolean(loginFailed));
		mav.setViewName(viewName);
		return mav;
	}

}
