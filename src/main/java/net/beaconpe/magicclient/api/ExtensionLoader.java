package net.beaconpe.magicclient.api;

import java.io.File;
import java.util.HashMap;

/**
 * Represents an extension loader.
 */
public interface ExtensionLoader {

    /**
     * Validate an Extension.
     * @param file The File that the extension is in.
     * @return If the extension is a valid extension.
     */
    boolean validateExtension(File file);

    /**
     * Load the extensions into memory. This also initiates them in the process.
     * @param extensionDirectory
     */
    void loadExtensions(File extensionDirectory);

    /**
     * Get a @link{java.util.HashMap} that contains the extensions currently loaded in memory. This includes disabled ones.
     * @return A Map with all loaded extensions.
     */
    HashMap<Extension, File> getExtensions();

    /**
     * Get a @link{Extension} by their name.
     * @param name The Extension's name.
     * @return The Extension. Returns null if it is not loaded in memory.
     */
    Extension getExtensionByName(String name);

}
