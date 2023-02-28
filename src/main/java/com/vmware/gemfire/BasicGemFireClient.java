package com.vmware.gemfire;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


public class BasicGemFireClient {
  private final Region<Integer, String> region;

  public BasicGemFireClient(Region<Integer, String> region) {
    this.region = region;
  }

  public static void main(String[] args) {
    String locatorAddress = System.getenv("LOCATOR_HOST");
    if (locatorAddress == null) {
      System.out.println("Error: No environment value set for LOCATOR_HOST");
      System.exit(-1);
    }

    String TRUST_PSWD= System.getenv("TRUST_STORE_PSWD");
    if (TRUST_PSWD == null) {
      System.out.println("Error: No environment value set for TRUST_STORE_PSWD");
      System.exit(-1);
    }

    Properties props = new Properties();

    props.setProperty("ssl-enabled-components", "all");
    props.setProperty("ssl-endpoint-identification-enabled", "true");
    props.setProperty("ssl-keystore", "/certs/keystore.p12");
    props.setProperty("ssl-keystore-password", TRUST_PSWD);
    props.setProperty("ssl-truststore", "/certs/truststore.p12");
    props.setProperty("ssl-truststore-password", TRUST_PSWD);

    System.out.println("Attempting connection to locator " + locatorAddress);
    ClientCache cache = new ClientCacheFactory(props)
        .addPoolLocator(locatorAddress, 10334)
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
