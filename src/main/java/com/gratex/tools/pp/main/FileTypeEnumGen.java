package com.gratex.tools.pp.main;

import java.util.Collection;

public class FileTypeEnumGen extends CommonGen {

	public static String genFileType(Collection<String> ppFileExtensions) {
		StringBuilder fileTypeCode = new StringBuilder()
			.append("package com.gratex.tools.pp.core;\n\n")
			.append("import static java.util.Objects.nonNull;\n\n")
			.append("import java.nio.file.Path;\n\n")
			.append(commentGenerated())
			.append("public enum FileType {\n\n");
		
		ppFileExtensions.forEach(ext -> fileTypeCode.append("\t").append(ext.toUpperCase()).append("(\".").append(ext).append("\"),\n"));
		fileTypeCode.delete(fileTypeCode.length() - 2, fileTypeCode.length()).append(";\n\n");
		
		return fileTypeCode
			.append("\tprivate final String extension;\n\n")
			.append("\tprivate FileType(String extension) {\n")
			.append("\t\tthis.extension = extension;\n")
			.append("\t}\n\n")
			.append("\tpublic String getFileExtension() {\n")
			.append("\t\treturn this.extension;\n")
			.append("\t}\n\n")
			.append("\tpublic boolean isExtensionOn(Path path) {\n")
			.append("\t\tif (nonNull(path)) {\n")
			.append("\t\t\tString file = String.valueOf(path.getFileName());\n")
			.append("\t\t\treturn file.endsWith(extension);\n")
			.append("\t\t}\n")
			.append("\t\treturn false;\n")
			.append("\t}\n\n")
			.append("}\n")
			.toString();
	}

}
