package com.example.file.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.example.file.model.AttachedFile;

@Mapper
public interface BoardMapper {

	int savedFile(AttachedFile file);

	List<AttachedFile> findAllFiles(RowBounds rowBounds);

	AttachedFile findFileById(Long id);

	int getTotal();

}
