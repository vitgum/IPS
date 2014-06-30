package mobi.eyeline.ips.service;

import mobi.eyeline.ips.model.Survey;
import mobi.eyeline.ips.properties.Config;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class EsdpServiceSupport {
    private final Config config;

    public EsdpServiceSupport(Config config) {
        this.config = config;
    }

    public String getServiceUrl(Survey survey) {
        try {
            final URIBuilder builder = new URIBuilder(config.getEsdpEndpointUrl());
            return builder.getScheme() + "://" + builder.getHost() +
                    "/push?service=" + getKey(survey);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    String getKey(Survey survey) {
        return "ips." + getTag(survey);
    }

    String getTag(Survey survey) {
        return String.format("ips-%04d-%04d", survey.getOwner().getId(), survey.getId());
    }

    String getId(Survey survey) {
        return getTag(survey);
    }

}