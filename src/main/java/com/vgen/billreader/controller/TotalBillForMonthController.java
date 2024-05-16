package com.vgen.billreader.controller;




import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;



import com.vgen.billreader.dto.TotalBillForMonthdto;


import com.vgen.billreader.model.TotalBillForMonth;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/tv/bill")
@Slf4j
public class TotalBillForMonthController {

	private final TotalBillForMonthServices totalBillForMonthServices;
	private static final Logger LOGGER = LoggerFactory.getLogger(TotalBillForMonthController.class);

	TotalBillForMonthController(TotalBillForMonthServices totalBillForMonthServices){
		this.totalBillForMonthServices=totalBillForMonthServices;
	}

	String[] phoneNumbers= {"325191128-00001","201-702-3929","330-501-4669","469-617-1147","773-575-9355","803-693-2543",
			"803-792-2439","803-992-3317","803-992-3443","803-203-9530","980-616-1500","615-487-3250","803-693-2505"};

	@GetMapping("/data")
	public ResponseEntity<?> getBills() {
		double[] totalAmount=new double[phoneNumbers.length];
		Resource totalBillFile=null;
		HashMap<Integer,String> monthName=getMonth();



		long count=1L;

		try {
			Optional<TotalBillForMonth> dataMonth= totalBillForMonthServices.findById(count);
			if(dataMonth.isEmpty()){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found");

			}

			var totalBillForMonth=dataMonth.get();
			int year=totalBillForMonth.getYear();
			int month=totalBillForMonth.getMonth();


			XSSFWorkbook workbook = new XSSFWorkbook();

			XSSFSheet sheet = workbook.createSheet("Bill content");
			for (int rowcont = 0;; rowcont++) {
				Row row = sheet.createRow(rowcont);

					var listBillForMonth = totalBillForMonthServices.findbyMonthAndYear(year, month);
					if(rowcont>0) {
						count = listBillForMonth.size() + count;
					}
				for (int cellcont = 0; cellcont < phoneNumbers.length; cellcont++) {
					if(rowcont==0) {
						Cell cell = row.createCell(cellcont);
						cell.setCellValue(phoneNumbers[cellcont]);
					}
					else if(cellcont==0) {
						Cell cell = row.createCell(cellcont);
						cell.setCellValue(monthName.get(month)+"-"+year);
					}
					else {
						String phoneNumber = phoneNumbers[cellcont];
						Cell cell = row.createCell(cellcont);
						LOGGER.info("phone number: {}", phoneNumber);
						var dataOfAmanut = listBillForMonth.stream()
								.filter((e) -> e.getMobileNumber().equals(phoneNumber)).findFirst();
							if(dataOfAmanut.isEmpty()){

								cell.setCellValue(0);
							}
							else{

								cell.setCellValue(dataOfAmanut.get().getBillAmount());
								totalAmount[cellcont]+=dataOfAmanut.get().getBillAmount();
							}
						}

					}
				dataMonth= totalBillForMonthServices.findById(count);
				if(dataMonth.isEmpty()){
					rowcont+=3;
					row = sheet.createRow(rowcont);
					for (int cellcont = 0; cellcont < phoneNumbers.length; cellcont++){
						Cell cell = row.createCell(cellcont);
						if(cellcont==0){
							cell.setCellValue("Total Amount");
						}
							else {
							cell.setCellValue(totalAmount[cellcont]);
						}
					}

						break;
				}
				totalBillForMonth=dataMonth.get();
				year=totalBillForMonth.getYear();
				month=totalBillForMonth.getMonth();
				}

			FileOutputStream outputStream = new FileOutputStream("BillAmount.xlsx");
			workbook.write(outputStream);
			Path path= Paths.get("BillAmount.xlsx");
			 totalBillFile= new UrlResource(path.toUri());


			workbook.close();

		}catch (IOException e) {
			e.getStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(totalBillFile);
		}
		String headervalue="attachment; filename=\"" + totalBillFile.getFilename() + "\"";
		String contentType="application/octet-stream";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION,headervalue).body(totalBillFile);
	}
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
			 LOGGER.info("Number of files {} ",multipartfiles.length);
			 Nextfile:
			 for (MultipartFile files : multipartfiles) {
				PDDocument document = PDDocument.load(files.getInputStream());
	            // Instantiate PDFTextStripper class
				 LOGGER.info("document of files {} ",document);
	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            for(int i=document.getNumberOfPages()-1;i>12;i--) {
	            	document.removePage(i);
	            }
				 LOGGER.info("Number of pages {} ",document.getNumberOfPages());

	            String text = pdfStripper.getText(document);
				 LOGGER.info("Number of text {} ",text);

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

	            FileOutputStream outputStream = new FileOutputStream("Data/template1.xlsx");
	            workbook.write(outputStream);


	            document.close();
				// workbook.close();
	            FileInputStream file = new FileInputStream("Data/template1.xlsx");

				XSSFWorkbook workbook2 = new XSSFWorkbook(file);

				int index = workbook2.getSheetIndex("pdf content");

				XSSFSheet sheet2 = workbook2.getSheetAt(index);

				int number=0;


				while(phoneNumbers.length>number) {

					boolean nextnumber=false;
					for(int rowindex=0;rowindex<sheet2.getLastRowNum()-1;rowindex++) {
						XSSFRow xrow=	sheet2.getRow(rowindex);

						for(int cellindex = 0;xrow.getLastCellNum()>cellindex;cellindex++) {
							XSSFCell xcell=xrow.getCell(cellindex);

							if (xcell.getCellTypeEnum() == CellType.STRING) {
								if(number==phoneNumbers.length) {
									break ;
								}
								if(xcell.getStringCellValue().equals("activity")){

									continue ;
								}

								if(xcell.getStringCellValue().equals(phoneNumbers[number])) {
									if(phoneNumbers[number].equals("325191128-00001")) {

										LOGGER.info(phoneNumbers[number]);
										if(cellindex==2) {
											XSSFRow row=	sheet2.getRow((rowindex-1));
											if (row.getCell(2).getCellTypeEnum()==CellType.STRING) {

												monthName=row.getCell(2).getStringCellValue();
												month=months.get(monthName);

											}
											if (row.getCell(4).getCellTypeEnum()==CellType.STRING) {

												year=Integer.parseInt(row.getCell(4).getStringCellValue());

											}


										}
										else  {
											XSSFRow row=	sheet2.getRow((rowindex));
											if (row.getCell(6).getCellTypeEnum()==CellType.STRING) {

												monthName=row.getCell(6).getStringCellValue();
												month=months.get(monthName);

											}
											if (row.getCell(8).getCellTypeEnum()==CellType.STRING) {

												year=Integer.parseInt(row.getCell(8).getStringCellValue());

											}
										}
										if((year>=2024)||((year==2023)&&(month>=4))) {
											x=2;
										}
										else {
											x = 1;
										}
										number++;
										var monthAndYear=	totalBillForMonthServices.findbyMonthAndYear(year, month);
										if (!(monthAndYear.isEmpty())) {
											document.close();

											filefoundnames[filefound++]="\n"+files.getOriginalFilename();
											continue Nextfile;

										}
										LOGGER.info("Year {}",year);
										LOGGER.info("Month {} ",month);
										continue;
									}


									XSSFRow row=	sheet2.getRow((rowindex)-x);
									TotalBillForMonthdto totalBillForMonthdto=new TotalBillForMonthdto();

									if (row.getCell(0).getCellTypeEnum()==CellType.STRING) {

										LOGGER.info(row.getCell(0).getStringCellValue());
									}

									if (row.getCell(1).getCellTypeEnum()==CellType.STRING) {
										LOGGER.info(row.getCell(1).getStringCellValue());
										totalBillForMonthdto.name=row.getCell(0).getStringCellValue()+" "+row.getCell(1).getStringCellValue();
									}
									if(x==2) {

									if (row.getCell(2).getCellTypeEnum()==CellType.STRING) {

										if (row.getCell(2).getStringCellValue().charAt(0) != '$') {
											if (row.getCell(2).getStringCellValue().charAt(0) != '-') {
												row = sheet2.getRow((rowindex) - 1);
												totalBillForMonthdto.name = row.getCell(0).getStringCellValue() + " " + row.getCell(1).getStringCellValue();
												totalBillForMonthdto.billAmount = Double.parseDouble(row.getCell(2).getStringCellValue().replace("$", ""));
											} else {
												totalBillForMonthdto.billAmount = Double.parseDouble(row.getCell(2).getStringCellValue().replace("-$", "-"));

											}
										} else {
											totalBillForMonthdto.billAmount = Double.parseDouble(row.getCell(2).getStringCellValue().replace("$", ""));


										}
									}
									}
									else {
										row=	sheet2.getRow((rowindex)+1);
										if (row.getCell(0).getCellTypeEnum()==CellType.STRING) {

											totalBillForMonthdto.billAmount=Double.parseDouble(row.getCell(0).getStringCellValue().replace("$",""));

										}
									}

									LOGGER.info("Amount {} ",totalBillForMonthdto.billAmount);
									totalBillForMonthdto.mobileNumber=phoneNumbers[number];
									totalBillForMonthdto.month=month;
									totalBillForMonthdto.year=year;
									totalBillForMonthServices.save(totalBillForMonthdto);
									number=number+1;
									nextnumber=true;									}
							}
						}

					}
					if(number<phoneNumbers.length) {
						if(!nextnumber){
							number=number+1;
						}

					}

				}
				file.close();
				workbook.close();
				 fileuploadednames[fileuploaded++]="\n"+files.getOriginalFilename();
				 LOGGER.debug("PDF content written to DB successfully {} ",files.getOriginalFilename());
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
		 		.body(String.format("File not Uploaded: %s" ,out+ " files are  fond "+filefound+ "\n\n"+"Files Uploaded \n"+fileuploaded+in));

	 }

		 return ResponseEntity.status(HttpStatus.OK)
					.body(String.format("Files  Uploaded: %s", in+ "\n files are Ok "+fileuploaded));



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
	private static HashMap<Integer,String> getMonth() {
		HashMap<Integer,String> months =new HashMap<>(12,12);
		months.put(1,"January");


		months.put(2,"February");

		months.put(3,"March");

		months.put(4,"April");
		months.put(5,"May");
		months.put(6,"June");

		months.put(7,"July");

		months.put(8,"August");

		months.put(9,"September");

		months.put(10,"October");

		months.put(11,"November");

		months.put(12,"December");

		return months;
	}


}
