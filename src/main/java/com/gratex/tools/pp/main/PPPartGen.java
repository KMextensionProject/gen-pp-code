package com.gratex.tools.pp.main;

import java.util.List;
import java.util.Map;

public class PPPartGen extends CommonGen {

	public static String generatePPModelClass(List<Map<String, Object>> structure, String ppFileExtension, int sentenceNumber, String coreClassName, String extendsFrom) {
		// resolve class name and hierarchy based on sentence number
		List<Map<String, Object>> headerProperties = getOrderedProperties(structure, sentenceNumber);
		List<String> variableNames = getUserDefinedVariableNames(headerProperties);
		variableNames.remove("code");
		variableNames.remove("serialNumberIn12M");

		StringBuilder code = new StringBuilder()
			.append(declarePackage(ppFileExtension))
			.append(declareClassWithParentImport(ppFileExtension.toUpperCase(), coreClassName, extendsFrom))
			.append(declareStringVariables(variableNames))
			.append(declareGetAndSet(variableNames, "String")) // types are all java.lang.String
			.append("}"); // end the source file

		return code.toString();
	}

	private static String declareStringVariables(List<String> variableNames) {
		StringBuilder variableDeclarations = new StringBuilder();
		variableNames.forEach(varName -> variableDeclarations.append("\tprivate String ").append(varName).append(";\n"));
		return variableDeclarations.toString();
	}

}
