package com.metis.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductSpecificationRequest {

    @NotBlank(message = "Specification name is required")
    private String specName;

    @NotBlank(message = "Specification value is required")
    private String specValue;

    public ProductSpecificationRequest() {
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }
}