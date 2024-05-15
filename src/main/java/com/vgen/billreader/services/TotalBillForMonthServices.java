package com.vgen.billreader.services;

import java.util.List;
import java.util.Optional;


import com.vgen.billreader.dto.TotalBillForMonthdto;
import com.vgen.billreader.model.TotalBillForMonth;
import com.vgen.billreader.repositrory.TotalBillForMonthRepositrory;

import org.springframework.stereotype.Service;

@Service
public class TotalBillForMonthServices {

	private final  TotalBillForMonthRepositrory totalBillForMonthRepositrory;
	TotalBillForMonthServices(TotalBillForMonthRepositrory totalBillForMonthRepositrory){
		this.totalBillForMonthRepositrory=totalBillForMonthRepositrory;
	}
	
	public void save(TotalBillForMonthdto totalBillForMonthdto) {
		TotalBillForMonth totalBillForMonth=new TotalBillForMonth();
		totalBillForMonth.setBillAmount(totalBillForMonthdto.billAmount);
		totalBillForMonth.setMobileNumber(totalBillForMonthdto.mobileNumber);
		
		totalBillForMonth.setMonth(totalBillForMonthdto.month);
		totalBillForMonth.setName(totalBillForMonthdto.name);
		totalBillForMonth.setYear(totalBillForMonthdto.year);
		totalBillForMonthRepositrory.save(totalBillForMonth);
	}
	public Optional<TotalBillForMonth> findById(Long id) {


	return totalBillForMonthRepositrory.findById(id);
	}


	public List<TotalBillForMonth> findbyMonthAndYear(int year,int month){
		
		return totalBillForMonthRepositrory.findByYearAndMonth(year, month);
	}
}
