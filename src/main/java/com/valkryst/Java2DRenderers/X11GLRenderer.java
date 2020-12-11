package com.valkryst.Java2DRenderers;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using OpenGL and the the X Window System API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/X_Window_System">X Window System</a>
 */
public class X11GLRenderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.opengl.GLXSurfaceData";

    /**
     * Constructs a new X11GLRenderer.
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
    public X11GLRenderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        if (!isSupported()) {
            return super.getGraphicsConfig();
        }

        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var x11GraphicsDevice = Class.forName("sun.awt.X11GraphicsDevice");
            final var x11GraphicsConfig = Class.forName("sun.awt.X11GraphicsConfig");

            final var getVisualMethod = x11GraphicsConfig.getMethod("getVisual");
            final int visual = (int) getVisualMethod.invoke(currentGraphicsConfiguration);

            final var getConfigMethod = x11GraphicsConfig.getMethod("getConfig", x11GraphicsDevice, int.class, int.class, int.class, boolean.class);

            // I pulled these values out of nowhere, may be wrong.
            // 24 = depth, 0 = colormap, false= doublebuffer
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visual, 24, 0, false);
        } catch (final Exception e) {
            // todo Tons of exceptions can be thrown in the above code, catch it or something.
            // Otherwise, we fall back to the default and hope for the best.
            e.printStackTrace();
            return super.getGraphicsConfig();
        }
    }

    /**
     * Retrieves this renderer's name.
     *
     * @return
     *          This renderer's name.
     */
    public static String getName() {
        return "OpenGL (X11)";
    }

    /**
     * Determines whether the X11GLRenderer is supported on this machine.
     *
     * @return
     *          Whether the X11GLRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }
}
