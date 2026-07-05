package com.carsales.carsales_anaytics.Service;
import java.util.*;

import com.carsales.carsales_anaytics.dto.MonthlyCountDto;
import com.carsales.carsales_anaytics.dto.YearlyCountDto;
import com.carsales.carsales_anaytics.entity.CarSales;
//import com.carsales.carsales_anaytics.controller.CarSales;// here i am doing mistake
import com.carsales.carsales_anaytics.dto.UploadSalesResponse;
import com.carsales.carsales_anaytics.repository.CarSalesRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class CarSalesServiceImpl implements CarSalesService{
    private final CarSalesRepository repository;

    public CarSalesServiceImpl(CarSalesRepository repository) {
        this.repository = repository;
    }
    @Override
    public UploadSalesResponse uploadCsv(MultipartFile file) {
        List<CarSales>cars=new ArrayList<>();
        int failCount=0;
        int totalRecords=0;
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            //CSVFormat
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader() //Header
                    .setSkipHeaderRecord(true) //Skip(Not Treated as Data)
                    .setIgnoreHeaderCase(true) //Case-insensitive
                    .setTrim(true) //
                    .build(); //build

            CSVParser csvParser = CSVParser.parse(reader, csvFormat);
            for( CSVRecord record : csvParser ){
                totalRecords++;
                try {
                    String carNumber = record.get("Car Number");
                    boolean exists = repository.existsByCarNumber(carNumber);

                    if (exists) {
                        failCount++;
                        System.out.println("Duplicate Data Found " + carNumber);
                        continue;
                    }
                    CarSales carSales = new CarSales();
                    carSales.setCarNumber(record.get("Car Number"));
                    carSales.setBrand(record.get("Brand"));
                    carSales.setModel(record.get("Model"));
                    carSales.setColor(record.get("Color"));
                    carSales.setYear(Integer.parseInt(record.get("Year")));
                    carSales.setDateOfPurchase(LocalDate.parse(record.get("Date of Purchase"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    carSales.setTimeOfPurchase(LocalTime.parse(record.get("Time of Purchase")));
                    carSales.setPrice(Long.parseLong(record.get("Price (Rs)")));
                    carSales.setMileage(Double.parseDouble(record.get("Mileage (km/l)")));
                    carSales.setEngine(Integer.parseInt(record.get("Engine (cc)")));
                    carSales.setFuelType(record.get("Fuel Type"));
                    carSales.setPaymentMode(record.get("Payment Mode"));
                    carSales.setState(record.get("State"));
                    carSales.setCity(record.get("City"));
                    carSales.setCustomerName(record.get("Customer Name"));
                    carSales.setContactNumber(record.get("Contact Number"));
                    carSales.setEmail(record.get("Email"));
                    carSales.setWarrantyPeriod(Integer.parseInt(record.get("Warranty Period (years)")));
                    cars.add(carSales);
                }catch(Exception e){
                    failCount++;
                    System.out.println("fail to process Row"+ record.getRecordNumber());
                }
            }
            if(!cars.isEmpty()){
                repository.saveAll(cars);
            }
        }
        catch (Exception exception){
            throw new RuntimeException("Unable to Parse CSV: " + exception.getMessage());
        }
        int successCount = totalRecords - failCount;
        return new UploadSalesResponse(totalRecords, successCount, failCount);
    }

    @Override
    public List<YearlyCountDto> getYearlyCarsCount() {
        return repository.getYearlyCount();
    }


    @Override
    public List<MonthlyCountDto> getMonthlyCountByYear(int year) {

        List<MonthlyCountDto> data = repository.getMonthlyCountByYear(year);

        Map<Integer, Long> map = data.stream()
                .collect(Collectors.toMap(
                        MonthlyCountDto::month,
                        MonthlyCountDto::count
                ));

        List<MonthlyCountDto> result = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            result.add(new MonthlyCountDto(
                    i,
                    map.getOrDefault(i, 0L)
            ));
        }

        return result;
}
    @Override
    public void clearAllData() {
        repository.deleteAll();
    }
    @Override
    public List<Map<String, Object>> getSalesByBrand() {
        return repository.findSalesByBrand()  // ye naam wrong hoga
                .stream()
                .map(row -> Map.of("label", row[0], "count", row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getSalesByColor() {
        return repository.findSalesByColor()
                .stream()
                .map(row -> Map.of("label", row[0], "count", row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getSalesByState() {
        return repository.findSalesByState()
                .stream()
                .map(row -> Map.of("label", row[0], "count", row[1]))
                .collect(Collectors.toList());
    }
    @Override
    public Map<String, Object> getKpiSummary() {

        // Total cars
        long totalCars = repository.count();

        // Top Brand
        String topBrand = repository.findSalesByBrand()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        // Top Color
        String topColor = repository.findSalesByColor()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        // Top State
        String topState = repository.findSalesByState()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        // Avg Price
        Double avgPrice = repository.findAveragePrice();

        // Top Fuel Type
        String topFuel = repository.findTopFuelType()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        // Top Payment Mode
        String topPayment = repository.findTopPaymentMode()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        // Top City
        String topCity = repository.findTopCity()
                .stream().findFirst()
                .map(r -> r[0].toString()).orElse("N/A");

        Map<String, Object> kpi = new HashMap<>();
        kpi.put("totalCars",   totalCars);
        kpi.put("topBrand",    topBrand);
        kpi.put("topColor",    topColor);
        kpi.put("topState",    topState);
        kpi.put("avgPrice",    avgPrice != null ? Math.round(avgPrice) : 0);
        kpi.put("topFuel",     topFuel);
        kpi.put("topPayment",  topPayment);
        kpi.put("topCity",     topCity);

        return kpi;
    }
}
