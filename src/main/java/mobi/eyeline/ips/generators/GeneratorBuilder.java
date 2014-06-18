package mobi.eyeline.ips.generators;

import dk.brics.automaton.Automaton;
import mobi.eyeline.ips.generators.impl.PolynomialPermutation;
import mobi.eyeline.ips.generators.impl.SimplePattern;
import mobi.eyeline.ips.generators.util.AutomatonUtils;

public class GeneratorBuilder {

    private Automaton automaton;

    public GeneratorBuilder(String regex) throws UnsupportedPatternException {
        automaton = AutomatonUtils.toAutomaton(regex);
    }

    public GeneratorBuilder exclude(String regex) throws UnsupportedPatternException {
        final Automaton excluded = AutomatonUtils.toAutomaton(regex);
        automaton = automaton.minus(excluded);

        return this;
    }

    public SequenceGenerator build(int startPosition)
            throws UnsupportedPatternException {

        final String[] positionOptions = AutomatonUtils.asStateSymbols(automaton);

        final SimplePattern pattern = new SimplePattern(positionOptions);
        final PolynomialPermutation permutation =
                new PolynomialPermutation(pattern.getCapacity());

        return new SequenceGenerator(pattern, permutation, startPosition);
    }
}