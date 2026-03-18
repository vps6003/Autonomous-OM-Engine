package com.vps.omengine.adapter.web.order;

import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.port.in.CreateOrderUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase){
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public UUID createOrder(@RequestBody CreateOrderCommand command){
        return createOrderUseCase.createOrder(command);
    }
}
