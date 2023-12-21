package com.gratex.tools.pp.main;

import java.util.List;
import java.util.Map;

public class PPParserGen extends CommonGen {

	public static String generatePPParserClass(List<Map<String, Object>> structure, String ppFileExtension) {

		String prefix = ppFileExtension.toUpperCase();
		StringBuilder parserCode = new StringBuilder()
			.append(declarePackage(ppFileExtension))
			.append(declareCustomImports())
			.append(declareClassWithParentImport(prefix, "Parser", "FileParser"))
			.append(declareOverridenMethods(prefix, structure))
			.append(appendValidateRemainingContent())
			.append("}\n");

		return parserCode.toString();
	}

	private static String declareCustomImports() {
		return new StringBuilder()
			.append("import static java.util.stream.Collectors.toList;\n\n")
			.append("import java.io.IOException;\n")
			.append("import java.nio.charset.Charset;\n")
			.append("import java.nio.file.Files;\n")
			.append("import java.nio.file.Path;\n")
			.append("import java.util.List;\n\n")
			.append("import com.gratex.tools.pp.utils.CharSequenceIterator;\n")
			.append("import com.gratex.tools.pp.core.DataIntegrityViolation;\n")
			.toString();
	}
	
	private static String declareOverridenMethods(String prefix, List<Map<String, Object>> structure) {
		String ppFile = prefix + "File";
		String ppHeader = prefix + "Header";
		String ppBody = prefix + "Record";
		String ppFooter = prefix + "Footer";
		String validationCall = "validateRemainingContent(sequencer);";

		StringBuilder parseMethod = new StringBuilder("\t@Override\n")
			.append("\tpublic ").append(ppFile).append(" parse(Path source, Charset encoding) throws IOException {\n")
			.append("\t\tList<String> lines = Files.readAllLines(source, encoding);\n")
			.append("\t\tlines.removeIf(e -> e.trim().isEmpty());\n\n")
			.append("\t\ttry {\n")
			.append("\t\t\t").append(ppHeader).append(" header = parseHeader(lines.remove(0));\n")
			.append("\t\t\t").append(ppFooter).append(" footer = parseFooter(lines.remove(lines.size() - 1));\n")
			.append("\t\t\tList<").append(ppBody).append("> body = parseBody(lines);\n")
			.append("\t\t\treturn new ").append(ppFile).append("(header, body, footer);\n\n")
			.append("\t\t} catch (DataIntegrityViolation div) {\n")
			.append("\t\t\tthrow new IOException(\"data integrity violation\", div);\n")
			.append("\t\t}\n")
			.append("\t}\n\n");

		StringBuilder parseHeader = new StringBuilder()
			.append("\t").append(ppHeader).append(" parseHeader(String line) {\n")
			.append("\t\tCharSequenceIterator sequencer = new CharSequenceIterator(line);\n\n")
			.append("\t\t").append(ppHeader).append(" header = new ").append(ppHeader).append("();\n");

		getOrderedProperties(structure, 1).forEach(headerMeta -> parseHeader.append("\t\t").append(getSetter(headerMeta, "header")).append(";\n"));
		parseHeader.append("\n\t\t").append(validationCall).append("\n")
			.append("\t\treturn header;\n")
			.append("\t}\n\n");

		StringBuilder parseBody = new StringBuilder()
			.append("\tList<").append(ppBody).append("> parseBody(List<String> lines) {\n")
			.append("\t\treturn lines.stream()\n")
			.append("\t\t.map(this::parseRecord)\n")
			.append("\t\t.collect(toList());\n")
			.append("\t}\n\n");

		StringBuilder parseRecord = new StringBuilder()
			.append("\tprivate ").append(ppBody).append(" parseRecord(String line) {\n")
			.append("\t\tCharSequenceIterator sequencer = new CharSequenceIterator(line);\n\n")
			.append("\t\t").append(ppBody).append(" bodyLine = new ").append(ppBody).append("();\n");

		getOrderedProperties(structure, 2).forEach(bodyMeta -> parseRecord.append("\t\t").append(getSetter(bodyMeta, "bodyLine")).append(";\n"));
		if ("PPE".equalsIgnoreCase(prefix)) {
			parseRecord.append("\n\t\t// commented-out on purpose until we find out why .ppe data sentences\n")
				.append("\t\t// differ in length in comparison to currently valid documentation\n")
				.append("\t\t// validateRemainingContent(sequencer);\n");
		} else {
			parseRecord.append("\n\t\t").append(validationCall).append("\n");
		}
		parseRecord.append("\t\treturn bodyLine;\n")
			.append("\t}\n\n");

		StringBuilder parseFooter = new StringBuilder()
			.append("\t").append(ppFooter).append(" parseFooter(String line) {\n")
			.append("\t\tCharSequenceIterator sequencer = new CharSequenceIterator(line);\n\n")
			.append("\t\t").append(ppFooter).append(" footer = new ").append(ppFooter).append("();\n");

		getOrderedProperties(structure, 3).forEach(footerMeta -> parseFooter.append("\t\t").append(getSetter(footerMeta, "footer")).append(";\n"));
		parseFooter.append("\n\t\t").append(validationCall).append("\n")
			.append("\t\treturn footer;\n")
			.append("\t}\n\n");

		return parseMethod.append(parseHeader)
			.append(parseBody)
			.append(parseRecord)
			.append(parseFooter)
			.toString();
	}

	private static String getSetter(Map<String, Object> meta, String part) {
		return new StringBuilder(part)
			.append(".set")
			.append(withFirstLetterInUpperCase((String)meta.get("nazovPremennej"))).append("(")
			.append("sequencer.next(").append(meta.get("dlzka")).append("))")
			.toString();
	}

	private static String appendValidateRemainingContent() {
		return new StringBuilder()
			.append("\tprivate void validateRemainingContent(CharSequenceIterator sequencer) {\n")
			.append("\t\tif (sequencer.hasMore()) {\n")
			.append("\t\t\tthrow new DataIntegrityViolation(\"Unexpected unparsable content\");\n")
			.append("\t\t}\n")
			.append("\t}\n\n")
			.toString();
	}

}
