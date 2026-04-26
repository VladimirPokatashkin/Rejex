package pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DecompilerTest {
	@Test
	void emptyStringOnlyTest() {
		Pattern source = Pattern.compile("$");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertEquals(copy.search("").isSuccess(), source.search("").isSuccess());
	}

	@Test
	void singleLiteralOnlyTest() {
		Pattern source = Pattern.compile("j");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertEquals(copy.search("j").isSuccess(), source.search("jjjj").isSuccess());
	}

	@Test
	void starOperatorTest() {
		Pattern source = Pattern.compile("j*");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(copy.search("abobajjjjhghghghhg").isSuccess());
		assertTrue(copy.search("hghghghg").isSuccess());
	}

	@Test
	void concatenationTest() {
		Pattern source = Pattern.compile("java");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertEquals(copy.search("aaaaaajavabbbbb").begin(), source.search("aaaaaajavabbbbb").begin());
	}

	@Test
	void choiceWithStarTest() {
		Pattern source = Pattern.compile("a|b*");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(copy.search("java").isSuccess());
		assertTrue(copy.search("jjjbbbb").isSuccess());
	}

	@Test
	void complexTest() {
		Pattern source = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(copy.search("gavi@barca.com").isSuccess());
		assertFalse(copy.search("aboba.ru").isSuccess());
		assertFalse(copy.search("0123@hghgh.ififi").isSuccess());
	}

	@Test
	void shieldingTest() {
		Pattern source = Pattern.compile("%[java%]%{8%}%$%%%.%|");
		String decompiled = source.decompile();
		Pattern copy = Pattern.compile(decompiled);
		assertTrue(copy.search("aboba[java]{8}$%.|aboba").isSuccess());
	}
}