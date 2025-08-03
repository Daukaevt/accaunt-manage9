package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.model.Picture;
import com.wixsite.mupbam1.repository.PictureRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final PictureRepository pictureRepository;

    public List<Picture> findAll() {
        return pictureRepository.findAll();
    }

	public List<Picture> getPicturesByOwner(String ownerKey, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return pictureRepository.findByOwnerKey(ownerKey, pageable).getContent();
	}
}
