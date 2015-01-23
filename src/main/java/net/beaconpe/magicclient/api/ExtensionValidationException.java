package net.beaconpe.magicclient.api;

/**
 * Represents an @link{java.lang.Exception} that is to be called when there is an error in extension validation.
 */
public class ExtensionValidationException extends RuntimeException{

    public ExtensionValidationException(String message){
        super(message);
    }
}
