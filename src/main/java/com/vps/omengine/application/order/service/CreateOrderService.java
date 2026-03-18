package com.vps.omengine.application.order.service;

import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.port.in.CreateOrderUseCase;
import com.vps.omengine.application.order.port.out.OrderRepository;
import com.vps.omengine.application.product.port.out.ProductRepository;
import com.vps.omengine.domain.order.Order;
import com.vps.omengine.domain.order.OrderLine;
import com.vps.omengine.domain.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
Application Service (Use Case Implementation) for creating an order.
This Class (Service) implements the CreateOrderUseCase interface (port in).

Responsibilities :
- Orchestrate the use case (creation of an order by interacting with the OrderRepository (port out)).
- Convert input DtOs -> Domain objects and vice versa.
- Call Domain logic (e.g., Order.create()) to perform the actual creation of the order.
- Persist aggregates through  ports (e.g., OrderRepository) and return results (e.g., order ID).

Important Rule :
Application Services should not contain business logic.
They should delegate to the Domain Model (e.g. Order entity) for any businsess rules or invariants.
Business rules belongs inside the Domain Layer ( Order Aggreagate, Domain Services , etc).


* */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    /*
      Output port dependency,

      The application layer depends on an interface (port) to interact with the persistence layer (e.g., OrderRepository),
      not a concrete database implementation.

      The acutal implementation will live in :
      adapter/persistence/JpaOrderRepository (or any other persistence technology) and will be injected
      into this service (q.g. via constructor injection) by the framework (e.g., Spring).
    */

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    /*
     Constructor injection.

     The persistence adapter will provide the implementation
     of the OrderRepository when the application starts.
     */

    public CreateOrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository
            ){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /*
    Main use case method.

    Flow :
    1. Recieve command from controller
    2. Convert commmand Dto -> domain objects
    3. Create domain aggregate
    4. Persist aggregate through repository
    5. Return generated Order ID
     */

    @Transactional
    @Override
    public UUID createOrder(CreateOrderCommand command){
          // Convert the incoming command  items into Domain Orderline Objects.
          //  Create Command is a DTO (application layer)
          // OrderLine is a Domain Object.
        List<OrderLine> lines = command.items()
                .stream()
                .map(item ->{
                        // 1. Fetch Product
                        Product product = productRepository.findById(item.productId())
                                .orElseThrow(() ->
                                        new RuntimeException("Product not found" + item.productId()));

                        // 2. Validate Stock
                        if(!product.isInStock(item.quantity())){
                            throw  new RuntimeException("Insufficient stock for product :" + item.productId());
                        }

                        // 3. Reduce Stock
                        product.decreaseStock(item.quantity());

                        // 4. save updated product
                         productRepository.save(product);

                     return   OrderLine.create(
                        item.productId(),
                        item.quantity(),
                        item.price()
                );
                })
                .collect(Collectors.toList());

        // Create the Order Aggregaate using the domain factory.

        // Important :
        // Domain rules like validation , total calculation and state initialization
        // happen inside the Order.create()

        Order order  = Order.create(command.customerId(), lines);

        // Persist the order aggregate through the output port.

        orderRepository.save(order);

        return order.getOrderId();
    }
}
