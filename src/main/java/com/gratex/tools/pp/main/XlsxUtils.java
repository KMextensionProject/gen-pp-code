package com.gratex.tools.pp.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class XlsxUtils {

	private XlsxUtils() {
		throw new IllegalStateException("This utility class was not designed to be instantiated");
	}

	public static CellValue getCellValue(Cell cell) {
		Object cellValue;
		int cellType = cell.getCellType();
		if (cellType == Cell.CELL_TYPE_FORMULA) {
			cellType = cell.getCachedFormulaResultType();
		}
		switch (cellType) {
		case Cell.CELL_TYPE_BLANK:
			cellValue = null;
			break;
		case Cell.CELL_TYPE_STRING:
			cellValue = cell.getRichStringCellValue()
				.getString()
				.replace("\n", "")
				.trim();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
                cellValue = cell.getDateCellValue();
            } else {
                cellValue = cell.getNumericCellValue();
            }
			break;
		case Cell.CELL_TYPE_ERROR:
			cellValue = cell.getErrorCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		default: 
			cellValue = null;
		}
		return new CellValue(cellValue);
	}

	public static class CellValue {

		private Object cellContent;
		private boolean isUndefined;
		private boolean isNull;
		private boolean isDate;
		private boolean isNumeric;

		public CellValue(Object value) {
			if (value == null) {
				this.isNull = true;
			}
			// toto ma byt else if + check ci to neni empty
			if ("N/A".equals(value)) {
				this.isUndefined = true;
			}
			if (value instanceof java.util.Date) {
				this.isDate = true;
			}
			if (value instanceof java.lang.Number) {
				this.isNumeric = true;
			}
			cellContent = value;
		}

		public boolean isNull() {
			return this.isNull;
		}

		public boolean isUndefined() {
			return this.isUndefined;
		}

		public boolean isDate() {
			return isDate;
		}

		public boolean isNumeric() {
			return isNumeric;
		}

		public String asString() {
			return String.valueOf(cellContent);
		}

		public int asInt() {
			if (cellContent instanceof String) {
				return Double.valueOf((String) cellContent).intValue();
			} else if (cellContent instanceof Number) {
				return ((Number) cellContent).intValue();
			}
			throw new UnsupportedOperationException(cellContent + " cannot be converted to int");
		}

		/**
		 * It's enough to provide mapping function to alter original double
		 * value. This method automatically extracts the integer value throwing
		 * away all fractional digits.
		 */
		public int asInt(DoubleUnaryOperator formattingFunction) {
			return (int) formattingFunction.applyAsDouble((new CellValue(cellContent).asDouble()));
		}

		public double asDouble() {
			if (cellContent instanceof String) {
				return Double.valueOf((String) cellContent).intValue();
			} else if (cellContent instanceof Number) {
				return ((Number) cellContent).doubleValue();
			}
			throw new UnsupportedOperationException(cellContent + " cannot be converted to double");
		}

		public LocalDate asLocalDate() {
			return asLocalDate(null);
		}

		public LocalDate asLocalDate(DateTimeFormatter format) {
			try {
				if (cellContent instanceof String) {
					String dateValue = (String) cellContent;
					if (format != null) {
						return LocalDate.parse(dateValue, format);
					}
					return LocalDate.parse(dateValue);
				} else if (cellContent instanceof java.util.Date) {
					return new java.sql.Date(((java.util.Date) cellContent).getTime()).toLocalDate();
				}
			} catch (DateTimeParseException parsingError) {
				// parsingError skipped in favor of UnsupportedOperationException
			}
			throw new UnsupportedOperationException(cellContent + " cannot be converted to LocalDate");
		}

		public Object asObject() {
			return this.cellContent;
		}

		public <T> T conditionally(Predicate<T> condition, T trueValue, T falseValue) {
			if (condition == null) {
				throw new IllegalArgumentException("Condition can not be null");
			}
			return condition.test(trueValue) ? trueValue : falseValue;
		}

		public <T, R> R customized(Function<T, R> mappingFunction) { 
			return mappingFunction.apply(null);
		}
	}
}
