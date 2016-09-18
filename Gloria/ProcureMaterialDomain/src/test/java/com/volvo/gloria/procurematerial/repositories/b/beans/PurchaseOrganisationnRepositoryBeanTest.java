package com.volvo.gloria.procurematerial.repositories.b.beans;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.c.dto.BuyerCodeDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.PurchaseOrganisation;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class PurchaseOrganisationnRepositoryBeanTest extends AbstractTransactionalTestCase {

    @Inject
    protected PurchaseOrganisationRepository purchaseOrganisationnRepository;
    
    public PurchaseOrganisationnRepositoryBeanTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    
    @Test
    public void testFindAll(){
        createBuyer("Rahul","code",null);
        List<Buyer> buyers = purchaseOrganisationnRepository.findAllBuyers();
        Assert.assertEquals(1, buyers.size());
        Assert.assertEquals("Rahul", buyers.get(0).getName()); 
    }
    
    @Test
    public void findBuyerByCode(){
        createBuyer("Rahul","code",null);
        Buyer buyerResult = purchaseOrganisationnRepository.findBuyerByCode("code");
        Assert.assertNotNull(buyerResult);
        Assert.assertEquals("Rahul", buyerResult.getName()); 
        
    }
    
    @Test
    public void findBuyerByCodeAndOrganisationCode(){
        createBuyerWithPurOrg("A020709","buyercode", "orgcode");
        Buyer buyerResult = purchaseOrganisationnRepository.findBuyerByCodeAndOrganisationCode("buyercode", "orgcode");
        Assert.assertNotNull(buyerResult);
        Assert.assertEquals("A020709", buyerResult.getName()); 
        
    }
    
    @Test
    public void findAllBuyers(){
        PageObject pageObject = new PageObject();
        pageObject.setCurrentPage(1);
        pageObject.setCount(10);
        pageObject.setResultsPerPage(10);
        createPurchaseOrganisation("Rahul","orgCode");
        PurchaseOrganisation purchaseOrganisationResult = purchaseOrganisationnRepository.findPurchaseOrgByCode("orgCode");
        createBuyer("Rahul1","code1",purchaseOrganisationResult);
        createBuyer("Rahul2","code2",purchaseOrganisationResult);
        createBuyer("Rahul3","code3",purchaseOrganisationResult);
        createBuyer("Rahul1","code1",purchaseOrganisationResult);
        pageObject = purchaseOrganisationnRepository.findAllBuyers(pageObject, "orgCode");
        Assert.assertEquals(3, pageObject.getGridContents().size());
    }

    @Test
    public void findPurchaseOrgByCode(){
        createPurchaseOrganisation("Rahul","orgCode");
        PurchaseOrganisation purchaseOrganisationResult = purchaseOrganisationnRepository.findPurchaseOrgByCode("orgCode");
        Assert.assertNotNull(purchaseOrganisationResult);
        Assert.assertEquals("Rahul", purchaseOrganisationResult.getOrganisationName()); 
    }

    private void createPurchaseOrganisation(String orgName, String orgCode) {
        PurchaseOrganisation purchaseOrganisation = new PurchaseOrganisation();
        purchaseOrganisation.setOrganisationName(orgName);
        purchaseOrganisation.setOrganisationCode(orgCode);
        purchaseOrganisationnRepository.save(purchaseOrganisation);
    }
    
    private void createBuyer(String name, String code, PurchaseOrganisation purchaseOrganisation ) {
        Buyer buyer = new Buyer();
        buyer.setName(name);
        buyer.setCode(code);
        buyer.setPurchaseOrganisation(purchaseOrganisation);
        purchaseOrganisationnRepository.save(buyer);
    }
    
    private void createBuyerWithPurOrg(String name, String code, String organisationCode) {
        Buyer buyer = new Buyer();
        buyer.setBuyerOid(0);
        buyer.setName(name);
        buyer.setCode(code);
        PurchaseOrganisation purchaseOrganisation = new PurchaseOrganisation();
        purchaseOrganisation.setOrganisationCode(organisationCode);
        buyer.setPurchaseOrganisation(purchaseOrganisation);
        purchaseOrganisation.setBuyers(Arrays.asList(buyer));
        purchaseOrganisationnRepository.save(buyer);
        purchaseOrganisationnRepository.save(purchaseOrganisation);
    }
}
