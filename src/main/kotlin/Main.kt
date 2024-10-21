package einmilchboss

class DeterministicFiniteAutomatonException(message: String) : Exception(message)

data class TransitionInput(val state: Int, val symbol: Char)

class DeterministicFiniteAutomaton private constructor(
    private val states: Set<Int>,
    private val symbols: Set<Char>,
    private val transitions: Map<TransitionInput, Int>,
    private val start: Int,
    private val finalizers: Set<Int>
) {
    companion object Factory {
        fun create(
            states: Set<Int>,
            symbols: Set<Char>,
            transitions: Map<TransitionInput, Int>,
            start: Int,
            finalizers: Set<Int>
        ): DeterministicFiniteAutomaton =
            DeterministicFiniteAutomaton(states, symbols, transitions, start, finalizers).let { dfa ->
                if (dfa.isValid()) dfa
                else throw DeterministicFiniteAutomatonException("The deterministic finite automaton does not fulfil all prerequisites.")
            }
    }

    override fun toString(): String =
        listOf(
            "states=$states", "symbols=$symbols", "transitions=$transitions", "start=$start", "finalizers=$finalizers"
        ).joinToString(", ", prefix = "DeterministicFiniteAutomaton(", postfix = ")")

    fun isWord(word: String): Boolean =
        finalizers.contains(endState(word))

    private fun endState(word: String): Int =
        deltaCaret(start, word)

    private fun deltaCaret(state: Int, word: String): Int =
        if (word.isEmpty()) state
        else deltaCaret(delta(state, word.last()), word.dropLast(1))

    private fun delta(state: Int, symbol: Char): Int =
        transitions[TransitionInput(state, symbol)]
            ?: throw DeterministicFiniteAutomatonException("The given transition input does not exist.")

    private fun isValid(): Boolean =
        states.contains(start) && states.containsAll(finalizers) && areStatesValid()


    private fun areStatesValid(): Boolean =
        states.all { state ->
            val transitionSymbols = transitions
                .filter { (input, _) -> input.state == state }
                .map { (input, _) -> input.symbol }
            transitionSymbols.size == symbols.size && symbols.containsAll(transitionSymbols)
        }
}

fun main() {
    val dfa = DeterministicFiniteAutomaton.create(
        setOf(1, 2, 3),
        setOf('0', '1'),
        mapOf(
            TransitionInput(1, '0') to 2,
            TransitionInput(1, '1') to 3,
            TransitionInput(2, '0') to 3,
            TransitionInput(2, '1') to 1,
            TransitionInput(3, '0') to 1,
            TransitionInput(3, '1') to 2,
        ),
        1,
        setOf(3)
    )

    val word = "00111"
    println("Is word \"$word\" element of DFA: ${dfa.isWord(word)}")
}