package com.vgen.billreader.model;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table
@Data
public class TotalBillForMonth {
	@Id

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private String mobileNumber;

	private String Name;

	private int month;

	private int year;

	private String BillAmount;
}
