package com.vgen.wemeat.repositrory;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vgen.wemeat.model.TotalBillForMonth;


public interface TotalBillForMonthRepositrory extends JpaRepository<TotalBillForMonth, Long> {
	
	@Query("FROM TotalBillForMonth WHERE year=?1 AND month=?2")
	List<TotalBillForMonth> findByYearandMonth(int year, int month);
	
}
