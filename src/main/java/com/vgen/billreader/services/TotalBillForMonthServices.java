package com.vgen.wemeat.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vgen.wemeat.dto.TotalBillForMonthdto;
import com.vgen.wemeat.model.TotalBillForMonth;
import com.vgen.wemeat.repositrory.TotalBillForMonthRepositrory;
@Service
public class TotalBillForMonthServices {
	@Autowired
	private TotalBillForMonthRepositrory totalBillForMonthRepositrory;
	
	public void save(TotalBillForMonthdto totalBillForMonthdto) {
		TotalBillForMonth totalBillForMonth=new TotalBillForMonth();
		totalBillForMonth.setBillAmount(totalBillForMonthdto.BillAmount);
		totalBillForMonth.setMobileNumber(totalBillForMonthdto.mobileNumber);
		
		totalBillForMonth.setMonth(totalBillForMonthdto.month);
		totalBillForMonth.setName(totalBillForMonthdto.Name);
		totalBillForMonth.setYear(totalBillForMonthdto.Year);
		totalBillForMonthRepositrory.save(totalBillForMonth);
	}
	
	public List<TotalBillForMonth> findbyMonthandYear(int year,int month){
		
		return totalBillForMonthRepositrory.findByYearandMonth(year, month);
	}
}
