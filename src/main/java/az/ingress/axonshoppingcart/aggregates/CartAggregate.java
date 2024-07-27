package az.ingress.axonshoppingcart.aggregates;

import az.ingress.axonshoppingcart.coreapi.commands.CreateCartCommand;
import az.ingress.axonshoppingcart.coreapi.commands.DeSelectProductCommand;
import az.ingress.axonshoppingcart.coreapi.commands.SelectProductCommand;
import az.ingress.axonshoppingcart.coreapi.events.CartCreatedEvent;
import az.ingress.axonshoppingcart.coreapi.events.ProductDeSelectedEvent;
import az.ingress.axonshoppingcart.coreapi.events.ProductSelectedEvent;
import az.ingress.axonshoppingcart.coreapi.exceptions.ProductDeSelectedException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Data
@Aggregate
@NoArgsConstructor
public class CartAggregate {

    @AggregateIdentifier
    private UUID cartId;

    private Map<UUID, Integer> selectedProducts;

    @CommandHandler
    public CartAggregate(CreateCartCommand command) {
        log.info("Handling a new cart command");
        UUID aggregateId = UUID.randomUUID();
        AggregateLifecycle.apply(new CartCreatedEvent(aggregateId));
    }

    @CommandHandler
    public void handle(SelectProductCommand command) {
        log.info("Handling a select product command {}", command);
        ProductSelectedEvent selectProductEvent = ProductSelectedEvent.builder()
                .cartId(command.getCartId())
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();
        AggregateLifecycle.apply(selectProductEvent);
    }

    @CommandHandler
    public void handle(DeSelectProductCommand command) {
        log.info("Handling a deselect product command {}", command);
        ProductDeSelectedEvent deSelectedEvent = ProductDeSelectedEvent.builder()
                .cartId(command.getCartId())
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();
        AggregateLifecycle.apply(deSelectedEvent);
    }

    @EventSourcingHandler
    public void on(CartCreatedEvent event) {
        log.info("Handling a cart created event {}", event);
        this.cartId = event.getCartId();
        this.selectedProducts = new HashMap<>();
    }

    @EventSourcingHandler
    public void on(ProductSelectedEvent event) {
        log.info("Handling a product selected event {}", event);
        selectedProducts.merge(event.getProductId(), event.getQuantity(), Integer::sum);
        log.info("The cart state is: {}", this);
    }

    @EventSourcingHandler
    public void on(ProductDeSelectedEvent event) {
        log.info("Handling a product deselected event {}", event);
        if (!selectedProducts.containsKey(event.getProductId())) {
            throw new ProductDeSelectedException();
        }
        Integer currentQuantity = selectedProducts.get(event.getProductId());
        int newQuantity = currentQuantity - event.getQuantity();
        if (newQuantity < 0) {
            newQuantity = 0;
        }
        selectedProducts.put(event.getProductId(), newQuantity);
        log.info("The cart state is: {}", this);
    }

}