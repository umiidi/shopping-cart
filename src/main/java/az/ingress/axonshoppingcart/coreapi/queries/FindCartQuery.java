package az.ingress.axonshoppingcart.coreapi.queries;

import lombok.Data;

import java.util.UUID;

@Data
public class FindCartQuery {

    UUID cartId;

    public FindCartQuery(UUID cartId) {
        this.cartId = cartId;
    }

}
