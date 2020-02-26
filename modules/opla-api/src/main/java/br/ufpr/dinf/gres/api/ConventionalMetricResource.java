package br.ufpr.dinf.gres.api;

import br.ufpr.dinf.gres.domain.entity.metric.ConventionalMetric;
import br.ufpr.dinf.gres.api.config.BaseResource;
import br.ufpr.dinf.gres.persistence.service.BaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conventional-metric")
public class ConventionalMetricResource extends BaseResource<ConventionalMetric> {

    public ConventionalMetricResource(BaseService<ConventionalMetric> service) {
        super(service);
    }
}