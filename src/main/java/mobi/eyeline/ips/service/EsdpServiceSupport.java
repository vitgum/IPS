package mobi.eyeline.ips.service;

import mobi.eyeline.ips.Hacks;
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
      final String port = builder.getPort() >= 0 ? ":" + builder.getPort() : "";

      final String uri = builder.getScheme() + "://" + builder.getHost() + port +
          "/push?service=" + getServiceId(survey);

      //noinspection ConstantConditions
      return Hacks.ENABLE_C2S_SOURCE_HEURISTICS ? uri + "&delivery=true" : uri;

    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public String getServiceId(Survey survey) {
    final String provider = survey.getOwner().getEsdpProvider();
    return provider + "." + getTag(survey);
  }

  String getTag(Survey survey) {
    return String.format("ips-%04d-%04d", survey.getOwner().getId(), survey.getId());
  }

  String getId(Survey survey) {
    return getTag(survey);
  }

}
