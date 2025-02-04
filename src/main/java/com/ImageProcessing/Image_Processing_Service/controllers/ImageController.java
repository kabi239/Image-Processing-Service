package com.ImageProcessing.Image_Processing_Service.controllers;

import com.ImageProcessing.Image_Processing_Service.model.requestModel.TransformationRequest;
import com.ImageProcessing.Image_Processing_Service.payload.ImageResponse;
import com.ImageProcessing.Image_Processing_Service.services.ImageServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageServiceImpl imageService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @PostMapping("/postImage")
    public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file)
    {
        if(file==null || file.isEmpty())
        {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        try{

            ImageResponse response = imageService.saveImage(file);
            return new ResponseEntity<>(response,HttpStatus.CREATED);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImageFromID(@PathVariable Long id)
    {
        try{
            ImageResponse response = imageService.getImageFromId(id);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getImages")
    public ResponseEntity<?> getUserImage(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10")int limit)
    {
        try {

            List<ImageResponse> responseList = imageService.getServerImages(page,limit);

            return ResponseEntity.ok(responseList);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/{id}/transform")
    public ResponseEntity<?> transformImage(@PathVariable Long id, @RequestParam(value = "request") String request, @RequestParam(value = "watermarkImageFile",required = false) MultipartFile watermarkImageFile)
    {
        try{
            ImageResponse response = imageService.getImageFromId(id);

            TransformationRequest transformationRequest = objectMapper.readValue(request, TransformationRequest.class);

            transformationRequest.getTransformations().setWatermarkImageFile(watermarkImageFile);

            response =   imageService.transformImage(response,transformationRequest.getTransformations());

            return new ResponseEntity<>(response,HttpStatus.OK);

        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
