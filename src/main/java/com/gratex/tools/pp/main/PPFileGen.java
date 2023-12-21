package com.gratex.tools.pp.main;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PPFileGen extends CommonGen {

	// poslat sem rovno prefix
	public static String generatePPFileClass(List<Map<String, Object>> structure, String ppFileExtension, String coreClassName, String extendsFrom) {
		// do not take core class name and extends

		String prefix = ppFileExtension.toUpperCase();
		List<String> instanceVariables = getInstanceVariables(prefix);		

		return new StringBuilder()
			.append(declarePackage(ppFileExtension))
			.append(declareCustomImports(prefix))
			.append(declareClassWithParentImport(prefix, coreClassName, extendsFrom))
			.append(declareInstanceVariables(ppFileExtension, instanceVariables))
			.append(declareConstructor(prefix, coreClassName, instanceVariables))
			.append(getAsMethod("get", "header", prefix + "Header"))
			.append(declareGetBodyMethod(prefix + "Record", "body"))
			.append(getAsMethod("get", "footer", prefix + "Footer"))
			.append(declareGetFileType(prefix))
			.append(declareBuildFileString())
			.append(declareAppendHelper(prefix, structure, 1, "header"))
			.append(declareAppendHelper(prefix, structure, 2, "body"))
			.append(declareAppendHelper(prefix, structure, 3, "footer"))
			.append("}")
			.toString();
	}

	private static String declareCustomImports(String prefix) {
		return new StringBuilder()
			.append("import static com.gratex.tools.pp.core.FileType.").append(prefix).append(";\n")
			.append("import static java.util.Collections.emptyList;\n")
			.append("import static java.util.Objects.nonNull;\n\n")
			.append("import java.util.ArrayList;\n")
			.append("import java.util.List;\n\n")
			.append("import com.gratex.tools.pp.core.FileType;\n")
			.toString();
	}

	private static String declareInstanceVariables(String ppFileExtension, List<String> variables) {
		return new StringBuilder()
			.append("\tprivate ").append(variables.get(0)).append(";\n")
			.append("\tprivate ").append(variables.get(1)).append(";\n")
			.append("\tprivate ").append(variables.get(2)).append(";\n\n")
			.toString();
	}

	private static String declareConstructor(String prefix, String coreClassName, List<String> variables) {
		return new StringBuilder()
			.append("\tpublic ").append(prefix).append(coreClassName).append("(").append(String.join(", ", variables)).append(") {\n")
			.append("\t\tthis.header = header;\n")
			.append("\t\tthis.body = body;\n")
			.append("\t\tthis.footer = footer;\n\t}\n\n")
			.toString();
	}

	private static List<String> getInstanceVariables(String prefix) {
		return Arrays.asList(
				prefix + "Header header",
				"List<" + prefix + "Record> body",
				prefix + "Footer footer");
	}

	private static String declareGetBodyMethod(String typeName, String variableName) {
		String adjustedVarName = Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
		return new StringBuilder()
			.append("\tpublic List<").append(typeName).append("> get").append(adjustedVarName).append("() {\n")
		    .append("\t\treturn nonNull(").append(variableName).append(") && !").append(variableName).append(".isEmpty()\n")
		    .append("\t\t\t? new ArrayList<>(").append(variableName).append(")\n")
		    .append("\t\t\t: emptyList();\n\t}\n\n")
			.toString();
	}

	private static String declareGetFileType(String prefix) {
		return new StringBuilder("\t@Override\n")
			.append(getAsMethod("get", prefix, "FileType"))
			.toString().replace("get" + prefix, "getFileType");
	}

	private static String declareBuildFileString() {
		return new StringBuilder("\t@Override\n")
			.append("\tprotected final String buildFileString() {\n")
			.append("\t\tStringBuilder fileContent = new StringBuilder();\n")
			.append("\t\tappendHeader(fileContent);\n")
			.append("\t\tappendBody(fileContent);\n")
			.append("\t\tappendFooter(fileContent);\n")
			.append("\t\treturn fileContent.toString();\n\t}\n\n")
			.toString();
	}

	private static String declareAppendHelper(String prefix, List<Map<String, Object>> structure, int sentence, String instanceVariableName) {
		List<Map<String, Object>> orderedStructure = getOrderedProperties(structure, sentence);
		if (sentence == 2) {
			return declareAppendHelperForBody(prefix, orderedStructure, instanceVariableName);
		} else {
			List<String> getterCalls = getGetterCalls(instanceVariableName, orderedStructure);
			StringBuilder appendHelperMethod =  new StringBuilder("\tprivate void append").append(sentence == 1 ? "Header" : "Footer").append("(StringBuilder fileContent) {\n")
				.append("\t\tfileContent.append(").append(getterCalls.remove(0)).append(")\n");

			getterCalls.forEach(call -> appendHelperMethod.append("\t\t           .append(").append(call).append(")\n"));
			appendHelperMethod.deleteCharAt(appendHelperMethod.length() - 1);
			return appendHelperMethod
				.append(sentence == 3 ? ";\n" : "\n\t\t           .append(\"\\r\\n\");\n")
				.append("\t}\n\n")
				.toString();
		}
	}

	protected static List<String> getGetterCalls(String instanceVariableName, List<Map<String, Object>> properties) {
		List<String> variables = properties.stream()
			.map(e -> (String) e.get("nazovPremennej"))
			.map(e -> instanceVariableName + ".get" + withFirstLetterInUpperCase(e) + "()")
			.collect(toList());

		return variables;
	}

	private static String declareAppendHelperForBody(String prefix, List<Map<String, Object>> orderedStructure, String instanceVariableName) {
		List<String> getterCalls = getGetterCalls("bodyLine", orderedStructure);
		StringBuilder appendHelperMethod =  new StringBuilder("\tprivate void appendBody(StringBuilder content) {\n")
				.append("\t\tfor (").append(prefix).append("Record ").append("bodyLine : ").append(instanceVariableName).append(") {\n")
				.append("\t\t\tcontent.append(").append(getterCalls.remove(0)).append(")\n");

			getterCalls.forEach(call -> appendHelperMethod.append("\t\t\t       .append(").append(call).append(")\n"));

			return appendHelperMethod
				.append("\t\t\t       .append(\"\\r\\n\");\n")
				.append("\t\t}\n")
				.append("\t}\n\n")
				.toString();
	}

}
