package  com.vps.omengine.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class OrderLine {

    private final UUID productId;
    private final int quantity;
    private final BigDecimal price;

    private OrderLine(UUID productId, int quantity , BigDecimal price){
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderLine create(UUID productId , int quantity , BigDecimal price){
        if(productId == null)
            throw new IllegalArgumentException("productId cannot be null");
        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");

        if(price == null || price.compareTo(BigDecimal.ZERO) <=0 )
            throw new IllegalArgumentException("Price must be positive and greater than 0.");

        return new OrderLine(productId, quantity, price);
    }

    public BigDecimal getSubtotal(){
        return price
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(2, RoundingMode.HALF_UP);
    }


    public UUID getProductId(){
        return productId;
    }

    public int getQuantity(){
        return quantity;
    }

    public BigDecimal getPrice(){
        return price;
    }

}