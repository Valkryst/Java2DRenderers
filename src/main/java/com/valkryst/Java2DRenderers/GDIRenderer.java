package com.valkryst.Java2DRenderers;

import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using Microsoft's Graphics Device Interface (GDI) API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Graphics_Device_Interface">GDI</a>
 */
public class GDIRenderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.windows.GDIWindowSurfaceData";

    /**
     * Constructs a new GDIRenderer.
     *
     * @param peer
     *          Component that the surface is displayed on.
     *          (e.g. An instance of java.awt.Panel)
     *
     * @throws ClassNotFoundException
     *          If the peer or surface classes cannot be found. This will
     *          occur if the JRE/JDK of this machine doesn't support this
     *          renderer.
     */
    public GDIRenderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    /**
     * Retrieves this renderer's name.
     *
     * @return
     *          This renderer's name.
     */
    public static String getName() {
        return "GDI";
    }

    /**
     * Determines whether the GDIRenderer is supported on this machine.
     *
     * @return
     *          Whether the GDIRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }
}
