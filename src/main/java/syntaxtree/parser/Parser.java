package syntaxtree.parser;

import lombok.Getter;
import other.Pair;
import syntaxtree.nodes.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final String input;
	@Getter
	private int groupCnt = 0;
	private int pos = 0;


	private char peek() {
		return pos < input.length() ? input.charAt(pos) : '\0';
	}

	private char next() {
		return input.charAt(pos++);
	}

	private boolean isMeta(char c) {
		return c == '[' || c == ']' || c == '{' || c == '}' || c == '(' || c == ')' || c == '$'
				|| c == '%' || c == '.' || c == '*' || c == '+' || c == '…' || c == '|' || c == '/' || c == '-';
	}

	private boolean matches(char c) {
		if (peek() == c) {
			++pos;
			return true;
		}
		return false;
	}

	private void expect(char c) throws ParsingException {
		char got = peek();
		if (got == c) {
			++pos;
		} else throw new ParsingException("unexpected token: '" + got + "'. expected: '" + c + "'");
	}

	private boolean isStopToken() {
		char c = peek();
		return c == '/' || c == '|' || c == ')' || c == '\0';
	}

	private ASTNode parseLiteral() throws ParsingException {
		char c = next();

		switch (c) {
			case '%' -> {
				char s = next();
				if (isMeta(s)) return new LiteralNode(s);
				throw new ParsingException("expected metacharacter after '%', got: " + s);
			}
			case '$' -> {
				return new EmptyNode();
			}
			case '(' -> {
				int id = ++groupCnt;
				ASTNode node = parseChoice();
				expect(')');
				return new GroupNode(node, id);
			}
			case '[' -> {
				return parseCharRange();
			}
			default -> {
				return new LiteralNode(c);
			}
		}
	}

	private ASTNode parseCharRange() throws ParsingException {
		List<Pair<Character, Character>> ranges = new ArrayList<>();
		List<Character> singles = new ArrayList<>();

		while (peek() != ']' && peek() != '\0') {
			char c = next();
			if (peek() == '-' && c != '%') {
				next();
				char end = next();
				ranges.add(new Pair<>(c, end));
			} else if (c == '%') {
				char s = next();
				if (isMeta(s)) {
					singles.add(s);
				} else throw new ParsingException("expected metacharacter after '%', got: " + s);
			} else {
				singles.add(c);
			}
		}

		expect(']');

		return new CharRangeNode(ranges, singles);
	}

	private ASTNode parseRange(ASTNode node) throws ParsingException {
		StringBuilder range = new StringBuilder();
		while (peek() != '}' && peek() != '\0') range.append(next());
		expect('}');

		String[] borders = range.toString().split(",", -1);
		int min = borders[0].isEmpty() ? 0 : Integer.parseInt(borders[0].trim());
		int max = borders.length < 2 || borders[1].isEmpty() ? -1 : Integer.parseInt(borders[1].trim());
		return new RepetitionNode(node, min, max);
	}

	private ASTNode parseRepetition() throws ParsingException {
		ASTNode node = parseLiteral();
		if (matches('*') || matches('…')) {
			return new RepetitionNode(node, 0, -1);
		}
		if (matches('+')) {
			return new RepetitionNode(node, 1, -1);
		}
		if (matches('{')) {
			return parseRange(node);
		}
		return node;
	}

	private ASTNode parseConcatenation() throws ParsingException {
		List<ASTNode> children = new ArrayList<>();
		children.add(parseRepetition());
		while (pos < input.length() && !isStopToken()) {
			matches('.');
			children.add(parseRepetition());
		}
		return children.size() == 1 ? children.getFirst() : new ConcatenationNode(children);
	}

	private ASTNode parseLookahead() throws ParsingException {
		ASTNode node = parseConcatenation();
		if (matches('/')) {
			node = new LookaheadNode(node, parseConcatenation());
		}
		return node;
	}

	private ASTNode parseChoice() throws ParsingException {
		ASTNode node = parseLookahead();
		while (matches('|')) {
			node = new ChoiceNode(node, parseLookahead());
		}
		return node;
	}

	public static Parser of(String input) {
		return new Parser(input);
	}

	public Parser(String input) {
		this.input = input;
	}

	public ASTNode parse() throws ParsingException {
		return parseChoice();
	}
}