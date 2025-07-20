package com.timesheetspro_api.common.dto.companyReportDto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class CompanyReportResponse {
    private List<CompanyReportDto> content;
    private int totalPages;
    private int currentPage;
    private Integer nextPage;
    private int numberOfElements;
    private boolean last;
    private String sortDirection;

    public CompanyReportResponse(Page<CompanyReportDto> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.nextPage = page.hasNext() ? currentPage + 1 : null;
        this.numberOfElements = page.getNumberOfElements();
        this.last = page.isLast();

        Sort.Order order = page.getSort().stream().findFirst().orElse(null);
        this.sortDirection = (order != null) ? order.getDirection().name() : "UNSORTED";
    }
}
