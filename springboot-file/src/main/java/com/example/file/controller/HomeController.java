package com.example.file.controller;

import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.example.file.model.AttachedFile;
import com.example.file.repository.BoardMapper;
import com.example.file.util.FileService;
import com.example.file.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final FileService fileService;
	private final BoardMapper boardMapper;

	// 페이징 처리 상수값
	final int countPerPage = 10; // 페이지 당 글의 수
	final int pagePerGroup = 10; // 페이지 그룹 당 표시할 페이지 수

	@Value("${file.upload.path}") // 파일이 저장될 경로 설정
	private String uploadPath;

	@GetMapping
	public String home() {
		return "index";
	}

	@GetMapping("upload")
	public String uploadForm() {
		return "uploadForm";
	}

	@PostMapping("upload")
	public String upload(MultipartFile file) {
		log.info("file: {}", file);
		log.info("file.getOriginalFileName(): {}", file.getOriginalFilename());
		log.info("file.getSize(): {}", file.getSize());

		AttachedFile savedFile = fileService.saveFile(file);
		log.info("savedFile: {}", savedFile);

		boardMapper.savedFile(savedFile);

		return "redirect:/";
	}

	@GetMapping("download")
	public String downloadForm(Model model, @RequestParam(defaultValue = "1") int page) {

		int total = boardMapper.getTotal();

		// 페이징 처리를 위한 네비게이터 객체 생성
		PageNavigator navigator = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navigator", navigator);

		RowBounds rowBounds = new RowBounds(navigator.getStartRecord(), navigator.getCountPerPage());
		List<AttachedFile> files = boardMapper.findAllFiles(rowBounds);
		model.addAttribute("files", files);

		return "downloadForm";
	}

	@GetMapping("download/{id}")
	public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
		log.info("id: {}", id);
		AttachedFile attachedFile = boardMapper.findFileById(id);
		String fullPath = uploadPath + attachedFile.getSavedFile();
		log.info("fullPath: {}", fullPath);

		// 바디에 넣어줄 값 세팅 (file: 서버에 저장된 파일의 경로)ㄴ
		UrlResource resource = new UrlResource("file:" + fullPath);

		// 헤더를 넣어줄 값 세팅 (attached: filename="다운로드 받을 파일 명"
		String encodingFileName = UriUtils.encode(attachedFile.getOriginalFile(), StandardCharsets.UTF_8);
		String contentDisposition = "attached; filename=\"" + encodingFileName + "\"";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
	}

}
