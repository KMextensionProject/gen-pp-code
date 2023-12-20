package com.gratex.tools.pp.main;

import static com.gratex.tools.pp.main.PPFileGen.generatePPFileClass;
import static com.gratex.tools.pp.main.PPParserGen.generatePPParserClass;
import static com.gratex.tools.pp.main.PPPartGen.generatePPModelClass;

import java.util.List;
import java.util.Map;

public class GenPPCodeRunner {

	public static void main(String[] args) throws Exception {

		for (Map.Entry<String, List<Map<String, Object>>> structure : StructureLoader.loadPPStructure().entrySet()) {

			// key   = schema name as file extension and class file prefix
			// value = PP structure and meta data
			String headerClass = generatePPModelClass(structure.getValue(), structure.getKey(), 1, "Header", "PPHeader"); // uvodna veta
			String recordClass = generatePPModelClass(structure.getValue(), structure.getKey(), 2, "Record", "PPPart"); // datova veta
			String footerClass = generatePPModelClass(structure.getValue(), structure.getKey(), 3, "Footer", "PPPart"); // koncova veta

			System.out.println(headerClass);
			System.out.println(recordClass);
			System.out.println(footerClass);

			// use these names to generate PPFile properties
			String ppFileClass = generatePPFileClass(structure.getValue(), structure.getKey(), "File", "PPFile");
			String ppParserClass = generatePPParserClass(structure.getValue(), structure.getKey());

			System.out.println(ppFileClass);
			System.out.println(ppParserClass);
		}
	}
}
