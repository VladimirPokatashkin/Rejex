package pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DecompilerTest {
	@Test
	void emptyStringOnlyTest() {
		Pattern source = Pattern.compile("$");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void singleLiteralOnlyTest() {
		Pattern source = Pattern.compile("j");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void starOperatorTest() {
		Pattern source = Pattern.compile("j*");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void concatenationTest() {
		Pattern source = Pattern.compile("java");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void choiceWithStarTest() {
		Pattern source = Pattern.compile("a|b*");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void complexTest() {
		Pattern source = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void shieldingTest() {
		Pattern source = Pattern.compile("%[java%]%{8%}%$%%%.%|");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}

	@Test
	void lookaheadOperatorTest() {
		Pattern source = Pattern.compile("a/b");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));

		source = Pattern.compile("[a-z]/[123]");
		decompiled = source.decompile();
		copy = Pattern.compile(decompiled);
		assertTrue(source.getDfa().isIsomorphicTo(copy.getDfa()));
	}
}