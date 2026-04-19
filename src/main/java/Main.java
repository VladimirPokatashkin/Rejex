import graphviz.DotMaker;
import pattern.Pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	static void main() {
		Path root = Path.of(System.getProperty("user.dir"));
		Pattern pattern = Pattern.compile("(a/b)c");

		try {
			Files.write(root.resolve("src/main/resources/graphviz/nfa.dot"),
				DotMaker.NFAtoDotString(pattern.getNfa()).getBytes());
		} catch (IOException ex) {
			System.err.println("it`s over(((");
		}

		if (pattern.matches("ac")) {
			System.out.println("goida!1!1!");
		} else {
			System.out.println("ne goida(((");
		}
	}
}