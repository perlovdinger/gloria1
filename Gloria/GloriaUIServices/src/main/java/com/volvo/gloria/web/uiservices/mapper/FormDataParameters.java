package com.volvo.gloria.web.uiservices.mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sun.jersey.multipart.FormDataMultiPart;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaSystemException;

/**
 * Content desposition,Multipart,FileDetail.
 * 
 */
public final class FormDataParameters {
    private FormDataParameters() {
        super();
    }

    public static DocumentDTO getDocument(FormDataMultiPart formDataMultiPart) {
        DocumentDTO document = new DocumentDTO();
        document.setContent(readFileContent(formDataMultiPart.getField("docFile").getValueAs(InputStream.class)));
        document.setName(formDataMultiPart.getField("docFile").getFormDataContentDisposition().getFileName());
        document.setSize(document.getContent().length);
        return document;
    }

    private static byte[] readFileContent(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads;
        try {
            reads = inputStream.read();
            while (reads != -1) {
                baos.write(reads);
                reads = inputStream.read();
            }
        } catch (IOException e) {
            throw new GloriaSystemException(e, e.getLocalizedMessage());
        }
        return baos.toByteArray();
    }
}
