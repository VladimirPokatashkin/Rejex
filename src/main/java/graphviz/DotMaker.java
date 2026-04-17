package graphviz;

import nfa.NFA;
import nfa.NFAState;

import java.util.*;
import java.util.stream.Collectors;

public class DotMaker {
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

				processLookahead(lookaheadNFA, sb);

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

	private static void processLookahead(NFA lookaheadNFA, StringBuilder sb) {
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
}