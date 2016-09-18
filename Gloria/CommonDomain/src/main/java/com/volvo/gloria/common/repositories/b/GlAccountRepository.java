package com.volvo.gloria.common.repositories.b;

import java.util.Set;

import com.volvo.gloria.common.d.entities.GlAccount;
import com.volvo.gloria.util.paging.c.PageObject;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * Interface for GLAccountRepository.
 */
public interface GlAccountRepository extends GenericRepository<GlAccount, Long> {

    PageObject findGlAccountsByCompanyCode(String companyCode, String glAccountStr, PageObject pageObject);

    Set<String> filterInvalidGlAccounts(Set<String> glAccountList, String companyCode);

}
