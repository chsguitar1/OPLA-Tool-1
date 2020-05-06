//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package br.ufpr.dinf.gres.core.jmetal4.metaheuristics.nsgaII;

import br.ufpr.dinf.gres.architecture.io.OPLALogs;
import br.ufpr.dinf.gres.domain.OPLAThreadScope;
import br.ufpr.dinf.gres.architecture.io.OptimizationInfo;
import br.ufpr.dinf.gres.architecture.io.OptimizationInfoStatus;
import com.rits.cloning.Cloner;
import br.ufpr.dinf.gres.core.jmetal4.core.*;
import br.ufpr.dinf.gres.core.jmetal4.interactive.InteractiveFunction;
import br.ufpr.dinf.gres.core.jmetal4.qualityIndicator.QualityIndicator;
import br.ufpr.dinf.gres.core.jmetal4.util.Distance;
import br.ufpr.dinf.gres.common.exceptions.JMException;
import br.ufpr.dinf.gres.core.jmetal4.util.Ranking;
import br.ufpr.dinf.gres.core.jmetal4.util.comparators.CrowdingComparator;
import br.ufpr.dinf.gres.core.learning.ClassifierAlgorithm;
import br.ufpr.dinf.gres.core.learning.SubjectiveAnalyzeAlgorithm;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This class implements the NSGA-II algorithm.
 */
public class NSGAII extends Algorithm {

    private static final long serialVersionUID = 5815971727148859507L;
    private static final Logger LOGGER = Logger.getLogger(NSGAII.class);

    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public NSGAII(Problem problem) {
        super(problem);

    } // NSGAII

    /**
     * Runs the NSGA-II algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     * @throws Exception
     */
    public SolutionSet execute() throws JMException {
        LOGGER.info("Iniciando Execução");
        int populationSize;
        int maxEvaluations;
        int evaluations;

        QualityIndicator indicators; // QualityIndicator object
        int requiredEvaluations; // Use in the example of use of the
        // indicators object (see below)

        SolutionSet population;
        SolutionSet offspringPopulation;
        SolutionSet union;

        Operator mutationOperator;
        Operator crossoverOperator;
        Operator selectionOperator;

        Distance distance = new Distance();
        SubjectiveAnalyzeAlgorithm subjectiveAnalyzeAlgorithm = null;

        // Read the parameters
        populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
        int maxInteractions = ((Integer) getInputParameter("maxInteractions")).intValue();
        int firstInteraction = ((Integer) getInputParameter("firstInteraction")).intValue();
        int intervalInteraction = ((Integer) getInputParameter("intervalInteraction")).intValue();
        Boolean interactive = ((Boolean) getInputParameter("interactive")).booleanValue();
        InteractiveFunction interactiveFunction = ((InteractiveFunction) getInputParameter("interactiveFunction"));

        int currentInteraction = 0;
        indicators = (QualityIndicator) getInputParameter("indicators");
        HashSet<Solution> bestOfUserEvaluation = new HashSet<>();

        // Initialize the variables
        population = new SolutionSet(populationSize);
        evaluations = 0;

        requiredEvaluations = 0;

        // Read the operators
        mutationOperator = operators_.get("mutation");
        crossoverOperator = operators_.get("crossover");
        selectionOperator = operators_.get("selection");

        try {
            LOGGER.info("Criando População");
            // Create the initial solutionSet
            Solution newSolution;
            for (int i = 0; i < populationSize; i++) {
                newSolution = newRandomSolution(mutationOperator);
                evaluations++;
                population.add(newSolution);
            }

        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            throw new JMException(e.getMessage());
        }

        try {
            LOGGER.info("Iniciando evoluções");
            // Generations
            while (evaluations < maxEvaluations) {
                // Create the offSpring solutionSet
                offspringPopulation = new SolutionSet(populationSize);
                Solution[] parents = new Solution[2];

                for (int i = 0; i < (populationSize / 2); i++) {
                    if (evaluations < maxEvaluations) {
                        LOGGER.info("Origin INDIVIDUO: " + i + " evolucao: " + evaluations);
                        parents[0] = (Solution) selectionOperator.execute(population);
                        parents[1] = (Solution) selectionOperator.execute(population);

                        Object execute = crossoverOperator.execute(parents);
                        if (execute instanceof Solution) {
                            Solution offSpring = (Solution) crossoverOperator.execute(parents);
                            problem_.evaluateConstraints(offSpring);
                            mutationOperator.execute(offSpring);
                            problem_.evaluateConstraints(offSpring);
                            problem_.evaluate(offSpring);
                            offspringPopulation.add(offSpring);
                        } else {
                            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
                            problem_.evaluateConstraints(offSpring[0]);
                            problem_.evaluateConstraints(offSpring[1]);

                            mutationOperator.execute(offSpring[0]);
                            mutationOperator.execute(offSpring[1]);
                            problem_.evaluateConstraints(offSpring[0]);
                            problem_.evaluateConstraints(offSpring[1]);

                            problem_.evaluate(offSpring[0]);
                            problem_.evaluate(offSpring[1]);

                            offspringPopulation.add(offSpring[0]);
                            offspringPopulation.add(offSpring[1]);
                        }
                        evaluations += 2;
                    }
                }

                // Create the solutionSet union of solutionSet and offSpring
                LOGGER.info("Union solutions");
                union = ((SolutionSet) population).union(offspringPopulation);

                // Ranking the union
                LOGGER.info("Ranking the union");
                Ranking ranking = new Ranking(union);

                int remain = populationSize;
                int index = 0;
                SolutionSet front = null;
                population.clear();

                // Obtain the next front
                LOGGER.info("getSubfront()");
                front = ranking.getSubfront(index);

                while ((remain > 0) && (remain >= front.size())) {
                    // Assign crowding distance to individuals
                    LOGGER.info("crowdingDistanceAssignment()");
                    distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                    // Add the individuals of this front
                    for (int k = 0; k < front.size(); k++) {
                        population.add(front.get(k));
                    }

                    // Decrement remain
                    remain = remain - front.size();

                    // Obtain the next front
                    index++;
                    if (remain > 0) {
                        LOGGER.info("getSubfront()");
                        front = ranking.getSubfront(index);
                    }
                }

                // Remain is less than front(index).size, insert only the best
                // one
                if (remain > 0) { // front contains individuals to insert
                    LOGGER.info("crowdingDistanceAssignment()");
                    distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
                    front.sort(new CrowdingComparator());
                    for (int k = 0; k < remain; k++) {
                        population.add(front.get(k));
                    }
                    remain = 0;
                }

                // This piece of code shows how to use the indicator object into
                // the code
                // of NSGA-II. In particular, it finds the number of evaluations
                // required
                // by the algorithm to obtain a Pareto front with a hypervolNSGAume
                // higher
                // than the hypervolume of the true Pareto front.

                int generation = evaluations / populationSize;
                OPLAThreadScope.currentGeneration.set(generation);
                OPLALogs.add(new OptimizationInfo(Thread.currentThread().getId(), "Generation " + generation, OptimizationInfoStatus.RUNNING));
                LOGGER.info(">> GENERATION " + generation);
                // The score is set up to 0 because in future mutations the object can be modified and due to the score the modified solution is manteined imutable
                for (Solution solution : offspringPopulation.getSolutionSet()) {
                    solution.setEvaluation(0);
//                        If you wish block replicated freezed solutions, uncomment this line
//                        for (Element elementsWithPackage : solution.getAlternativeArchitecture().getElementsWithPackages()) {
//                            elementsWithPackage.unsetFreeze();
//                        }
                }
                if (interactive && currentInteraction < maxInteractions && ((generation % intervalInteraction == 0 && generation >= firstInteraction) || generation == firstInteraction)) {
                    offspringPopulation = interactiveFunction.run(offspringPopulation);
                    if (subjectiveAnalyzeAlgorithm == null) {
                        subjectiveAnalyzeAlgorithm = new SubjectiveAnalyzeAlgorithm((OPLASolutionSet) offspringPopulation, ClassifierAlgorithm.CLUSTERING_MLP);
                        subjectiveAnalyzeAlgorithm.run(null, false);
                    } else {
                        subjectiveAnalyzeAlgorithm.run((OPLASolutionSet) offspringPopulation, false);
                    }
                    bestOfUserEvaluation.addAll(offspringPopulation.getSolutionSet().stream().filter(p -> (p.getEvaluation() >= 5 && p.getEvaluatedByUser()) || (p.containsArchitecturalEvaluation() && p.getEvaluatedByUser())).collect(Collectors.toList()));
                    currentInteraction++;
                }

//              MID MLP
                if (interactive && currentInteraction < maxInteractions && Math.abs((currentInteraction * intervalInteraction) + (intervalInteraction / 2)) == generation && generation > firstInteraction) {
                    subjectiveAnalyzeAlgorithm.run((OPLASolutionSet) offspringPopulation, true);
                }

                if (interactive && subjectiveAnalyzeAlgorithm != null && !subjectiveAnalyzeAlgorithm.isTrained() && currentInteraction >= maxInteractions) {
//                    subjectiveAnalyzeAlgorithm.run();
                    subjectiveAnalyzeAlgorithm.setTrained(true);
                }

                if (interactive && subjectiveAnalyzeAlgorithm != null && subjectiveAnalyzeAlgorithm.isTrained() && currentInteraction >= maxInteractions && ((generation % intervalInteraction == 0 && generation >= firstInteraction) || generation == firstInteraction)) {
                    subjectiveAnalyzeAlgorithm.evaluateSolutionSetSubjectiveAndArchitecturalMLP((OPLASolutionSet) offspringPopulation, true);
                }

                if ((indicators != null) && (requiredEvaluations == 0)) {
                    double HV = indicators.getHypervolume(population);
                    if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
                        requiredEvaluations = evaluations;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            throw new JMException(e.getMessage());
        }

        // Return as output parameter the required evaluations
        LOGGER.info("setOutputParameter()");
        setOutputParameter("evaluations", requiredEvaluations);

        // Return the first non-dominated front
        LOGGER.info("Ranking()");
        SolutionSet populationOriginal = new Cloner().shallowClone(population);
        Ranking ranking = new Ranking(population);

        SolutionSet subfrontToReturn = ranking.getSubfront(0);

        removeRuim(subfrontToReturn, populationOriginal, interactive);

        subfrontToReturn.setCapacity(subfrontToReturn.getCapacity() + bestOfUserEvaluation.size());
        for (Solution solution : bestOfUserEvaluation) {
            if (!subfrontToReturn.getSolutionSet().contains(solution)) {
                subfrontToReturn.add(solution);
            }
        }

        if (interactive && subjectiveAnalyzeAlgorithm != null && subjectiveAnalyzeAlgorithm.isTrained()) {
            try {
                subjectiveAnalyzeAlgorithm.evaluateSolutionSetSubjectiveAndArchitecturalMLP((OPLASolutionSet) subfrontToReturn, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return subfrontToReturn;
        // return population;
    } // execute

    private void removeRuim(SolutionSet population, SolutionSet original, Boolean interactive) {
        if (interactive) {
            for (int i = 0; i < population.getSolutionSet().size(); i++) {
                if (population.get(i).getEvaluation() == 1) {
                    population.remove(i);
                }
            }
        }
    }

    private Solution newRandomSolution(Operator mutationOperator) throws Exception {
        Solution newSolution;
        newSolution = new Solution(problem_);
        // criar a diversidade na populacao inicial
        mutationOperator.execute(newSolution);
        problem_.evaluate(newSolution);

        problem_.evaluateConstraints(newSolution);
        return newSolution;
    }
} // NSGA-II
