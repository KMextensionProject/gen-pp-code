package com.gratex.tools.pp.main;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommonGen {

	protected static String declarePackage(String ppFileExtension) {
		return "package com.gratex.tools.pp.io." + ppFileExtension.toLowerCase() + ";\n\n";
	}

	protected static String declareClassWithParentImport(String prefix, String className, String extendsFrom) {
		return new StringBuilder("import com.gratex.tools.pp.core.").append(extendsFrom).append(";\n\n")
			.append("public class ").append(prefix).append(className).append(" extends ").append(extendsFrom).append("FileParser".equals(extendsFrom) ? "<" + prefix + "Parser>" : "").append(" {\n\n")
			.toString();
	}

	protected static String declareGetAndSet(List<String> variableNames, String typeName) {
		StringBuilder methods = new StringBuilder("\n");
		for (String variableName : variableNames) {
			methods.append(getAsMethod("set", variableName, typeName))
			       .append(getAsMethod("get", variableName, typeName));
		}
		return methods.toString();
	}

	protected static String getAsMethod(String kind, String variableName, String typeName) {
		String adjustedVarName = withFirstLetterInUpperCase(variableName);
		StringBuilder method = new StringBuilder("\t");
		switch(kind) {
		case "set":
			method.append("public void set").append(adjustedVarName).append("(").append(typeName).append(" ").append(variableName).append(") {\n")
			      .append("\t\tthis.").append(variableName).append(" = ").append(variableName).append(";\n\t}\n\n");
			break;
		default :
			method.append("public ").append(typeName).append(" get").append(adjustedVarName).append("() {\n")
		      .append("\t\treturn ").append(variableName).append(";\n\t}\n\n");
		}
		return method.toString();
	}

	protected static List<String> getUserDefinedVariableNames(List<Map<String, Object>> properties) {
		List<String> variables = properties.stream()
			.map(e -> (String) e.get("nazovPremennej"))
			.collect(toList());
		return variables;
	}

	protected static List<Map<String, Object>> getOrderedProperties(List<Map<String, Object>> structure, int sentenceNumber) {
		return structure.stream()
			.filter(e -> Integer.valueOf(sentenceNumber).equals(e.get("veta")))
			.sorted(Comparator.comparing(e -> (Integer) e.get("cislo")))
			.collect(toList());
	}

	protected static String withFirstLetterInUpperCase(String text) {
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}
	
}
