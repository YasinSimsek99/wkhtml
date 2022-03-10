package com.html.to.pdf.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface Wkhtml {
    void createPdf(MultipartFile multipartFile, String path) throws IOException, InterruptedException;

}
