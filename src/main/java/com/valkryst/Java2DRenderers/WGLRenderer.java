package com.valkryst.Java2DRenderers;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using Microsoft's WGL API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/WGL_(API)">WGL</a>
 */
public class WGLRenderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.opengl.WGLSurfaceData";

    /**
     * Constructs a new WGLRenderer.
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
    public WGLRenderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            final var currentGraphicsConfiguration = super.getGraphicsConfig();

            final var win32GraphicsDevice = Class.forName("sun.awt.Win32GraphicsDevice");
            final var win32GraphicsConfig = Class.forName("sun.awt.Win32GraphicsConfig");
            final var wglGraphicsConfig = Class.forName("sun.java2d.opengl.WGLGraphicsConfig");

            // Get the visual id.
            final int visualId = (int) win32GraphicsConfig.getMethod("getVisual").invoke(currentGraphicsConfiguration);

            // Gets the method to get the GraphicsConfiguration for Windows OpenGL
            final var getConfigMethod = wglGraphicsConfig.getMethod("getConfig", win32GraphicsDevice, int.class);

            // Get the graphics configuration for the graphics device and visual id.
            return (GraphicsConfiguration) getConfigMethod.invoke(null, currentGraphicsConfiguration.getDevice(), visualId);
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
        return "OpenGL (WGL)";
    }

    /**
     * Determines whether the WGLRenderer is supported on this machine.
     *
     * @return
     *          Whether the WGLRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }
}
