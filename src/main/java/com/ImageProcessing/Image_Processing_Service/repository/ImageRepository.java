package com.ImageProcessing.Image_Processing_Service.repository;

import com.ImageProcessing.Image_Processing_Service.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository <Image,Long>{
}
