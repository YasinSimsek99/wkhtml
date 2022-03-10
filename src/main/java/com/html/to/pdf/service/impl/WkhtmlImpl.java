package com.html.to.pdf.service.impl;

import com.html.to.pdf.service.Wkhtml;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class WkhtmlImpl implements Wkhtml {
    @Override
    public void createPdf(MultipartFile multipartFile, String path) throws IOException, InterruptedException {
        Process wkhtml;

        File destinationFile = new File(path + "/" + UUID.randomUUID() + ".pdf");

        InputStream fis = multipartFile.getInputStream();
        FileOutputStream fos = new FileOutputStream(destinationFile);

        String command = "wkhtmltopdf - -";

        wkhtml = Runtime.getRuntime().exec(command);

        Thread errThread = new Thread(() -> {
            try {
                IOUtils.copy(wkhtml.getErrorStream(), System.err);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread htmlReadThread = new Thread(() -> {
            try {
                IOUtils.copy(fis, wkhtml.getOutputStream());
                wkhtml.getOutputStream().flush();
                wkhtml.getOutputStream().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread pdfWriteThread = new Thread(() -> {
            try {
                IOUtils.copy(wkhtml.getInputStream(), fos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        errThread.start();
        pdfWriteThread.start();
        htmlReadThread.start();

        wkhtml.waitFor();
        fis.close();
        fos.close();
    }
}
