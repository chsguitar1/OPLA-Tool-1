package br.ufpr.dinf.gres.core.jmetal4.metrics.objectivefunctions;

import br.ufpr.dinf.gres.architecture.representation.Architecture;
import br.ufpr.dinf.gres.architecture.representation.Element;
import br.ufpr.dinf.gres.architecture.representation.Package;
import br.ufpr.dinf.gres.core.jmetal4.metrics.ObjectiveFunctionBase;
import br.ufpr.dinf.gres.core.jmetal4.metrics.mutability.AV;

/**
 * Architecture Variability (Zhang et al., 2008)
 * <p>
 * It measures the total variability of the PLA.
 * <p>
 * (|Cv| + sum AV)
 */
public class TV extends ObjectiveFunctionBase {

    public TV(Architecture architecture) {
        super(architecture);

        Double results = new AV(architecture).getResults();
        this.setResults(results);
    }


}
