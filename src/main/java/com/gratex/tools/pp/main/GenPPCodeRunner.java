package com.gratex.tools.pp.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GenPPCodeRunner {

	private static final String PP_STRUCTURE = "src/main/resources/pp_structure.xlsx";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Workbook workbook = new XSSFWorkbook(new FileInputStream(PP_STRUCTURE));
		int sheets = workbook.getNumberOfSheets();

		List<Map<String, String>> data = new ArrayList<>();

		// lets loop through all the sheets
		for (int sheetNum = 0; sheetNum < sheets; sheetNum++) {
			Sheet sheet = workbook.getSheetAt(sheetNum);
			String sourceFilePrefix = sheet.getSheetName();
			System.out.println(sourceFilePrefix);

			// lets loop through all the rows
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);

				// lets loop through all the columns
				for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
					
				}

			}

		}

	}
	
}
