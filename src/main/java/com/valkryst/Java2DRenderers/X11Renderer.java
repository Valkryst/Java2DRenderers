package com.valkryst.Java2DRenderers;

import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using the the X Window System API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/X_Window_System">X Window System</a>
 */
public class X11Renderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.xr.XRSurfaceData";

    /**
     * Constructs a new X11Renderer.
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
    public X11Renderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    /**
     * Retrieves this renderer's name.
     *
     * @return
     *          This renderer's name.
     */
    public static String getName() {
        return "X11";
    }

    /**
     * Determines whether the X11Renderer is supported on this machine.
     *
     * @return
     *          Whether the XRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }
}
