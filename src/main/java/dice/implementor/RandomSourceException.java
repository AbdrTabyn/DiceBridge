package dice.implementor;
/** The only failure type of the Implementor contract. Knows nothing about any concrete source. */
public class RandomSourceException extends RuntimeException {
    public RandomSourceException(String message) {
        super(message);
    }
    public RandomSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
