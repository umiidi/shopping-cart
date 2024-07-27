package az.ingress.axonshoppingcart.coreapi.events;

import lombok.Data;

import java.util.UUID;

@Data
public class CartCreatedEvent {

    UUID cartId;

    public CartCreatedEvent(UUID cartId) {
        this.cartId = cartId;
    }

}
