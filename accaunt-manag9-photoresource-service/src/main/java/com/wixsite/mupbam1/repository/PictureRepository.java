package com.wixsite.mupbam1.repository;

import com.wixsite.mupbam1.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findByOwnerKey(String ownerKey);
}
