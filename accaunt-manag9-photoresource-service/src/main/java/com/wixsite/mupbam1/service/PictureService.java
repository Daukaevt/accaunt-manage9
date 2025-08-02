package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.model.Picture;
import com.wixsite.mupbam1.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final PictureRepository pictureRepository;

    public List<Picture> findAll() {
        return pictureRepository.findAll();
    }

    public List<Picture> findByOwnerKey(String ownerKey) {
        return pictureRepository.findByOwnerKey(ownerKey);
    }
}
