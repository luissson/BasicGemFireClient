package com.vmware.gemfire;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.proxy.ProxySocketFactories;


public class BasicGemFireClient {
  private final Region<Integer, String> region;

  public BasicGemFireClient(Region<Integer, String> region) {
    this.region = region;
  }

  public static void main(String[] args) throws ParseException {

    CommandLine commandLine;

    Options options = new Options();
    CommandLineParser parser = new DefaultParser();

    options.addOption("h", true, "locator hostname or ip address" );
    options.addOption("p", true, "trustore and keystore password");
    options.addOption("i", true, "ingress ip address");
    options.addOption("o", true, "ingress port number");

    commandLine = parser.parse(options, args);

    String LOCATOR_HOST = commandLine.getOptionValue("h");
    System.out.println(LOCATOR_HOST);
    String TRUST_PSWD = commandLine.getOptionValue("p");
    System.out.println(TRUST_PSWD);
    String INGRESS_IP = commandLine.getOptionValue("i");
    System.out.println(INGRESS_IP);
    String INGRESS_PORT = commandLine.getOptionValue("o");
    System.out.println(INGRESS_PORT);

    Properties props = new Properties();

    props.setProperty("ssl-enabled-components", "all");
    props.setProperty("ssl-endpoint-identification-enabled", "true");
    props.setProperty("ssl-keystore", "./certs/keystore.p12");
    props.setProperty("ssl-keystore-password", TRUST_PSWD);
    props.setProperty("ssl-truststore", "./certs/truststore.p12");
    props.setProperty("ssl-truststore-password", TRUST_PSWD);

    System.out.println("Attempting connection to locator " + LOCATOR_HOST);
    ClientCache cache = new ClientCacheFactory(props)
        .setPoolSocketFactory(ProxySocketFactories.sni(INGRESS_IP, Integer.parseInt(INGRESS_PORT)))
        .addPoolLocator(LOCATOR_HOST, 10334)
        .create();

    Region<Integer, String> region =
        cache.<Integer, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create("example-region");

    BasicGemFireClient example = new BasicGemFireClient(region);
    example.putData(10);
    example.getAndPrintValues(example.getRegionKeys());

    cache.close();
  }

  Set<Integer> getRegionKeys() {
    return new HashSet<>(region.keySetOnServer());
  }

  void putData(int numValues) {
    for(int i = 0; i < numValues; i++) {
      region.put(i, "value" + i);
    }
  }

  void getAndPrintValues(Set<Integer> keySet) {
    for(Integer key : keySet) {
      System.out.printf("%d:%s%n", key, region.get(key));
    }
  }
}
