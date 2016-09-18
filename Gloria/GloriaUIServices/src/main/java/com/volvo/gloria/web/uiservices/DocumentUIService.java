/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.web.uiservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.multipart.FormDataMultiPart;
import com.volvo.gloria.materialrequest.b.MaterialRequestServices;
import com.volvo.gloria.procurematerial.b.DeliveryServices;
import com.volvo.gloria.procurematerial.util.DeliveryHelper;
import com.volvo.gloria.util.DocumentDTO;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.gloria.util.c.GloriaExceptionConstants;
import com.volvo.gloria.web.uiservices.mapper.FormDataParameters;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

/**
 * Restful service for handling file upload/download functionality.
 * 
 */
@Path("/documents")
public class DocumentUIService {

    private DeliveryServices deliveryServices = ServiceLocator.getService(DeliveryServices.class);

    private MaterialRequestServices materialRequestServices = ServiceLocator.getService(MaterialRequestServices.class);

    private static final String ID = "id";

    
    @GET    
    @Path("/v1/receivedocs/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadReceiveDoc(@PathParam(ID) Long qualityDocId) {
        DocumentDTO document = DeliveryHelper.transformAsDTO(deliveryServices.getReceiveDocument(qualityDocId), true);
        if (document != null) {
            return Response.ok(document.getContent(), MediaType.APPLICATION_OCTET_STREAM)
                           .header("content-disposition", "attachment; filename = " + document.getName()).build();
        }
        return null;
    }
    
    @POST    
    @Path("/v1/deliverynotelines/{id}/receivedoc")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentDTO uploadRecevedocV1(@PathParam(ID) Long deliveryNoteLineId, FormDataMultiPart formDataMultiPart) throws GloriaApplicationException {     
        return DeliveryHelper.transformAsDTO(deliveryServices.uploadReceiveDocuments(deliveryNoteLineId, FormDataParameters.getDocument(formDataMultiPart)),
                                             false);
    }

    @GET
    @Path("/v1/deliverynotelines/{id}/receivedocs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentDTO> getReceiveDocsMetasV1(@PathParam(ID) Long deliveryNoteLineId) {
          return DeliveryHelper.transformReceiveDocsAsDocumentDTO(deliveryServices.getReceiveDocuments(deliveryNoteLineId), false);
    }

    @DELETE
    @Path("/v1/receivedocs/{id}")   
    public void deleteReceiveDoc(@PathParam(ID) Long qualityDocId) {       
        deliveryServices.deleteReceiveDocument(qualityDocId);
    }

    @GET
    @Path("/v1/problemdocs/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadProblemDoc(@PathParam(ID) Long problemNoteDocId) {
        DocumentDTO document = DeliveryHelper.transformAsDTO(deliveryServices.getProblemDocument(problemNoteDocId), true);
        if (document != null) {
            return Response.ok(document.getContent(), MediaType.APPLICATION_OCTET_STREAM)
                           .header("content-disposition", "attachment; filename = " + document.getName()).build();
        }
        return null;
    }

    @POST
    @Path("/v1/deliverynotelines/{id}/problemdoc")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentDTO uploadProblemdocsV1(@PathParam(ID) Long deliveryNoteLineId, FormDataMultiPart formDataMultiPart) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.uploadProblemDocuments(deliveryNoteLineId, FormDataParameters.getDocument(formDataMultiPart)),
                                             false);
    }

    @GET
    @Path("/v1/deliverynotelines/{id}/problemdocs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentDTO> getProblemDocsMetasV1(@PathParam(ID) Long deliveryNoteLineId) {
        return DeliveryHelper.transformProblemDocsAsDocumentDTO(deliveryServices.getProblemDocuments(deliveryNoteLineId), false);
    }

    @DELETE
    @Path("/v1/problemdocs/{id}")
    public void deleteProblemDoc(@PathParam(ID) Long problemNoteDocId) {
        deliveryServices.deleteProblemDoc(problemNoteDocId);
    }

   

    @GET
    @Path("/v1/deliverynotelines/{id}/qidocs")    
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentDTO> getQiDocsMetasV1(@PathParam(ID) Long deliveryNoteLineId) {
        return DeliveryHelper.transformQiDocsAsDocumentDTO(deliveryServices.getQiDocs(deliveryNoteLineId), false);
    }

    @POST
    @Path("/v1/deliverynotelines/{id}/qidoc")   
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentDTO uploadQidocsV1(@PathParam(ID) Long deliveryNoteLineId, FormDataMultiPart formDataMultiPart) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.uploadQiDocs(deliveryNoteLineId, FormDataParameters.getDocument(formDataMultiPart)),
                                             false);
    }

    @GET
    @Path("/v1/qidocs/{id}") 
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadQiDoc(@PathParam(ID) Long inspectionDocId) {
        DocumentDTO document = DeliveryHelper.transformAsDTO(deliveryServices.getQiDoc(inspectionDocId), true);
        if (document != null) {
            return Response.ok(document.getContent(), MediaType.APPLICATION_OCTET_STREAM)
                           .header("content-disposition", "attachment; filename = " + document.getName()).build();
        }
        return null;
    }
    @DELETE
    @Path("/v1/qidocs/{id}")
    public void deleteQiDoc(@PathParam(ID) Long inspectionDocId) {
        deliveryServices.deleteQiDoc(inspectionDocId);
    }

    @GET
    @Path("/v1/deliveryschedules/{id}/attacheddocs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DocumentDTO> getAttachedDocsMetasV1(@PathParam(ID) Long deliveryScheduleId) {
        return DeliveryHelper.transformAttachedDocsAsDocumentDTO(deliveryServices.getAttachedDocuments(deliveryScheduleId), false);
    }

    @GET
    @Path("/v1/attacheddocs/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadAttachedDoc(@PathParam(ID) Long attachedDocId) {
        DocumentDTO document = DeliveryHelper.transformAsDTO(deliveryServices.getAttachedDocument(attachedDocId), true);
        if (document != null) {
            return Response.ok(document.getContent(), MediaType.APPLICATION_OCTET_STREAM)
                           .header("content-disposition", "attachment; filename = " + document.getName()).build();
        }
        return null;
    }

    @POST
    @Path("/v1/deliveryschedules/{id}/attacheddoc")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentDTO uploadAttacheddocsV1(@PathParam(ID) Long deliveryScheduleId, FormDataMultiPart formDataMultiPart) throws GloriaApplicationException {
        return DeliveryHelper.transformAsDTO(deliveryServices.uploadAttachedDocuments(deliveryScheduleId, FormDataParameters.getDocument(formDataMultiPart)),
                                             false);
    }

    @POST
    @Path("/v1/materialrequest/{id}/materialrequestlines")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentDTO uploadMaterialRequestDocsV1(@PathParam(ID) Long materialRequestId, FormDataMultiPart formDataMultiPart)
            throws GloriaApplicationException {
        DocumentDTO documentUploaded = FormDataParameters.getDocument(formDataMultiPart);
        try {
            materialRequestServices.uploadMaterialRequestDocuments(materialRequestId, documentUploaded);
        } catch (GloriaApplicationException e) {
            throw new GloriaApplicationException(GloriaExceptionConstants.INVALID_EXCEL_DATA,
                                                 "The data in excel is not valid. Please upload an excel with valid data", null);
        }
        return documentUploaded;
    }

    @DELETE
    @Path("/v1/attacheddocs/{id}")
    public void deleteAttachedDoc(@PathParam(ID) Long attachedDocId) {
        deliveryServices.deleteAttachedDoc(attachedDocId);
    }
}
