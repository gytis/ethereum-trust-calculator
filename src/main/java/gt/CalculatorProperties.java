package gt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("calculator")
public class CalculatorProperties {

    private String nodeUrl = "http://127.0.0.1:8545";

    private int firstBlockNumber = 0;

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public int getFirstBlockNumber() {
        return firstBlockNumber;
    }

    public void setFirstBlockNumber(int firstBlockNumber) {
        this.firstBlockNumber = firstBlockNumber;
    }
}
