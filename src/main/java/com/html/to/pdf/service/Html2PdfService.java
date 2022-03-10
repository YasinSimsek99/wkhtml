package com.html.to.pdf.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface Html2PdfService {

    byte[] oneToOneConvert(MultipartFile multipartFile) throws IOException, InterruptedException;

    byte[] manyToManyConvert(List<MultipartFile> htmlFiles) throws IOException, InterruptedException;

    byte[] manyToOneConvert(List<MultipartFile> htmlFiles) throws IOException, InterruptedException;

}
