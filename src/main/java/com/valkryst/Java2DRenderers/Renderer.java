package com.valkryst.Java2DRenderers;

import lombok.SneakyThrows;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class Renderer {
    /** Component that the surface is displayed on. */
    private final ComponentPeer parent;

    /** Class of the surface's peer component. */
    private Class<?> peerClass;

    /** Class of the surface to render on. */
    private Class<?> surfaceClass;

    /** Cached instance of this renderer's graphics. */
    private Graphics2D surfaceGraphics;

    private VolatileImage screenBuffer;

    /** Cached instance of the screen buffer's graphics. */
    private Graphics2D bufferGraphics;

    /**
     * Constructs a new Renderer.
     *
     * @param parent
     *          Component that the surface is displayed on.
     *          (e.g. An instance of java.awt.Panel)
     *
     * @param peerClassName
     *          Class name of the surface's peer component.
     *
     * @param surfaceClassName
     *          Class name of the surface to render on.
     *
     * @throws ClassNotFoundException
     *          If the peer or surface classes cannot be found. This will
     *          occur if the JRE/JDK of this machine doesn't support this
     *          renderer.
     */
    public Renderer(final ComponentPeer parent, final String peerClassName, final String surfaceClassName) throws ClassNotFoundException {
        this.peerClass = Class.forName(peerClassName);
        this.surfaceClass = Class.forName(surfaceClassName);

        this.parent = parent;

        parent.updateGraphicsData(getGraphicsConfig());
    }

    public void blitBufferToSurface() {
        final var surfaceGraphics = getSurfaceGraphics2D();

		/*
		 * todo Specify the width/height
		 *
		 * If you consider the screen to be a JPanel, the width/height
		 * are the width/height of your JPanel.
		 *
		 * In my original implementation, I retrieved the singleton of
		 * my settings model and used the width/height from there.
		 */
        // final var settings = SettingsModel.getInstance();
        // final int width = settings.getScreenWidth();
        // final int height = settings.getScreenHeight();
        surfaceGraphics.drawImage(screenBuffer, 0, 0, 512, 512, null); // todo Change "512, 512" to "width, height"

        /*
         * For an unknown reason, the D3D pipeline requires the graphics
         * object to be revalidated and then for the surface to be marked as
         * dirty.
         *
         * If these two actions are not performed, then nothing will be
         * displayed.
         */
        if (this instanceof D3DRenderer) {
            // todo The D3DRenderer class should handle this itself
            try {
                var method = SunGraphics2D.class.getDeclaredMethod("revalidateAll");
                method.setAccessible(true);
                method.invoke(surfaceGraphics);

                ((SunGraphics2D) surfaceGraphics).surfaceData.markDirty();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempt to create a surface and convert it to a {@link Graphics2D} object.
     * <p/>
     * In doing so, this method temporarily switches to the new {@link GraphicsConfiguration}.
     * This method does not cache the newly created {@link Graphics2D} object.
     *
     * #method
     *
     * @return a {@link Graphics2D} object.
     */
    protected Graphics2D createGraphics() {
        try {
            // Creates the surface.
            final var createDataMethod = surfaceClass.getDeclaredMethod("createData", peerClass);
            final var surfaceData = (SurfaceData) createDataMethod.invoke(null, parent);

            // Fail if the surface is not created.
            if (surfaceData == null) {
                return null;
            }

            // Use unofficial APi to convert the surface to Graphics2D.
            return new SunGraphics2D(surfaceData, Color.BLACK, Color.BLACK, null);
        } catch (Exception e) {
            // #error If anything bad happens, we fail.
            e.printStackTrace(); // todo Remove this and do something diff.
            return null;
        }
    }

    /**
     * Retrieves renderer's {@link java.awt.GraphicsConfiguration}.
     *
     * @return
     *          This renderer's {@link java.awt.GraphicsConfiguration}.
     */
    protected GraphicsConfiguration getGraphicsConfig() {
        return parent.getGraphicsConfiguration();
    }

    private void applyRenderHints(final Graphics2D graphics2D) {
        // Automatically detect the best text rendering settings and apply them.
        final var desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        graphics2D.setRenderingHints(desktopHints);


		/*
		 * todo Specify the rendering hints.
		 *
		 * In my original implementation, I retrieved the singleton of
		 * my settings model and used the values from there.
		 */
		// final var settings = SettingsModel.getInstance();
        // graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, settings.getAlphaInterpolationHint());
        // graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, settings.getAntialiasingHint());
		// graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, settings.getColorRenderingHint());
		// graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, settings.getDitheringHint());
		// graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, settings.getInterpolationHint());
		// graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, settings.getRenderingHint());
		// graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    }

    private Graphics2D getSurfaceGraphics2D() {
        if (surfaceGraphics == null) {
            surfaceGraphics = createGraphics();
            applyRenderHints(surfaceGraphics);
        }

        if (((SunGraphics2D) surfaceGraphics).surfaceData.isSurfaceLost()) {
            surfaceGraphics = createGraphics();
            applyRenderHints(surfaceGraphics);
        }

        return surfaceGraphics;
    }

    public Graphics2D getBufferGraphics2D() {
        if (bufferGraphics == null) {
            if (screenBuffer == null) {
				/*
				 * todo Specify the width/height
				 *
				 * If you consider the screen to be a JPanel, the width/height
				 * are the width/height of your JPanel.
				 *
				 * In my original implementation, I retrieved the singleton of
				 * my settings model and used the width/height from there.
				 */
				// final var settings = SettingsModel.getInstance();
				// final int width = settings.getScreenWidth();
				// final int height = settings.getScreenHeight();
                final int imageType = BufferedImage.TYPE_INT_ARGB;

                final var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                final var graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
                final var graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
                screenBuffer = graphicsConfiguration.createCompatibleVolatileImage(512, 512, imageType); // todo Change "512, 512" to "width, height"
            }

            bufferGraphics = (Graphics2D) screenBuffer.getGraphics();
            applyRenderHints(bufferGraphics);
            bufferGraphics.setClip(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        }

        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        return bufferGraphics;
    }

    /**
     * Determines if the buffer is in a bad state and needs to be re-rendered.
     *
     * @see VolatileImage#contentsLost()
     *
     * @return
     *          Whether the buffer is in a bad state and needs to be,
     *          re-rendered or not.
     *
     */
    public boolean bufferContentsLost() {
        return screenBuffer.contentsLost();
    }

    /**
     * Determines whether the specified renderer is supported on this machine.
     *
     * @param peerClassName
     *          Class name of the surface's peer component.
     *
     * @param surfaceClassName
     *          Class name of the surface to render on.
     *
     * @return
     *          Whether the renderer is supported on this machine.
     */
    @SneakyThrows
    public static boolean isSupported(final String peerClassName, final String surfaceClassName) {
        try {
            Class.forName(peerClassName);
            Class.forName(surfaceClassName);
        } catch (final ClassNotFoundException ignored) {
            return false;
        }

        return true;
    }
}
