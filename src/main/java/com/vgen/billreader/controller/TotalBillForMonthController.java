package com.vgen.billreader.controller;


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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tv/bill")
@Slf4j
public class TotalBillForMonthController {

	private final TotalBillForMonthServices totalBillForMonthServices;

	TotalBillForMonthController(TotalBillForMonthServices totalBillForMonthServices){
		this.totalBillForMonthServices=totalBillForMonthServices;
	}

	String[]phoneNumbers= {"325191128-00001","201-702-3929","330-501-4669","469-617-1147","803-693-2543",
			"803-792-2439","803-992-3317","803-992-3443",
			"980-616-1500","803-203-9530","773-575-9355","615-487-3250","615-487-3250","803-693-2505"};
    @PostMapping
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile[] multipartfiles) {
		String monthName;
		int year=0;
		int month=0;
		int x=1;

		HashMap<String, Integer> months = getHashMap();
		String[]  filefoundnames=new String[multipartfiles.length];
			String[] fileuploadednames=new String[multipartfiles.length];
				int filefound=0;
				int fileuploaded=0;
		 try {
			 System.out.println(multipartfiles.length);
			 Nextfile:
			 for (MultipartFile files : multipartfiles) {
				PDDocument document = PDDocument.load(files.getInputStream());
	            // Instantiate PDFTextStripper class
	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            for(int i=document.getNumberOfPages()-1;i>12;i--) {
	            	document.removePage(i);
	            }
	            System.out.println(document.getNumberOfPages());
	            String text = pdfStripper.getText(document);

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
	            FileOutputStream outputStream = new FileOutputStream("template.xlsx");
	            workbook.write(outputStream);
	            workbook.close();
	            document.close();
	            FileInputStream file = new FileInputStream("template.xlsx");
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
									if(phoneNumbers[number].equals("325191128-00001")) {

										System.out.println(phoneNumbers[number]);
										if(cellindex==2) {
											XSSFRow row=	sheet2.getRow((rowindex-1));
											if (row.getCell(2).getCellTypeEnum()==CellType.STRING) {

												monthName=row.getCell(2).getStringCellValue();
												month=months.get(monthName);
												System.out.println(month);
											}
											if (row.getCell(4).getCellTypeEnum()==CellType.STRING) {

												year=Integer.parseInt(row.getCell(4).getStringCellValue());
												System.out.println(year);
											}


										}
										else  {
											XSSFRow row=	sheet2.getRow((rowindex));
											if (row.getCell(6).getCellTypeEnum()==CellType.STRING) {

												monthName=row.getCell(6).getStringCellValue();
												month=months.get(monthName);
												System.out.println(month);
											}
											if (row.getCell(8).getCellTypeEnum()==CellType.STRING) {

												year=Integer.parseInt(row.getCell(8).getStringCellValue());
												System.out.println(year);
											}
										}
										if((year>=2024)||((year==2023)&&(month>=4))) {
											x=2;
										}
										else {
											x = 1;
										}
										number++;
										var MonthAndYear=	totalBillForMonthServices.findbyMonthAndYear(year, month);
										if (!(MonthAndYear.isEmpty())) {
											document.close();

											filefoundnames[filefound++]=files.getOriginalFilename()+"\n";
											continue Nextfile;

										}
										continue;
									}


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
										if(row.getCell(2).getStringCellValue().charAt(0) != '$') {

											row=	sheet2.getRow((rowindex)-1);
											totalBillForMonthdto.Name=row.getCell(0).getStringCellValue()+" "+row.getCell(1).getStringCellValue();

										}

											totalBillForMonthdto.BillAmount = row.getCell(2).getStringCellValue();

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
				 fileuploadednames[fileuploaded++]=files.getOriginalFilename()+"\n";
				System.out.println("PDF content written to DB successfully "+files.getOriginalFilename());
			 }
		 } catch (IOException e) {
					e.getStackTrace();
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body(String.format("User not Uploaded:Exception %s", ""));


	        }
		StringBuilder in=new StringBuilder();
		if(fileuploaded>0){

			for(int i=fileuploaded-1;i>=0;i--) {
				in.append(fileuploadednames[i]);
			}
			}
     	if(filefound>0){
		 StringBuilder out=new StringBuilder();
		 for(int i=filefound-1;i>=0;i--) {
			 out.append(filefoundnames[i]);
		 }

		 return ResponseEntity.status(HttpStatus.FOUND)
		 		.body(String.format("File not Uploaded: %s" ,out+ " files are  fond "+filefound+ "\n\n"+"Files Uploaded "+fileuploaded+in));

	 }

		 return ResponseEntity.status(HttpStatus.OK)
					.body(String.format("Files  Uploaded: %s", in+ " files are Ok "+fileuploaded));



	 }

	private static HashMap<String, Integer> getHashMap() {
		HashMap<String, Integer> months =new HashMap<>(24,200);
		months.put("January", 1);
		months.put("Jan", 1);
		months.put("Feb", 2);
		months.put("February", 2);
		months.put("Mar", 3);
		months.put("March", 3);
		months.put("Apr", 4);
		months.put("April", 4);
		months.put("May", 5);
		months.put("June", 6);
		months.put("Jun", 6);
		months.put("July", 7);
		months.put("Jul", 7);
		months.put("August", 8);
		months.put("Aug", 8);
		months.put("September", 9);
		months.put("Sep", 9);
		months.put("October", 10);
		months.put("Oct", 10);
		months.put("November", 11);
		months.put("Nov", 11);
		months.put("December", 12);
		months.put("Dec", 12);
		return months;
	}
}
