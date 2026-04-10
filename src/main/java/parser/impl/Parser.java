package parser.impl;

import other.Pair;
import parser.IParser;
import parser.ParsingException;
import syntaxtree.*;

import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {
	private final String input;
	private int groupCnt = 0;
	private int pos = 0;


	private char peek() {
		return pos < input.length() ? input.charAt(pos) : '\0';
	}

	private char next() {
		return input.charAt(pos++);
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
		} else throw new ParsingException("unexpected token: " + got + ". expected: " + c);
	}

	private boolean isStopToken() {
		char c = peek();
		return c == '/' || c == '|' || c == ')' || c == '\0';
	}

	private ASTNode parseLiteral() throws ParsingException {
		char c = next();

		switch (c) {
			case '%' -> {
				return new LiteralNode(String.valueOf(next()));
			}
			case '$' -> {
				return new LiteralNode("");
			}
			case '(' -> {
				ASTNode node = parseChoice();
				expect(')');
				return new GroupNode(node, ++groupCnt);
			}
			case '[' -> {
				return parseCharRange();
			}
			default -> {
				return new LiteralNode(String.valueOf(c));
			}
		}
	}

	private ASTNode parseCharRange() throws ParsingException {
		List<Pair<Character, Character>> ranges = new ArrayList<>();
		List<Character> singles = new ArrayList<>();

		while (peek() != ']' && peek() != '\0') {
			char c = next();
			if (peek() == '-') {
				next();
				char end = next();
				ranges.add(new Pair<>(c, end));
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
		while (pos < input.length() && !isStopToken()) {
			matches('.');
			children.add(parseRepetition());
		}
		return children.size() == 1 ? children.getFirst() : new ConcatenationNode(children);
	}

	private ASTNode parseLookahead() throws ParsingException {
		ASTNode node = parseConcatenation();
		while (matches('/')) {
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



	public Parser(String input) {
		this.input = input;
	}

	@Override
	public ASTNode parse() {
		return null;
	}
}