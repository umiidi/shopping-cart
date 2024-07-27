package az.ingress.axonshoppingcart.coreapi.repository;

import az.ingress.axonshoppingcart.coreapi.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
