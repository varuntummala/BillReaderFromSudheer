package com.vgen.billreader.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name = "TotalBillForMonth")
@Data
public class TotalBillForMonth {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@Column(name = "mobile_number", nullable = false)
	private String mobileNumber;
	@Column(name = "name", nullable = false)
	private String Name;
	@Column(name = "month", nullable = false)
	private int month;
	@Column(name = "year", nullable = false)
	private int Year;
	@Column(name = "bill_amount", nullable = false)
	private String BillAmount;
}
