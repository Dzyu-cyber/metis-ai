package com.metis.backend.dto;

public class ProductSpecificationResponse {

    private Long id;
    private String specName;
    private String specValue;

    public ProductSpecificationResponse(
            Long id,
            String specName,
            String specValue) {

        this.id = id;
        this.specName = specName;
        this.specValue = specValue;
    }

    public Long getId() {
        return id;
    }

    public String getSpecName() {
        return specName;
    }

    public String getSpecValue() {
        return specValue;
    }
}