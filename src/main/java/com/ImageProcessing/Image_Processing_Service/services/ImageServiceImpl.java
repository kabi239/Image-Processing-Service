package com.ImageProcessing.Image_Processing_Service.services;

import com.ImageProcessing.Image_Processing_Service.model.Image;
import com.ImageProcessing.Image_Processing_Service.payload.ImageResponse;
import com.ImageProcessing.Image_Processing_Service.repository.ImageRepository;
import com.ImageProcessing.Image_Processing_Service.utils.ImageUtil;
import com.ImageProcessing.Image_Processing_Service.model.requestModel.TransformationRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageServiceImpl {

    @Value("${image.upload.url}")
    private String uploadDir;
    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private ImageRepository imageRepo;


    @Autowired
    private ImageUtil imageUtil;

    public static String[] allowedFormat={"JPG", "JPEG", "PNG", "BMP", "WBMP" , "GIF"};


    public ImageResponse saveImage(MultipartFile file) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //uploading image to server
        String systemFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Path.of(uploadDir + systemFileName);
        file.transferTo(filePath);

        String fileUrl = "http://localhost:" + serverPort + "/" + uploadDir + systemFileName;
        ImageResponse response = this.saveImageInDB(file,fileUrl);
        return response;
    }

    public ImageResponse saveImageInDB(MultipartFile file,String url) throws IOException {


        //Making Image object
        Image image =  Image.builder()
                .type(file.getContentType())
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .url(url)
                .build();

        //Saving in db
        image =   imageRepo.save(image);

        ImageResponse response = this.convertToImageResponse(image);

        return response;
    }
    public ImageResponse saveImageInDB(File file,String url) throws IOException {


        Path filePath = file.toPath();
        //Making image object
        Image image =  Image.builder()
                .fileName(file.getName())
                .fileSize(file.length())
                .type(Files.probeContentType(filePath))
                .url(url)
                .build();



        //Saving in db
        image =   imageRepo.save(image);

        ImageResponse response = this.convertToImageResponse(image);

        return response;
    }
    private ImageResponse convertToImageResponse(Image image)
    {

        ImageResponse response = ImageResponse.builder()
                .imageId(image.getImageId())
                .url(image.getUrl())
                .fileSize(image.getFileSize())
                .type(image.getType())
                .build();

        return response;
    }
    public ImageResponse getImageFromId(Long id) throws Exception {


        try{
            Image image = imageRepo.findById(id).orElse(null);
            if(image!=null)
            {
                ImageResponse response = convertToImageResponse(image);
                return response;
            }
            else {
                throw new EntityNotFoundException("Image is not found");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new Exception(e.getMessage());
        }

    }


    public List<ImageResponse> getServerImages(int pageNo, int pageSize) throws Exception
    {

        try{
            Pageable pageable =  PageRequest.of(pageNo,pageSize);
            List<Image> userImages = imageRepo.findAll(pageable).toList();
            List<ImageResponse>responseList = userImages.stream().map(this::convertToImageResponse).toList();

            return responseList;

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public ImageResponse changeFormatAndSaveImage(BufferedImage image, String format, String url) throws Exception {

        int counter = 1;
        if(format!=null)
        {
            if(Arrays.stream(allowedFormat).anyMatch(f -> f.equals(format)))
            {
                String filename = System.currentTimeMillis()+"_Transformed_"+ counter+++"."+format.toLowerCase();
                String path = uploadDir+filename;
                ImageIO.write(image,format,new File(path));

                File newFile = new File(path);

                String fileUrl = "http://localhost:"+serverPort+"/"+uploadDir+ filename;

                ImageResponse response = this.saveImageInDB(newFile,fileUrl);

                return response;

            }
            else {
                throw new Exception("Incorrect format type");
            }



        }
        else {
            String filename = System.currentTimeMillis()+"_Transformed_"+ counter++ +".png";
            String path = uploadDir+filename;
            ImageIO.write(image,"PNG",new File(path));

            File newFile = new File(path);

            String fileUrl = "http://localhost:"+serverPort+"/"+uploadDir+ filename;

            ImageResponse response = this.saveImageInDB(newFile,fileUrl);

            return response;

        }
    }

    public ImageResponse transformImage(ImageResponse image, TransformationRequest.Transformations transformations) throws Exception {
        BufferedImage transformedImage =  imageUtil.convertImage(image.getUrl(),transformations);

        ImageResponse newImageResponse = this.changeFormatAndSaveImage(transformedImage,transformations.getFormat(),image.getUrl());

        return newImageResponse;

    }

}

