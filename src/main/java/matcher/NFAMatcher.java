package matcher;

import lombok.Getter;
import automaton.nfa.GroupInfo;
import automaton.nfa.NFA;
import automaton.nfa.NFAState;

import java.util.*;

public class NFAMatcher {
	private final NFA nfa;
	private final String input;
	@Getter
	private int pos;
	private final Map<Integer, GroupInfo> groups;

	public NFAMatcher(NFA nfa, String input) {
		this.nfa = nfa;
		this.input = input;
		this.pos = 0;
		this.groups = new HashMap<>();
	}

	public MatchResult match() {
		// Запускаем рекурсивный поиск с начального состояния и 0-й позиции
		boolean isSuccess = backtrack(nfa.getBegin(), 0, new HashSet<>());

		if (isSuccess) {
			Map<Integer, String> groupMap = new HashMap<>();
			groups.forEach((index, group) -> {
				// Извлекаем подстроку. end хранится включительно, поэтому +1
				int start = group.getBegin();
				int end = group.getEnd();

				if (start <= end + 1) { // end + 1 покрывает пустые группы (например, (a*))
					groupMap.put(index, input.substring(start, end + 1));
				}
			});
			return new MatchResult(groupMap);
		}

		return new MatchResult();
	}

	/**
	 * Основной метод обхода в глубину (DFS / Backtracking).
	 *
	 * @param state           Текущее состояние NFA
	 * @param currentPos      Текущая позиция в строке input
	 * @param visitedEpsilons Множество эпсилон-переходов для защиты от бесконечных циклов
	 * @return true, если путь привел к успешному совпадению
	 */
	private boolean backtrack(NFAState state, int currentPos, Set<NFAState> visitedEpsilons) {
		// 1. Проверка Lookahead (если есть)
		if (state.getLookahead() != null) {
			NFAMatcher lookaheadMatcher = new NFAMatcher(state.getLookahead(), input.substring(currentPos));
			if (!lookaheadMatcher.matchForLookahead()) {
				return false; // Если lookahead не совпал, ветка тупиковая
			}
		}

		// 2. СОХРАНЕНИЕ СОСТОЯНИЯ ГРУПП (для возможного отката)
		Map<Integer, GroupInfo> backupGroups = new HashMap<>();
		for (var entry : state.getGroupMap().entrySet()) {
			int index = entry.getKey();
			boolean isBegin = entry.getValue();

			// Делаем бекап текущего состояния группы, если она уже существует
			if (groups.containsKey(index)) {
				GroupInfo oldGroup = groups.get(index);
				GroupInfo backup = new GroupInfo(oldGroup.getIndex(), oldGroup.getBegin());
				backup.setEnd(oldGroup.getEnd());
				backupGroups.put(index, backup);
			}

			// Применяем новые границы
			if (isBegin) {
				groups.put(index, new GroupInfo(index, currentPos));
			} else {
				if (groups.containsKey(index)) {
					// Конец группы — это предыдущий прочитанный символ
					groups.get(index).setEnd(currentPos - 1);
				}
			}
		}

		// 3. УСЛОВИЕ ВЫХОДА (Успех)
		// Если дошли до конца строки и состояние является принимающим
		if (currentPos == input.length() && state.isAcceptable()) {
			return true;
		}

		// 4. ЭПСИЛОН-ПЕРЕХОДЫ (Обрабатываем в первую очередь)
		for (NFAState eps : state.getEpsilons()) {
			if (visitedEpsilons.add(eps)) { // Защита от зацикливания
				if (backtrack(eps, currentPos, visitedEpsilons)) {
					return true;
				}
				visitedEpsilons.remove(eps); // Убираем из сета при откате
			}
		}

		// 5. ПЕРЕХОДЫ ПО СИМВОЛУ
		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState nextState : state.getTransition(c)) {
				// При шаге по символу сбрасываем историю эпсилон-переходов
				if (backtrack(nextState, currentPos + 1, new HashSet<>())) {
					return true;
				}
			}
		}

		// 6. ОТКАТ (Backtracking)
		// Если ни один путь не подошел, возвращаем группы в исходное состояние
		for (var entry : state.getGroupMap().entrySet()) {
			int index = entry.getKey();
			if (backupGroups.containsKey(index)) {
				groups.put(index, backupGroups.get(index));
			} else {
				groups.remove(index);
			}
		}

		return false;
	}

	// Вспомогательный метод для Lookahead, тоже переведенный на DFS
	private boolean matchForLookahead() {
		return backtrackLookahead(nfa.getBegin(), 0, new HashSet<>());
	}

	private boolean backtrackLookahead(NFAState state, int currentPos, Set<NFAState> visitedEpsilons) {
		if (state.isAcceptable()) return true;

		for (NFAState eps : state.getEpsilons()) {
			if (visitedEpsilons.add(eps)) {
				if (backtrackLookahead(eps, currentPos, visitedEpsilons)) return true;
				visitedEpsilons.remove(eps);
			}
		}

		if (currentPos < input.length()) {
			char c = input.charAt(currentPos);
			for (NFAState nextState : state.getTransition(c)) {
				if (backtrackLookahead(nextState, currentPos + 1, new HashSet<>())) return true;
			}
		}
		return false;
	}
}