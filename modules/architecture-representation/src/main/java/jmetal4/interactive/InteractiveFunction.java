package jmetal4.interactive;

import jmetal4.core.SolutionSet;

/**
 * Interface that allows to adapt the method of interaction with the user
 */
public interface InteractiveFunction {

    jmetal4.core.SolutionSet run(SolutionSet solutionSet);
}