package az.ingress.axonshoppingcart.coreapi.exceptions;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }

}
