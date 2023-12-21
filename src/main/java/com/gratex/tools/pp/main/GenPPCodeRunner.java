package com.gratex.tools.pp.main;

import static com.gratex.tools.pp.main.PPFileGen.generatePPFileClass;
import static com.gratex.tools.pp.main.PPParserGen.generatePPParserClass;
import static com.gratex.tools.pp.main.PPPartGen.generatePPModelClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GenPPCodeRunner {

	public static void main(String[] args) throws Exception {

		try {
			// subor tychto class ulozit do noveho foldra v
			// projekt/src/main/java/...vytvorit priecinky ak neexistuju a
			// prepisat subory co tam su
			System.out.println("starting generation...");

			Map<String, List<Map<String, Object>>> structures = StructureLoader.loadPPStructure();
			for (Map.Entry<String, List<Map<String, Object>>> structure : structures.entrySet()) {

				// key = schema name as file extension and class file prefix
				String fileExtension = structure.getKey().toLowerCase();
				String classPrefix = fileExtension.toUpperCase();
				List<Map<String, Object>> meta = structure.getValue();

				String headerClass = generatePPModelClass(meta, fileExtension, 1, "Header", "PPHeader"); // uvodna
				String recordClass = generatePPModelClass(meta, fileExtension, 2, "Record", "PPPart"); // datova
				String footerClass = generatePPModelClass(meta, fileExtension, 3, "Footer", "PPPart"); // koncova
				String ppFileClass = generatePPFileClass(structure.getValue(), structure.getKey(), "File", "PPFile");
				String ppParserClass = generatePPParserClass(structure.getValue(), structure.getKey());

				// this will lie in its own pp-codegen folder
				Path outputLocationPath = Paths.get("../gen-pp", "src/main/java/com/gratex/tools/pp/io/" + fileExtension.toLowerCase()).toAbsolutePath().normalize();
				Files.createDirectories(outputLocationPath);
				String outputLocation = outputLocationPath.toString();

				saveFile(outputLocation, headerClass, classPrefix, "Header.java");
				saveFile(outputLocation, recordClass, classPrefix, "Record.java");
				saveFile(outputLocation, footerClass, classPrefix, "Footer.java");
				saveFile(outputLocation, ppFileClass, classPrefix, "File.java");
				saveFile(outputLocation, ppParserClass, classPrefix, "Parser.java");

			}
			String enumFile = FileTypeEnumGen.genFileType(structures.keySet());
			String outputLocation = Paths.get("../gen-pp", "src/main/java/com/gratex/tools/pp/core").toAbsolutePath().normalize().toString();
			saveFile(outputLocation, enumFile, "", "FileType.java");
			System.out.println("Finished");
		} catch (Exception ex) {
			System.out.println("[ERROR] " + ex.getStackTrace()[1]);
		}
	}

	private static void saveFile(String outputLocation, String sourceCode, String prefix, String ending) throws IOException {
		Path processing = Paths.get(outputLocation, prefix + ending);
		Files.write(processing, sourceCode.getBytes());
		System.out.println(processing);
	}
}
