package kelly.rocketmq.entity;

import java.math.BigDecimal;

/**
 * Created by kelly.li on 17/12/17.
 */
public class Order {

    private String orderId;
    private String payName;
    private BigDecimal amount;


    public Order(String orderId, String payName, BigDecimal amount) {
        this.orderId = orderId;
        this.payName = payName;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
