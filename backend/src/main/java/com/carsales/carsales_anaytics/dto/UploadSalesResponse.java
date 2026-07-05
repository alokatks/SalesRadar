package com.carsales.carsales_anaytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UploadSalesResponse {

    private int totalRecords;
    private int successCount;
    private int failedCount;
}