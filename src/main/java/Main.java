import graphviz.DotMaker;
import pattern.Pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	public static void main(String[] args) {
		Path root = Path.of(System.getProperty("user.dir"));
		Pattern pattern = Pattern.compile("(ab)*");

		try {
			Files.write(root.resolve("src/main/resources/graphviz/syntaxtree.dot"),
				DotMaker.ASTtoDotString(pattern.getSyntaxTree()).getBytes());
			Files.write(root.resolve("src/main/resources/graphviz/nfa.dot"),
				DotMaker.NFAtoDotString(pattern.getNfa()).getBytes());
		} catch (IOException ex) {
			System.err.println("it`s over(((");
		}

		if (pattern.search("xabx", false).isSuccess()) {
			System.out.println("goida!1!1!");
		} else {
			System.out.println("ne goida(((");
		}
	}
}