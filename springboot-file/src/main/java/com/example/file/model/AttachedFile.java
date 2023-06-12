package com.example.file.model;

import lombok.Data;

@Data
public class AttachedFile {

	private Long id;
	private String originalFile;
	private String savedFile;

	public AttachedFile(String originalFile, String savedFile) {
		this.originalFile = originalFile;
		this.savedFile = savedFile;
	}

}

// 브라우저에서 선택한 파일 명 A.txt(originalFile) ->
// 서버로 전송되어 저장될 파일 명 A.txt(savedFile) 가 되면 안된다.
// 다운로드가 될 때는 본래 이름(originalName)으로 다운로드 되는 것이 타당하다.
// 서버에 저장될 때만 저장될 파일 명으로 저장된다.
// 고로, 서버에는 두 개의 이름을 둘 다 갖고 있어야 한다.
