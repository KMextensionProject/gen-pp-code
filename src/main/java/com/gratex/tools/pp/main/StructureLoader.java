package com.gratex.tools.pp.main;

import static com.gratex.tools.pp.main.XlsxUtils.getCellValue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gratex.tools.pp.main.XlsxUtils.CellValue;

public class StructureLoader {
	private static final String PP_STRUCTURE = "src/main/resources/pp_structure.xlsx";

	public static Map<String, List<Map<String, Object>>> loadPPStructure() throws FileNotFoundException, IOException {
		Workbook workbook = new XSSFWorkbook(new FileInputStream(PP_STRUCTURE));
		int sheets = workbook.getNumberOfSheets();

		Map<String, List<Map<String, Object>>> structures = new HashMap<>();

		// lets loop through all the sheets
		for (int sheetNum = 0; sheetNum < sheets; sheetNum++) {
			Sheet sheet = workbook.getSheetAt(sheetNum);

			// load to some list once, with cammelCase names
			Row titleRow = sheet.getRow(sheet.getFirstRowNum());
			List<String> columnNames = readTitleColumns(titleRow);

			List<Map<String, Object>> structure = new ArrayList<>();
			// lets loop through all the rows
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row dataRow = sheet.getRow(rowNum);
				Map<String, Object> data = new HashMap<>();

				// lets loop through all the columns
				for (int cellNum = 0; cellNum < 6; cellNum++) {
					CellValue cellValue = getCellValue(dataRow.getCell(cellNum));
					String columnName = columnNames.get(cellNum);
					data.put(columnNames.get(cellNum), getTypedValueByName(columnName, cellValue));
				}
				structure.add(data);
			}
			structures.put(sheet.getSheetName(), structure);
		}
		structures.forEach((a, b)-> System.out.println(a + "=" + b));
		workbook.close();
		return structures;
	}

	/**
	 * @param titleRow
	 * @return list cammelCase column names of this titleRow
	 */
	private static List<String> readTitleColumns(Row titleRow) {
		List<String> columnNames = new ArrayList<>();
		titleRow.forEach(cell -> columnNames.add(cammelize(getCellValue(cell).asString())));
		return columnNames;
	}

	private static String cammelize(String input) {
		List<String> nameParts = Arrays.stream(input.split(" "))
			.filter(e -> !e.trim().isEmpty())
			.filter(e -> e.indexOf('-') < 0)
			.map(String::toLowerCase)
			.map(StructureLoader::stripDiacritics)
			.collect(Collectors.toList());

		StringBuilder cammelized = new StringBuilder(nameParts.remove(0));
		cammelized.append(nameParts.stream()
				.map(StructureLoader::capitalizeFirstLetter)
				.collect(Collectors.joining())
		);
		return cammelized.toString();
	}

	private static String stripDiacritics(String diacritics) {
		return Normalizer.normalize(diacritics, Form.NFD);
	}

	private static String capitalizeFirstLetter(String input) {
		char firstLetter = input.charAt(0);
		return Character.toUpperCase(firstLetter) + input.substring(1);
	}

	private static Object getTypedValueByName(String columnName, CellValue cellValue) {
		switch (columnName) {
		case "cislo":
		case "veta":
		case "dlzka":
			return cellValue.asInt();
		default:
			return cellValue.asString();
		}
	}
}
