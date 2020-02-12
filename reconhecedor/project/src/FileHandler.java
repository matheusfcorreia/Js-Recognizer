import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileHandler {
	public String readFile(String path) throws IOException {
		if(isJSFile(path)) {
			File inputFile = new File(path);
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				br.lines().forEach(sb::append);
				br.close();
				return sb.toString();
			} catch (FileNotFoundException e) {
				System.out.println("Arquivo não encontrado.");
				return null;
			}
		}
		System.out.println("O arquivo não é um script JavaScript.");
		return null;
	}
	
	public void convert(TreeObject to) {
		try {
			File newFolder = new File("output");
			newFolder.mkdir();
			File json = new File(newFolder.getAbsolutePath() + "/tree.json");
			json.createNewFile();
			ObjectMapper om = new ObjectMapper();
			om.writeValue(json, to);
		} catch (IOException e) {
		e.printStackTrace();	
		}
	}

	private boolean isJSFile(String path) {
		if(path.toLowerCase().endsWith(".js")) {
			return true;
		}
		return false;
	}
}
