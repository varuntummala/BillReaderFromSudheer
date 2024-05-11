package com.vgen.billreader.services;

import java.util.List;


import com.vgen.billreader.dto.TotalBillForMonthdto;
import com.vgen.billreader.model.TotalBillForMonth;
import com.vgen.billreader.repositrory.TotalBillForMonthRepositrory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalBillForMonthServices {
	@Autowired
	private TotalBillForMonthRepositrory totalBillForMonthRepositrory;
	
	public void save(TotalBillForMonthdto totalBillForMonthdto) {
		TotalBillForMonth totalBillForMonth=new TotalBillForMonth();
		totalBillForMonth.setBillAmount(totalBillForMonthdto.billAmount);
		totalBillForMonth.setMobileNumber(totalBillForMonthdto.mobileNumber);
		
		totalBillForMonth.setMonth(totalBillForMonthdto.month);
		totalBillForMonth.setName(totalBillForMonthdto.name);
		totalBillForMonth.setYear(totalBillForMonthdto.year);
		totalBillForMonthRepositrory.save(totalBillForMonth);
	}
	
	public List<TotalBillForMonth> findbyMonthAndYear(int year,int month){
		
		return totalBillForMonthRepositrory.findByYearAndMonth(year, month);
	}
}
