package com.html.to.pdf.service.impl;

import com.html.to.pdf.service.Html2PdfService;
import com.html.to.pdf.service.Wkhtml;
import net.lingala.zip4j.ZipFile;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class Html2PdfServiceImpl implements Html2PdfService {
    private static final String ZIP_EXTENTION = ".zip";
    private static final String ZIP_FOLDER = "zip/";
    private static final String PDF = "pdf/";
    private static final String PDF_FILE_TYPE = ".pdf";

    private final Wkhtml wkhtml;

    public Html2PdfServiceImpl(Wkhtml wkhtml) {
        this.wkhtml = wkhtml;
    }

    @Override
    public byte[] oneToOneConvert(MultipartFile multipartFile) throws IOException, InterruptedException {
        String pathAndName = createFilePath();
        Files.createDirectories(Paths.get(pathAndName));

        File file = new File(pathAndName);

        String rarName = ZIP_FOLDER + UUID.randomUUID() + ZIP_EXTENTION;
        wkhtml.createPdf(multipartFile, pathAndName);
        new ZipFile(rarName).addFiles(Arrays.asList(Objects.requireNonNull(file.listFiles())));
        byte[] fileContent = Files.readAllBytes(Paths.get(rarName));

        FileUtils.deleteDirectory(file);
        Files.delete(Paths.get(rarName));
        return fileContent;
    }

    @Override
    public byte[] manyToManyConvert(List<MultipartFile> htmlFiles) throws IOException, InterruptedException {
        String pathAndName = createFilePath();
        Files.createDirectories(Paths.get(pathAndName));
        File file = new File(pathAndName);

        for (MultipartFile htmlFile : htmlFiles) {
            wkhtml.createPdf(htmlFile, pathAndName);
        }

        String rarName = ZIP_FOLDER + UUID.randomUUID() + ZIP_EXTENTION;
        new ZipFile(rarName).addFiles(Arrays.asList(Objects.requireNonNull(file.listFiles())));

        byte[] fileContent = Files.readAllBytes(Paths.get(rarName));
        FileUtils.deleteDirectory(file);
        Files.delete(Paths.get(rarName));
        return fileContent;
    }

    @Override
    public byte[] manyToOneConvert(List<MultipartFile> htmlFiles) throws IOException, InterruptedException {
        String pathAndName = createFilePath();
        Files.createDirectories(Paths.get(pathAndName));
        File file = new File(pathAndName);

        for (MultipartFile htmlFile : htmlFiles) {
            wkhtml.createPdf(htmlFile, pathAndName);
        }

        String random = UUID.randomUUID().toString();
        String rarName = ZIP_FOLDER + random + ZIP_EXTENTION;
        PDFMergerUtility ut = new PDFMergerUtility();
        for (File f : Objects.requireNonNull(file.listFiles())) {
            ut.addSource(f);
        }
        ut.setDestinationFileName(PDF + random + PDF_FILE_TYPE);
        ut.mergeDocuments();

        File mergedFile = new File(PDF + random + PDF_FILE_TYPE);
        new ZipFile(rarName).addFiles(Collections.singletonList(Objects.requireNonNull(mergedFile)));

        byte[] fileContent = Files.readAllBytes(Paths.get(rarName));
        FileUtils.deleteDirectory(file);
        Files.delete(Paths.get(mergedFile.getPath()));
        Files.delete(Paths.get(rarName));
        return fileContent;
    }

    private String createFilePath() {
        return "pdf/" + UUID.randomUUID();
    }

}
