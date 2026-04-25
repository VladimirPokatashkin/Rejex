package graphviz;

import automaton.dfa.DFA;
import automaton.dfa.DFAState;
import automaton.nfa.NFA;
import automaton.nfa.NFAState;
import other.Pair;
import syntaxtree.SyntaxTree;
import syntaxtree.nodes.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DotMaker {
	public static void visualizeAll(Path pathToDots) {
		try (Stream<Path> files = Files.list(pathToDots)) {
			files.filter(file -> file.getFileName().toString().endsWith(".dot"))
					.forEach(file -> {
						String fileName = file.getFileName().toString();
						String pngName = fileName.replace(".dot", ".png");
						Path pngPath = file.resolveSibling(pngName);

						var pb = new ProcessBuilder("dot", "-Tpng",
								file.toString(), "-o", pngPath.toString());
						pb.redirectErrorStream(true);

						try {
							Process p = pb.start();
							String error = new String(p.getInputStream().readAllBytes());
							int exitCode = p.waitFor();

							if (exitCode != 0) {
								throw new IOException("graphviz error with " + fileName + ": " + error);
							}
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							throw new RuntimeException("Interrupted while processing " + fileName, e);
						} catch (IOException _) {}
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String NFAtoDotString(NFA nfa) {
		var begin = nfa.getBegin();
		var end = nfa.getEnd();

		StringBuilder sb = new StringBuilder();
		sb.append("digraph NFA {\n");
		sb.append("    rankdir=LR;\n");
		sb.append("    node [shape=circle];\n");

		if (nfa.getEnd().isAcceptable()) {
			sb.append("    node [shape=doublecircle]; \"").append(end.getId()).append("\";\n");
			sb.append("    node [shape=circle];\n");
		}

		Set<NFAState> visited = new HashSet<>();
		Queue<NFAState> queue = new LinkedList<>();

		queue.add(begin);
		visited.add(begin);

		while (!queue.isEmpty()) {
			NFAState current = queue.poll();

			for (var entry : current.getTransitions().entrySet()) {
				char symbol = entry.getKey();

				for (NFAState target : entry.getValue()) {
					String label = escapeDotLabel(String.valueOf(symbol));
					sb.append("    \"").append(current.getId()).append("\" -> \"")
							.append(target.getId()).append("\" [label=\"").append(label).append("\"];\n");

					if (!visited.contains(target)) {
						visited.add(target);
						queue.add(target);
					}
				}
			}

			for (NFAState target : current.getEpsilons()) {
				sb.append("    \"").append(current.getId()).append("\" -> \"")
						.append(target.getId()).append("\" [label=\"ε\"];\n");

				if (!visited.contains(target)) {
					visited.add(target);
					queue.add(target);
				}
			}

			if (!current.getGroupMap().isEmpty()) {
				String groupInfo = current.getGroupMap().entrySet().stream()
					.map(e -> "group " + e.getKey() + ":" + (e.getValue() ? "start" : "end"))
					.collect(Collectors.joining(", "));
				sb.append("    \"").append(current.getId()).append("\" [xlabel=\"").append(groupInfo).append("\"];\n");
			}

			if (current.getLookahead() != null) {
				NFA lookaheadNFA = current.getLookahead();

				sb.append("    subgraph cluster_lookahead_").append(current.getId()).append(" {\n");
				sb.append("        label=\"Lookahead\";\n");
				sb.append("        color=blue;\n");

				processLookaheadNFA(lookaheadNFA, sb);

				sb.append("    }\n");
			}
		}

		sb.append("    start [shape=point];\n");
		sb.append("    start -> \"").append(begin.getId()).append("\";\n");

		sb.append("}\n");
		return sb.toString();
	}

	private static String escapeDotLabel(String label) {
		return label.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	private static void processLookaheadNFA(NFA lookaheadNFA, StringBuilder sb) {
		if (lookaheadNFA == null) return;

		Set<NFAState> visited = new HashSet<>();
		Queue<NFAState> queue = new LinkedList<>();

		queue.add(lookaheadNFA.getBegin());
		visited.add(lookaheadNFA.getBegin());

		while (!queue.isEmpty()) {
			NFAState current = queue.poll();

			for (var entry : current.getTransitions().entrySet()) {
				char symbol = entry.getKey();
				for (NFAState target : entry.getValue()) {
					String label = escapeDotLabel(String.valueOf(symbol));
					sb.append("        \"").append(current.getId()).append("\" -> \"")
							.append(target.getId()).append("\" [label=\"").append(label).append("\", style=dashed, color=blue];\n");

					if (!visited.contains(target)) {
						visited.add(target);
						queue.add(target);
					}
				}
			}

			for (NFAState target : current.getEpsilons()) {
				sb.append("        \"").append(current.getId()).append("\" -> \"")
						.append(target.getId()).append("\" [label=\"ε\", style=dashed, color=blue];\n");

				if (!visited.contains(target)) {
					visited.add(target);
					queue.add(target);
				}
			}
		}
	}

	public static String DFAtoDotString(DFA dfa) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph DFA {\n");
		sb.append("    rankdir=LR;\n");
		sb.append("    node [shape=circle];\n");
		sb.append("    start [shape=point];\n");
		sb.append("    start -> ").append(dfa.getBegin().getId()).append(";\n\n");

		for (DFAState state : dfa.getStates()) {
			if (state.isAcceptable()) {
				sb.append("    ").append(state.getId())
						.append(" [shape=doublecircle];\n");
			}
			if (state.isLookaheadBound()) {
				sb.append("    ").append(state.getId())
						.append(" [color=blue, peripheries=2];\n");
			}
		}

		sb.append("\n");

		Map<Pair<Integer, Integer>, Set<Character>> groupedTransitions = new HashMap<>();
		for (DFAState from : dfa.getStates()) {
			for (var entry : from.getTransitions().entrySet()) {
				char c = entry.getKey();
				DFAState to = entry.getValue();
				Pair<Integer, Integer> pair = new Pair<>(from.getId(), to.getId());
				groupedTransitions.computeIfAbsent(pair, _ -> new TreeSet<>()).add(c);
			}
		}

		for (var entry : groupedTransitions.entrySet()) {
			var pair = entry.getKey();
			Set<Character> chars = entry.getValue();
			String label = compressCharSet(chars);
			sb.append("    ").append(pair.first)
					.append(" -> ").append(pair.second)
					.append(" [label=\"").append(escapeDotLabel(label)).append("\"];\n");
		}

		sb.append("}\n");
		return sb.toString();
	}

	private static String compressCharSet(Set<Character> chars) {
		if (chars.isEmpty()) return "";

		List<Character> sorted = new ArrayList<>(chars);
		Collections.sort(sorted);

		StringBuilder result = new StringBuilder();
		int i = 0;
		while (i < sorted.size()) {
			char start = sorted.get(i);
			char end = start;
			int j = i + 1;
			while (j < sorted.size() && sorted.get(j) == end + 1) {
				end = sorted.get(j);
				j++;
			}

			if (!result.isEmpty()) result.append(",");

			if (start == end) {
				result.append(escapeChar(start));
			} else if (end == start + 1) {
				result.append(escapeChar(start)).append(",").append(escapeChar(end));
			} else {
				result.append(escapeChar(start)).append("-").append(escapeChar(end));
			}
			i = j;
		}
		return result.toString();
	}

	private static String escapeChar(char c) {
		return switch (c) {
			case '\\' -> "\\\\";
			case '"' -> "\\\"";
			case '\n' -> "\\\\n";
			case '\r' -> "\\\\r";
			case '\t' -> "\\\\t";
			default -> String.valueOf(c);
		};
	}

	public static String ASTtoDotString(SyntaxTree tree) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph SyntaxTree {\n");
		sb.append("  rankdir=TB;\n");
		sb.append("  node [shape=box];\n");

		processNode(tree.getRoot(), sb, 0);

		sb.append("}\n");
		return sb.toString();
	}

	private static int processNode(ASTNode node, StringBuilder sb, int id) {
		String label = getNodeLabel(node);
		sb.append("  n").append(id).append(" [label=\"").append(escapeDotLabel(label)).append("\"];\n");

		int nextId = id + 1;

		switch (node) {
			case LiteralNode _, EmptyNode _, CharRangeNode _, EndNode _ -> {}
			case GroupNode group -> {
				sb.append("  n").append(id).append(" -> n").append(nextId).append(";\n");
				nextId = processNode(group.getNode(), sb, nextId);
			}
			case RepetitionNode repetition -> {
				sb.append("  n").append(id).append(" -> n").append(nextId).append(";\n");
				nextId = processNode(repetition.getNode(), sb, nextId);
			}
			case LookaheadNode lookahead -> {
				sb.append("  n").append(id).append(" -> n").append(nextId).append(" [label=\"expr\"];\n");
				nextId = processNode(lookahead.getLeft(), sb, nextId);
				sb.append("  n").append(id).append(" -> n").append(nextId).append(" [label=\"lookahead\"];\n");
				nextId = processNode(lookahead.getRight(), sb, nextId);
			}
			case ChoiceNode choice -> {
				sb.append("  n").append(id).append(" -> n").append(nextId).append(" [label=\"left\"];\n");
				nextId = processNode(choice.getLeft(), sb, nextId);
				sb.append("  n").append(id).append(" -> n").append(nextId).append(" [label=\"right\"];\n");
				nextId = processNode(choice.getRight(), sb, nextId);
			}
			case ConcatenationNode concatenation -> {
				for (int i = 0; i < concatenation.getChildren().size(); ++i) {
					sb.append("  n").append(id).append(" -> n").append(nextId);
					if (concatenation.getChildren().size() > 1) {
						sb.append(" [label=\"").append(i + 1).append("\"]");
					}
					sb.append(";\n");
					nextId = processNode(concatenation.getChildren().get(i), sb, nextId);
				}
			}
		}

		return nextId;
	}

	private static String getNodeLabel(ASTNode node) {
		return switch (node) {
			case LiteralNode literal -> "'" + escapeDotLabel(String.valueOf(literal.getValue())) + "'";
			case EmptyNode _ -> "ε";
			case EndNode _ -> "#";
			case GroupNode group -> "group №" + group.getIndex();
			case ConcatenationNode _ -> "concatenation";
			case LookaheadNode _ -> "lookahead";
			case ChoiceNode _ -> "choice";
			case CharRangeNode range -> {
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				range.getRanges().forEach(pair -> {
					sb.append(pair.first);
					sb.append('-');
					sb.append(pair.second);
				});
				range.getSingles().forEach(sb::append);
				sb.append("]");
				yield sb.toString();
			}
			case RepetitionNode repetition -> {
				String maxStr = repetition.getMax() == -1 ? "∞" : String.valueOf(repetition.getMax());
				yield "repetition [" + repetition.getMin() + ".." + maxStr + "]";
			}
		};
	}
}