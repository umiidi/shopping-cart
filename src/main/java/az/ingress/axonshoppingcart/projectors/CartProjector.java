package az.ingress.axonshoppingcart.projectors;

import az.ingress.axonshoppingcart.coreapi.entities.Cart;
import az.ingress.axonshoppingcart.coreapi.events.CartCreatedEvent;
import az.ingress.axonshoppingcart.coreapi.events.ProductDeSelectedEvent;
import az.ingress.axonshoppingcart.coreapi.events.ProductSelectedEvent;
import az.ingress.axonshoppingcart.coreapi.exceptions.CartNotFoundException;
import az.ingress.axonshoppingcart.coreapi.queries.FindCartQuery;
import az.ingress.axonshoppingcart.coreapi.repository.CartRepository;
import az.ingress.axonshoppingcart.dto.CartView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartProjector {

    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;

    @EventHandler
    public void on(CartCreatedEvent event) {
        log.info("Cart persisted into database {}", event);
        Cart cart = new Cart(event.getCartId(), Collections.emptyMap());
        cartRepository.save(cart);
    }

    @EventHandler
    public void on(ProductSelectedEvent event) {
        log.info("Cart select product event persisted into database {}", event);
        Cart cart = getCart(event.getCartId());
        cart.getProducts().merge(event.getProductId(), event.getQuantity(), Integer::sum);
        cartRepository.save(cart);
    }

    @EventHandler
    public void on(ProductDeSelectedEvent event) {
        log.info("Cart deselect product event persisted into database {}", event);
        Cart cart = getCart(event.getCartId());
        Integer currentQuantity = cart.getProducts().get(event.getProductId());
        int newQuantity = currentQuantity - event.getQuantity();
        if (newQuantity < 0) {
            cart.getProducts().remove(event.getProductId());
        } else {
            cart.getProducts().put(event.getProductId(), newQuantity);
        }
        cartRepository.save(cart);
    }

    @QueryHandler
    @Transactional
    public CartView on(FindCartQuery query) {
        log.info("query cart with id: {}", query.getCartId());
        Cart cart = getCart(query.getCartId());
        return modelMapper.map(cart, CartView.class);
    }

    private Cart getCart(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id " + cartId));
    }

}

