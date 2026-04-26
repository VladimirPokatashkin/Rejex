package pattern;

import matcher.SearchResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {
	@Test
	void intersectionAAndATest() {
		Pattern all = Pattern.compile("[a-z]");
		Pattern a = Pattern.compile("[a-j]");
		Pattern inter = all.intersection(a);

		SearchResult result = inter.search("a", false);
		assertTrue(result.isSuccess());

		result = inter.search("j", false);
		assertTrue(result.isSuccess());

		result = inter.search("q", false);
		assertFalse(result.isSuccess());
	}

	@Test
	void intersectionVowelsTest() {
		Pattern letters = Pattern.compile("[a-z]");
		Pattern vowels = Pattern.compile("[aeiou]");
		Pattern inter = letters.intersection(vowels);

		assertTrue(inter.search("a", false).isSuccess());
		assertTrue(inter.search("e", false).isSuccess());
		assertTrue(inter.search("i", false).isSuccess());

		assertFalse(inter.search("j", false).isSuccess());
		assertFalse(inter.search("v", false).isSuccess());
		assertFalse(inter.search("m", false).isSuccess());
	}

	@Test
	void intersectionZeroAndOneTest() {
		Pattern digits = Pattern.compile("[0-9]");
		Pattern zeroOne = Pattern.compile("[01]");
		Pattern inter = digits.intersection(zeroOne);

		assertTrue(inter.search("0", false).isSuccess());
		assertTrue(inter.search("1", false).isSuccess());

		assertFalse(inter.search("2", false).isSuccess());
		assertFalse(inter.search("5", false).isSuccess());
		assertFalse(inter.search("9", false).isSuccess());
	}

	@Test
	void intersectionAbcTest() {
		Pattern threeLetters = Pattern.compile("[a-z][a-z][a-z]");
		Pattern abc = Pattern.compile("abc");
		Pattern inter = threeLetters.intersection(abc);

		assertTrue(inter.search("abc", false).isSuccess());

		assertFalse(inter.search("abd", false).isSuccess());
		assertFalse(inter.search("xyz", false).isSuccess());
		assertFalse(inter.search("aaa", false).isSuccess());
		assertFalse(inter.search("jvm", false).isSuccess());
	}

	@Test
	void intersectionAStarAndAATest() {
		Pattern aStar = Pattern.compile("a*");
		Pattern aa = Pattern.compile("aa");
		Pattern inter = aStar.intersection(aa);

		assertTrue(inter.search("aa", false).isSuccess());
		assertTrue(inter.search("aaaa", false).isSuccess());

		assertFalse(inter.search("", false).isSuccess());
		assertFalse(inter.search("a", false).isSuccess());
	}

	@Test
	void intersectionGmailTest() {
		Pattern emails = Pattern.compile("[a-z]+@[a-z]+%.[a-z]+");
		Pattern gmail = Pattern.compile("[a-z]+@gmail%.c");
		Pattern inter = emails.intersection(gmail);

		assertTrue(inter.search("aboba@gmail.c", false).isSuccess());
		assertTrue(inter.search("ivanzolo@gmail.c", false).isSuccess());

		assertFalse(inter.search("gavi@barca.com", false).isSuccess());
		assertFalse(inter.search("test@mail.ru", false).isSuccess());
		assertFalse(inter.search("dvenashka@mephi.ru", false).isSuccess());
	}

	@Test
	void intersectionEmptyAndPatternTest() {
		Pattern empty = Pattern.compile("[]");
		Pattern any = Pattern.compile("a");
		Pattern inter = empty.intersection(any);

		assertFalse(inter.search("a", false).isSuccess());
		assertFalse(inter.search("aboba", false).isSuccess());
		assertFalse(inter.search("jvm", false).isSuccess());
		assertFalse(inter.search("", false).isSuccess());
	}

	@Test
	void intersectionPatternAndEmptyTest() {
		Pattern pattern = Pattern.compile("a+");
		Pattern empty = Pattern.compile("");
		Pattern inter = pattern.intersection(empty);

		assertFalse(inter.search("a", false).isSuccess());
		assertFalse(inter.search("aa", false).isSuccess());
		assertFalse(inter.search("b", false).isSuccess());
		assertFalse(inter.search("", false).isSuccess());
	}

	@Test
	void testIntersectionPatternWithItself() {
		Pattern pattern = Pattern.compile("a+b+c+");
		Pattern inter = pattern.intersection(pattern);

		assertTrue(inter.search("abc", false).isSuccess());
		assertTrue(inter.search("aabbcc", false).isSuccess());
		assertFalse(inter.search("", false).isSuccess());
	}

	@Test
	void intersectionDisjointPatternsTest() {
		Pattern digits = Pattern.compile("[0-9]+");
		Pattern letters = Pattern.compile("[a-z]+");
		Pattern inter = digits.intersection(letters);

		assertFalse(inter.search("123", false).isSuccess());
		assertFalse(inter.search("0", false).isSuccess());
		assertFalse(inter.search("abc", false).isSuccess());
		assertFalse(inter.search("a", false).isSuccess());
	}

	@Test
	void intersectionNoGroupsSupportTest() {
		Pattern pattern1 = Pattern.compile("(a)(b)");
		Pattern pattern2 = Pattern.compile("ab");
		Pattern inter = pattern1.intersection(pattern2);

		assertThrows(IllegalArgumentException.class, () -> inter.search("test", true));
	}

	@Test
	void lookaheadIntersectionTest() {
		Pattern a = Pattern.compile("[a-z]/[123]");
		Pattern b = Pattern.compile("[a-z]/3");
		Pattern inter = a.intersection(b);

		assertTrue(inter.search("a3", false).isSuccess());
		assertTrue(inter.search("b3", false).isSuccess());
		assertFalse(inter.search("a1", false).isSuccess());
		assertFalse(inter.search("a2", false).isSuccess());
	}

	@Test
	void intersectionComplexWithRepetitionsTest() {
		Pattern hasA = Pattern.compile("[ab]*ab[ab]*");
		Pattern endsWithB = Pattern.compile("[ab]*b");
		Pattern inter = hasA.intersection(endsWithB);

		assertTrue(inter.search("ab", false).isSuccess());
		assertTrue(inter.search("aaaaab", false).isSuccess());

		assertEquals(3, inter.search("aabaa", false).end());
		assertFalse(inter.search("bbbb", false).isSuccess());
	}
}