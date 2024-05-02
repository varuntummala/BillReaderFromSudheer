package com.vgen.billreader.repositrory;

import java.util.List;


import com.vgen.billreader.model.TotalBillForMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;




public interface TotalBillForMonthRepositrory extends JpaRepository<TotalBillForMonth, Long> {
	
	@Query("FROM TotalBillForMonth WHERE year=?1 AND month=?2")
	List<TotalBillForMonth> findByYearAndMonth(int year, int month);
	
}
