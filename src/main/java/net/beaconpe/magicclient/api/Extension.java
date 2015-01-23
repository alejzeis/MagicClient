package net.beaconpe.magicclient.api;

/**
 * Represents an extension.
 */
public interface Extension {
    /**
     * Gets this Extension's name.
     * @return The Extension's name.
     */
    String getName();

    /**
     * Get this Extension's provider. The Provider is usually the language the extension is written in (ex javascript).
     * @return The Extension's provider as a String.
     */
    String getProvider();

    /**
     * Check to see if this extension is enabled.
     * @return If this extension is enabled or not.
     */
    boolean isEnabled();

}
