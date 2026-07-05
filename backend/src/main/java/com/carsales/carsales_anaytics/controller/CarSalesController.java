package com.carsales.carsales_anaytics.controller;

import com.carsales.carsales_anaytics.Service.CarSalesService;
import com.carsales.carsales_anaytics.commons.response.ApiResponse;
import com.carsales.carsales_anaytics.dto.MonthlyCountDto;
import com.carsales.carsales_anaytics.dto.UploadSalesResponse;
import com.carsales.carsales_anaytics.dto.YearlyCountDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/car-sales")
public class CarSalesController {
    private final CarSalesService salesService;

    public CarSalesController(CarSalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping("/upload-csv")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file){

        //file is available or not
        if(file.isEmpty()){
            //res
            UploadSalesResponse response = new UploadSalesResponse(0, 0, 0);
            ApiResponse<UploadSalesResponse> apiResponse = new ApiResponse<>(
                    false,
                    "File is Empty",
                    response,
                    HttpStatus.BAD_REQUEST.value() );

            return new ResponseEntity< ApiResponse<UploadSalesResponse>>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        // Change uploadCSV to uploadCsv
        UploadSalesResponse response = salesService.uploadCsv(file);
        ApiResponse<UploadSalesResponse> apiResponse = getApiResponse(response);

        return ResponseEntity.ok(apiResponse);
        //res
    }
    private static ApiResponse<UploadSalesResponse> getApiResponse(UploadSalesResponse response){
        String message;
        boolean success;

        if(response.getFailedCount()==0){
            message="All records uploaded successfully";
            success=true;
        }
        else if(response.getSuccessCount()==0){
            message="All records fail to upload";
            success=false;
        }
        else{
            message="Uploaded with some errors "+response.getFailedCount()+" rows failed";
            success=false;
        }

        return new ApiResponse<UploadSalesResponse>(success,message,response,HttpStatus.OK.value());
    }
    @GetMapping("/yearly-count")//this api is yearly count data
    public ResponseEntity<ApiResponse<List<YearlyCountDto>>> yearlyCount() {

        List<YearlyCountDto> carsCount = salesService.getYearlyCarsCount();

        ApiResponse<List<YearlyCountDto>> response = new ApiResponse<>(
                true,
                "Data Read Successfully",
                carsCount,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/monthly-count")//this is api for monthly count data
    public ResponseEntity<ApiResponse<List<MonthlyCountDto>>> monthlyCount(
            @RequestParam int year) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Monthly Data Read Successfully",
                        salesService.getMonthlyCountByYear(year),
                        HttpStatus.OK.value()
                )
        );
    }
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearData() {
        salesService.clearAllData();
        ApiResponse<?> response = new ApiResponse<>(true, "All data cleared", null, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand-count")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> brandCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Brand Data", salesService.getSalesByBrand(), HttpStatus.OK.value())
        );
    }

    @GetMapping("/color-count")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> colorCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Color Data", salesService.getSalesByColor(), HttpStatus.OK.value())
        );
    }

    @GetMapping("/state-count")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> stateCount() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "State Data", salesService.getSalesByState(), HttpStatus.OK.value())
        );
    }
    @GetMapping("/kpi-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kpiSummary() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "KPI Summary", salesService.getKpiSummary(), HttpStatus.OK.value())
        );
    }
}
