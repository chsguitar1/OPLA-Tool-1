package br.ufpr.dinf.gres.core.jmetal4.metrics.concernDrivenMetrics.concernDiffusion;

import br.ufpr.dinf.gres.architecture.representation.Architecture;
import br.ufpr.dinf.gres.core.jmetal4.metrics.ObjectiveFunctionBase;

public class CDAClass extends ObjectiveFunctionBase {

    public CDAClass(Architecture architecture) {
        super(architecture);
        double sumCDAClass = 0.0;

        CDAClassConcerns cdaclass = new CDAClassConcerns(architecture);
        for (CDAClassResult c : cdaclass.getResults()) {
            sumCDAClass += c.getElements().size();
        }

        this.setResults(sumCDAClass);
    }

}
