import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reconhecedor {
	
	public static void main(String[] args) {
		if(args.length > 0) {
			FileHandler fh = new FileHandler();
			try {
				String content = fh.readFile(args[0]);
				String[] tokens = content.split(" ");
				
				List<Token> ts = new ArrayList<>();
				
				for(String string : tokens) {
					ts.add(new Token(string));
				}
				Machine m = new Machine();
				m.evaluateLeftSide(ts);
				ts = ts.subList(Machine.index, ts.size());
				m.evaluateRightSide(ts);
				fh.convert(m.getRoot());
				File currentDir = new File("");
				String[] jsCall = new String[] {"cmd.exe", "/c", "cd " + currentDir.getAbsolutePath() + "\\output" + " && node generate-tree.js"};
				new ProcessBuilder(jsCall).start();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Erro ao tentar ler o arquivo " + args[0]);
			}

		}
		
	}
}
