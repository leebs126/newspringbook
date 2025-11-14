package com.springboot.ckb.common.security.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	//요청값(이름, 값, 만료 기간)을 바탕으로 쿠키 추가
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	
	//쿠키의 이름을 입력받아 쿠키 삭제
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return;
		}
		
		for(Cookie cookie :  cookies) {
			if(name.equals(cookie.getName())) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
	
	//객체를 직렬화해 쿠키의 값을 변환
//	public static String serialize(Object obj) {
//		return Base64.getUrlEncoder()
//				.encodeToString(SerializationUtils.serialize(obj));
//	}
	
	// 쿠키를 역직렬화해 객체로 변환
//	public static <T> T deserialization(Cookie cookie, Class<T> cls){
//		return cls.cast(
//				SerializationUtils.deserialize(
//						Base64.getUrlDecoder().decode(cookie.getValue())
//						)
//				);
//	}
	
	public static String serialize(Object obj) {
	    if (obj == null) {
	        return "";
	    }

	    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         ObjectOutputStream oos = new ObjectOutputStream(baos)) {

	        oos.writeObject(obj);
	        oos.flush();
	        byte[] bytes = baos.toByteArray();
	        return Base64.getUrlEncoder().encodeToString(bytes);

	    } catch (IOException e) {
	        throw new IllegalStateException("Serialization failed", e);
	    }
	}
	
	
	public static <T> T deserialization(Cookie cookie, Class<T> cls) {
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isEmpty()) {
            return null;
        }

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cookie.getValue());
            try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object obj = ois.readObject();
                return cls.cast(obj);
            }
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            // 예외 발생 시 null 반환 (로그를 남기면 추적 가능)
            System.err.println("⚠️ 쿠키 역직렬화 실패: " + e.getMessage());
            return null;
        }
    }

}






