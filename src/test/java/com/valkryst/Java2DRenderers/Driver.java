package com.valkryst.Java2DRenderers;

import javax.swing.*;
import java.awt.*;
import java.awt.peer.ComponentPeer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Driver {
	public static void main(final String[] args) throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
		final var panel = new Panel();
		panel.setPreferredSize(new Dimension(512, 512));

		final var frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);

		/*
		 * This is the only way that I know of, for retrieving the peer of an
		 * AWT component. It causes an illegal reflective access exception,
		 * unless the program is run with "--illegal-access=permit".
		 */
		final var peerField = Component.class.getDeclaredField("peer");
		peerField.setAccessible(true);
		final var peer = (ComponentPeer) peerField.get(panel);
		final var renderer = new D3DRenderer(peer);

		final var executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			/*
			 * If the buffer contents are lost while rendering, you should
			 * re-draw the current state again. You could just ignore it and
			 * continue on drawing the next state, but there could be some
			 * visual issues caused by the missed frame.
			 */
			do {
				final var graphics = renderer.getBufferGraphics2D();

				for (int i = 0; i < 1000; i++) {
					final var random = ThreadLocalRandom.current();

					final var x = random.nextInt(panel.getWidth() + 1);
					final var y = random.nextInt(panel.getHeight() + 1);
					final var width = random.nextInt(20);
					final var height = random.nextInt(20);

					final var r = random.nextInt(256);
					final var g = random.nextInt(256);
					final var b = random.nextInt(256);
					final var a = random.nextInt(256);
					final var color = new Color(r, g, b, a);

					graphics.setColor(color);
					graphics.fillOval(x, y, width, height);
				}

				/*
				 * You would normally call graphics.dispose() here, but that
				 * should not be done when using the renderer classes.
				 */
			} while(renderer.bufferContentsLost());

			// This causes the graphics to show up on screen.
			renderer.blitBufferToSurface();
		}, 0, 1000 / 60, TimeUnit.MILLISECONDS);
	}
}
