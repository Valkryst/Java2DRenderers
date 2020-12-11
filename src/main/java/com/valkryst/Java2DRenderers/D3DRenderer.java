package com.valkryst.Java2DRenderers;

import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.pipe.hw.ContextCapabilities;

import java.awt.*;
import java.awt.peer.ComponentPeer;

/**
 * Represents a renderer using Microsoft's Direct3D API.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Direct3D">Direct3D</a>
 */
public class D3DRenderer extends Renderer {
    /** Class name of the surface's peer component. */
    private final static String PEER_CLASS_NAME = "sun.awt.windows.WComponentPeer";
    /** Class name of the surface to render on. */
    private final static String SURFACE_CLASS_NAME = "sun.java2d.d3d.D3DSurfaceData";

    /**
     * Constructs a new D3DRenderer.
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
    public D3DRenderer(final ComponentPeer peer) throws ClassNotFoundException {
        super(peer, PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfig() {
        try {
            // We use reflection so that this class loads on linux.
            final var d3dGraphicsDevice = Class.forName("sun.java2d.d3d.D3DGraphicsDevice");
            final var d3dGraphicsConfig = Class.forName("sun.java2d.d3d.D3DGraphicsConfig");
            final var contextCapabilities = Class.forName("sun.java2d.pipe.hw.ContextCapabilities");

            // Find the screen id.
            var method = d3dGraphicsDevice.getMethod("getScreen");
            final int screen = (int) method.invoke(super.getGraphicsConfig().getDevice());

            // Find the device capability.
            method = d3dGraphicsDevice.getDeclaredMethod("getDeviceCaps", int.class);
            method.setAccessible(true);
            final var d3dDeviceCaps = method.invoke(null, screen);

            // Check Direct3D pipeline support.
            method = contextCapabilities.getMethod("getCaps");
            final int CAPS_OK = 1 << 18;
            if ((((int) method.invoke(d3dDeviceCaps)) & CAPS_OK) == 0) {
                throw new RuntimeException("Could not enable Direct3D pipeline on " + "screen " + screen);
            }

            // Find constructors for Direct3D graphics device and configuration.
            final var newD3DGraphicsDevice = d3dGraphicsDevice.getDeclaredConstructor(int.class, ContextCapabilities.class);
            newD3DGraphicsDevice.setAccessible(true);

            final var newD3DGraphicsConfig = d3dGraphicsConfig.getDeclaredConstructor(D3DGraphicsDevice.class);
            newD3DGraphicsConfig.setAccessible(true);

            // Create the Direct3D graphics device.
            final var device = newD3DGraphicsDevice.newInstance(screen, d3dDeviceCaps);
            return (GraphicsConfiguration) newD3DGraphicsConfig.newInstance(device);
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
        return "Direct3D";
    }

    /**
     * Determines whether the D3DRenderer is supported on this machine.
     *
     * @return
     *          Whether the D3DRenderer is supported on this machine.
     */
    public static boolean isSupported() {
        return Renderer.isSupported(PEER_CLASS_NAME, SURFACE_CLASS_NAME);
    }
}
