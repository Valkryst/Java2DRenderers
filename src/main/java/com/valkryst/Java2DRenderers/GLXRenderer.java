package com.valkryst.Java2DRenderers;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using the OpenGL Extension to the X Window System (GLX)
 * API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/GLX">GLX</a>
 */
public class GLXRenderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.X11ComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.x11.X11SurfaceData";

    /**
     * Constructs a new GLXRenderer.
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
    public GLXRenderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var glxGraphicsConfig = Class.forName("sun.java2d.opengl.GLXGraphicsConfig");
            final var x11GraphicsDevice = Class.forName("sun.awt.X11GraphicsDevice");
            final var x11GraphicsConfig = Class.forName("sun.awt.X11GraphicsConfig");

            final var getVisualMethod = x11GraphicsConfig.getMethod("getVisual");
            final int visual = (int) getVisualMethod.invoke(currentGraphicsConfiguration);

            final var getConfigMethod = glxGraphicsConfig.getMethod("getConfig", x11GraphicsDevice, int.class);
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visual);
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
        return "OpenGL (GLX)";
    }

    /**
     * Determines whether the GLXRenderer is supported on this machine.
     *
     * @return
     *          Whether the GLXRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        boolean isSupported = Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);

        try {
            final var x11GraphicsEnvironment = Class.forName("sun.awt.X11GraphicsEnvironment");
            final var isGLXAvailableMethod = x11GraphicsEnvironment.getMethod("isGLXAvailable");
            isSupported &= (boolean) isGLXAvailableMethod.invoke(null);
        } catch (final Exception e) {
            isSupported = false;
        }

        return isSupported;
    }
}
