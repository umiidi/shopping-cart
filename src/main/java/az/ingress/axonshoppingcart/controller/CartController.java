package az.ingress.axonshoppingcart.controller;

import az.ingress.axonshoppingcart.coreapi.commands.CreateCartCommand;
import az.ingress.axonshoppingcart.coreapi.commands.DeSelectProductCommand;
import az.ingress.axonshoppingcart.coreapi.commands.SelectProductCommand;
import az.ingress.axonshoppingcart.coreapi.queries.FindCartQuery;
import az.ingress.axonshoppingcart.dto.CartView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public UUID handle() {
        log.info("Received a new cart action");
        return commandGateway.sendAndWait(new CreateCartCommand());
    }

    @PostMapping("/add")
    public void handle(@RequestBody SelectProductCommand command) {
        log.info("Received select product action");
        commandGateway.send(command);
    }

    @PostMapping("/remove")
    public void handle(@RequestBody DeSelectProductCommand command) {
        log.info("Received deselect product action");
        commandGateway.send(command);
    }

    @GetMapping("/{cartId}")
    public CompletableFuture<CartView> get(@PathVariable String cartId) {
        return queryGateway.query(new FindCartQuery(UUID.fromString(cartId)), ResponseTypes.instanceOf(CartView.class));
    }

}
