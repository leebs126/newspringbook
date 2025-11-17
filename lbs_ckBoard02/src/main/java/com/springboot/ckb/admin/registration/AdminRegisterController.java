package com.springboot.ckb.admin.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.ckb.common.security.admin.AdminRegistrationProperties;
import com.springboot.ckb.common.security.service.SercurityMemberService;
import com.springboot.ckb.member.domain.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/registration")
public class AdminRegisterController {

    private final AdminRegistrationProperties adminRegProps;
	private final SercurityMemberService sercurityMemberService;

    // 1️⃣ 관리자 등록 코드 입력창
    @GetMapping("/memberForm")
    public String adminCodeForm() {
        return "/admin/registration/adminCodeForm";   // 코드 입력 페이지
    }

    // 2️⃣ 등록 코드 검증
    @PostMapping("/verifyCode")
    public String verifyAdminCode(@RequestParam("adminCode") String adminCode,
                                  HttpSession session,
                                  Model model) {

        boolean isValid = adminRegProps.getCodes()
                                       .stream()
                                       .anyMatch(code -> code.equals(adminCode));

        if (!isValid) {
            model.addAttribute("error", "관리자 등록 코드가 일치하지 않습니다.");
            return "/admin/registration/adminCodeForm";
        }

        // 세션에 승인 플래그 저장
        session.setAttribute("adminRegistrationApproved", true);

        return "redirect:/admin/registration/adminMemberForm";
    }

    /// 3️⃣ 승인된 사용자만 관리자 등록폼 접근 가능
    @GetMapping("/adminMemberForm")
    public String adminMemberForm(HttpSession session, Model model) {

        // 세션에서 관리자 등록 승인 여부 확인
        Boolean approved = (Boolean) session.getAttribute("adminRegistrationApproved");

        if (approved == null || !approved) {
            return "redirect:/admin/memberForm"; // 비정상 접근 차단
        }

        // ★ 반드시 Member 객체를 모델에 추가
        model.addAttribute("member", new Member());

        return "/admin/registration/adminMemberForm";  // 관리자 등록 HTML
    }


    // 4️⃣ 관리자 회원가입 처리
    @PostMapping("/createAdmin")
    public String createAdmin(Member adminMember,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Boolean approved = (Boolean) session.getAttribute("adminRegistrationApproved");
        if (approved == null || !approved) {
            return "redirect:/admin/registration/adminMemberForm";
        }

        // ROLE_ADMIN 부여
        adminMember.setRole("ADMIN");
        

        // 승인 플래그 제거 (한번 등록 후 재사용 방지)
        try {
        	sercurityMemberService.saveAdmin(adminMember); // DB 저장
            // RedirectAttributes로 메시지 전달
            redirectAttributes.addFlashAttribute("adminCreatedSucceeded", "true");
            return "redirect:/member/loginForm"; // 회원가입 로그인 페이지로 이동
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("adminCreatedSucceeded", "false");
            return "redirect:/admin/registration/adminMemberForm"; // 오류 발생 시 다시 관리자 등록 페이지
        }finally {
        	session.removeAttribute("adminRegistrationApproved");	
        }
    }
}
