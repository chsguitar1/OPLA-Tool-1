package br.ufpr.dinf.gres.core.jmetal4.metrics.conventionalMetrics;

import br.ufpr.dinf.gres.architecture.representation.Architecture;
import br.ufpr.dinf.gres.architecture.representation.Class;
import br.ufpr.dinf.gres.core.jmetal4.metrics.ObjectiveFunctionBase;

public class ATMRElegance extends ObjectiveFunctionBase {

    public ATMRElegance(Architecture architecture) {
        super(architecture);
        this.setResults(0.0);
        double stdDeviationRatios;
        double[] ratiosAttributesMethods = new double[10000];
        int i = 0;

        ConventionalMetricsStatistic e = new ConventionalMetricsStatistic();

        for (Class cls : architecture.getAllClasses()) {
            if (cls.getAllMethods().size() > 0) {
                ratiosAttributesMethods[i] = (double) cls.getAllAttributes().size() / cls.getAllMethods().size();
                i++;
            } else {
                ratiosAttributesMethods[i] = 0.0;
                i++;
            }

        }
        e.setArray(ratiosAttributesMethods);
        stdDeviationRatios = e.getSampleStandardDeviation();

        this.setResults(stdDeviationRatios);
    }

}
