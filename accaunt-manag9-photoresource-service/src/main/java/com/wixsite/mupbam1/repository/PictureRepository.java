package com.wixsite.mupbam1.repository;

import com.wixsite.mupbam1.model.Picture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
	Page<Picture> findByOwnerKey(String ownerKey, Pageable pageable);

}
