package com.amit.image.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amit.image.payload.FileResponse;
import com.amit.image.service.FileService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private FileService fileService;
	
	@Value("${project.image}")
	private String path;
	
	@PostMapping("/upload")
	public ResponseEntity<FileResponse> fileUpload(
			@RequestParam("image") MultipartFile image
			){
		String filename=null;
		try {
			filename= this.fileService.uploadImage(path, image);
		}
		catch(IOException e){
			e.printStackTrace();
			return new ResponseEntity<>(new FileResponse(null,"Image is not uploaded due to some reason"),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new FileResponse(filename,"Image is sucessfully uploaded"),HttpStatus.OK);
	}
	
	
//	method to serve the file
	@GetMapping(value="/profiles/{imageName}", produces=MediaType.IMAGE_JPEG_VALUE)
	public  void downloadImage(
			@PathVariable("imageName") String imageName, HttpServletResponse response
			) throws FileNotFoundException,IOException {
		InputStream resource= this.fileService.getResource(path, imageName);
		
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		
		StreamUtils.copy(resource, response.getOutputStream());
	}
	
}
