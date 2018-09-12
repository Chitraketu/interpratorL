package hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
/**
 * 
 * @author chitraketu
 *
 */
public class Main {
	public static void main(String[] args) throws Exception {
		for (String s : args) {
			File f = new File(s);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readline = "";
			int lineNumber = 1;
			try {
				while ((readline = b.readLine()) != null) {
					if (readline.isEmpty())
						continue;
					String[] tokens = Helper.tokenize(readline);
					Helper.executeLine(tokens);
					lineNumber++;
				}
				b.close();
			} catch (Throwable e) {
				System.out.println("RUNTIME ERROR: line " + lineNumber);
			}
		}
	}
}