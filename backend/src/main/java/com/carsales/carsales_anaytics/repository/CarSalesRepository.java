package com.carsales.carsales_anaytics.repository;

import com.carsales.carsales_anaytics.dto.MonthlyCountDto;
import com.carsales.carsales_anaytics.dto.YearlyCountDto;
import com.carsales.carsales_anaytics.entity.CarSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CarSalesRepository extends JpaRepository<CarSales, Long> {

    boolean existsByCarNumber(String carNumber);
    @Query("SELECT new com.carsales.carsales_anaytics.dto.YearlyCountDto(c.year, count(c)) " +
            "FROM CarSales c " +
            "GROUP BY c.year " +
            "ORDER BY c.year")
    List<YearlyCountDto> getYearlyCount();

    @Query("""
            SELECT new com.carsales.carsales_anaytics.dto.MonthlyCountDto(
                MONTH(c.dateOfPurchase), COUNT(c)
            )
            FROM CarSales c
            WHERE YEAR(c.dateOfPurchase) = :year
            GROUP BY MONTH(c.dateOfPurchase)
            ORDER BY MONTH(c.dateOfPurchase)
            """)
    List<MonthlyCountDto> getMonthlyCountByYear(@Param("year") int year);
    // Brand wise count
    @Query("SELECT cs.brand, COUNT(cs) FROM CarSales cs GROUP BY cs.brand ORDER BY COUNT(cs) DESC")
    List<Object[]> findSalesByBrand();

    // Color wise count
    @Query("SELECT cs.color, COUNT(cs) FROM CarSales cs GROUP BY cs.color ORDER BY COUNT(cs) DESC")
    List<Object[]> findSalesByColor();

    // State wise count
    @Query("SELECT cs.state, COUNT(cs) FROM CarSales cs GROUP BY cs.state ORDER BY COUNT(cs) DESC")
    List<Object[]> findSalesByState();

    // Average Price
    @Query("SELECT AVG(cs.price) FROM CarSales cs")
    Double findAveragePrice();

    // Top Fuel Type
    @Query("SELECT cs.fuelType, COUNT(cs) FROM CarSales cs GROUP BY cs.fuelType ORDER BY COUNT(cs) DESC")
    List<Object[]> findTopFuelType();

    // Top Payment Mode
    @Query("SELECT cs.paymentMode, COUNT(cs) FROM CarSales cs GROUP BY cs.paymentMode ORDER BY COUNT(cs) DESC")
    List<Object[]> findTopPaymentMode();

    // Top City
    @Query("SELECT cs.city, COUNT(cs) FROM CarSales cs GROUP BY cs.city ORDER BY COUNT(cs) DESC")
    List<Object[]> findTopCity();
}
