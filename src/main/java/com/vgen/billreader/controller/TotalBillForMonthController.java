package com.vgen.billreader.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import com.vgen.billreader.dto.TotalBillForMonthdto;
import com.vgen.billreader.services.TotalBillForMonthServices;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tv/bill")
@Slf4j
public class TotalBillForMonthController {
	@Autowired
	private TotalBillForMonthServices totalBillForMonthServices;
	
	String[]phoneNumbers= {"201-702-3929","330-501-4669","469-617-1147","803-693-2543",
			"803-792-2439","803-992-3317","803-992-3443",
			"980-616-1500","803-203-9530","773-575-9355","615-487-3250","615-487-3250","803-693-2505"};
    @PostMapping
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile files) {
		 try {
	            PDDocument document = PDDocument.load(files.getInputStream());
	            int year=Integer.parseInt( files.getOriginalFilename().substring(7, 11));
	            int month=Integer.parseInt(files.getOriginalFilename().substring(11, 13));
			 	int x;
	            if((year>=2024)||((year==2023)&&(month>=4))) {
	            	x=2;
	            }
				else {
					x = 1;
				}
	            var MonthAndYear=	totalBillForMonthServices.findbyMonthAndYear(year, month);
	            if (!(MonthAndYear.isEmpty())) {
	            	document.close();
					return ResponseEntity.status(HttpStatus.FOUND)
							.body(String.format("File not Uploaded: %s", files.getOriginalFilename() + " files is  fond"));
				}
	           
	            // Instantiate PDFTextStripper class
	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            for(int i=document.getNumberOfPages()-1;i>12;i--) {
	            	document.removePage(i);
	            }
	            
	            System.out.println(document.getNumberOfPages());
	            
	            // Retrieving text from PDF document
	            String text = pdfStripper.getText(document);
	            
	            // Printing the text
	            

	            		
	            String[] data = text.split("\n+");
	            

	            XSSFWorkbook workbook = new XSSFWorkbook();
	            XSSFSheet sheet = workbook.createSheet("pdf content");
	            for(int i=0;i<data.length;i++) {
	            Row row = sheet.createRow(i);
	            		String[] data2=	data[i].split("\\s+");
	            			for(int j=0;j<data2.length;j++) {
	                        Cell cell = row.createCell(j);
	                        cell.setCellValue(data2[j]);
	                    }
	            	}
	            		
	          String FileName= "../BillReader/template.xlsx";
	            FileOutputStream outputStream = new FileOutputStream(FileName);
	            
	            workbook.write(outputStream);

	            // Closing the workbook
	            workbook.close();

	            // Closing the document
	            document.close();
	            
	            FileInputStream file = new FileInputStream(new File(FileName));
				XSSFWorkbook workbook2 = new XSSFWorkbook(file);
				
				int index = workbook2.getSheetIndex("pdf content");

				XSSFSheet sheet2 = workbook2.getSheetAt(index);
				
				int number=0;

				sheet2.getLastRowNum();
				while(phoneNumbers.length>number) {
					for(int rowindex=0;rowindex<sheet2.getLastRowNum()-1;rowindex++) {
						XSSFRow xrow=	sheet2.getRow(rowindex);
						
						for(int cellindex = 0;xrow.getLastCellNum()>cellindex;cellindex++) {
							XSSFCell xcell=xrow.getCell(cellindex);

							if (xcell.getCellTypeEnum() == CellType.STRING) {
								if(number==phoneNumbers.length) {
									break ;
								}
								if(xcell.getStringCellValue().equals(phoneNumbers[number])) {
									
									XSSFRow row=	sheet2.getRow((rowindex)-x);
									TotalBillForMonthdto totalBillForMonthdto=new TotalBillForMonthdto();
									
									if (row.getCell(0).getCellTypeEnum()==CellType.STRING) {
										
										System.out.print(row.getCell(0).getStringCellValue()+" ");
									}
									
									if (row.getCell(1).getCellTypeEnum()==CellType.STRING) {
										System.out.println(row.getCell(1).getStringCellValue());
										totalBillForMonthdto.Name=row.getCell(0).getStringCellValue()+" "+row.getCell(1).getStringCellValue();
									}
									if(x==2) {
									
									if (row.getCell(2).getCellTypeEnum()==CellType.STRING) {
										System.out.println(row.getCell(2).getStringCellValue());
										totalBillForMonthdto.BillAmount=row.getCell(2).getStringCellValue();
									}
									}
									else {
										row=	sheet2.getRow((rowindex)+1);
										if (row.getCell(0).getCellTypeEnum()==CellType.STRING) {
											System.out.println(row.getCell(0).getStringCellValue());
											totalBillForMonthdto.BillAmount=row.getCell(0).getStringCellValue();
										}
									}
									totalBillForMonthdto.mobileNumber=phoneNumbers[number];	
									totalBillForMonthdto.month=month;
									totalBillForMonthdto.Year=year;
									totalBillForMonthServices.save(totalBillForMonthdto);
									number=number+1;
									
									
									
								}
								
								
							} 
							
						}
						
					}
					if(number<phoneNumbers.length) {
						number=number+1;
					}
					
					}
				file.close();
				workbook.close();
				
				System.out.println("PDF content written to Excel successfully!");
	           
	        } catch (IOException e) {

			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body(String.format("User not Uploaded:Exception %s", ""));


	        }
	        
     
     
    
		 return ResponseEntity.status(HttpStatus.OK)
					.body(String.format("File  Uploaded: %s", files.getOriginalFilename() + " files is Ok"));
		
		 
		 
	 }
}
