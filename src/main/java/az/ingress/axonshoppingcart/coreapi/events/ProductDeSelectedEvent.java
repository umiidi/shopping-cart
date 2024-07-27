package az.ingress.axonshoppingcart.coreapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeSelectedEvent {

    private UUID cartId;
    private UUID productId;
    private Integer quantity;

}
