import graphviz.DotMaker;
import pattern.Pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	public static void main(String[] args) {
		Path root = Path.of(System.getProperty("user.dir"));
		Pattern source = Pattern.compile("%$");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);

		try {
			Files.write(root.resolve("src/main/resources/graphviz/syntaxtree.dot"),
				DotMaker.ASTtoDotString(copy.getSyntaxTree()).getBytes());

			Files.write(root.resolve("src/main/resources/graphviz/dfa.dot"),
					DotMaker.DFAtoDotString(copy.getDfa()).getBytes());

			if (copy.getNfa() != null) {
				Files.write(root.resolve("src/main/resources/graphviz/nfa.dot"),
						DotMaker.NFAtoDotString(copy.getNfa()).getBytes());
			}

			DotMaker.visualizeAll(root.resolve("src/main/resources/graphviz/"));
		} catch (RuntimeException | IOException ex) {
			System.err.println("it`s over(((");
			System.err.println(ex.getMessage());
		}

		if (copy.search("xab@a.abcx", false).isSuccess()) {
			System.out.println("goida!1!1!");
		} else {
			System.out.println("ne goida(((");
		}
		System.out.println("twice decompiled expression: \"" + copy.decompile() + "\"");
	}
}