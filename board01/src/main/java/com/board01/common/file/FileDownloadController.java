package com.board01.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FileDownloadController {
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\article_image1";
	
	@RequestMapping("/download.do")
	protected void download(@RequestParam("imageFileName") String imageFileName,
							@RequestParam("articleNO") String articleNO,
						     HttpServletResponse response)throws Exception {
//		OutputStream out = response.getOutputStream();
		String filePath= ARTICLE_IMAGE_REPO + "\\" +articleNO+"\\"+ imageFileName;
		File imageFile=new File(filePath);

		 if (imageFile.exists()) {
		        // 한글 파일명 인코딩 처리
		        String encodedFilename = URLEncoder.encode(imageFile.getName(), "UTF-8").replaceAll("\\+", "%20");
		        response.setContentType("application/octet-stream");
		        response.setContentLength((int) imageFile.length());
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");

		        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(imageFile));
		             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
		            FileCopyUtils.copy(in, out);
		        }
		    }
		
	}
	
	
//	@RequestMapping("/download.do")
//	protected void download(@RequestParam("imageFileName") String imageFileName,
//										@RequestParam("articleNO") String articleNO,
//						                 HttpServletResponse response)throws Exception {
//		OutputStream out = response.getOutputStream();
//		String downFile = ARTICLE_IMAGE_REPO + "\\" +articleNO+"\\"+ imageFileName;
//		File file = new File(downFile);
//
//		response.setHeader("Cache-Control", "no-cache");
//		response.addHeader("Content-disposition", "attachment; fileName=" + imageFileName);
//		FileInputStream in = new FileInputStream(file);
//		byte[] buffer = new byte[1024 * 8];
//		while (true) {
//			int count = in.read(buffer); 
//			if (count == -1) 
//				break;
//			out.write(buffer, 0, count);
//		}
//		in.close();
//		out.close();
//	}
}

