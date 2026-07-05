package com.carsales.carsales_anaytics.Service;

import com.carsales.carsales_anaytics.dto.MonthlyCountDto;
import com.carsales.carsales_anaytics.dto.UploadSalesResponse;
import com.carsales.carsales_anaytics.dto.YearlyCountDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CarSalesService {

    UploadSalesResponse uploadCsv(MultipartFile file);
    List<YearlyCountDto> getYearlyCarsCount();
    List<MonthlyCountDto> getMonthlyCountByYear(int year);
   // void clearAllData();
   void clearAllData();
    List<Map<String, Object>> getSalesByBrand();
    List<Map<String, Object>> getSalesByColor();
    List<Map<String, Object>> getSalesByState();
    Map<String, Object> getKpiSummary();

}
