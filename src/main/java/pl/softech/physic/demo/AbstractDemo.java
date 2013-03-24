/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.softech.physic.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public abstract class AbstractDemo extends JPanel {

	private final class Repainter implements Runnable {

		@Override
		public void run() {
			long start;
			while (true) {
				start = System.currentTimeMillis();
				try {
					Thread.sleep(sleep);
				} catch (Exception e) {
					e.printStackTrace();
				}
				doUpdate(System.currentTimeMillis() - start);
				AbstractDemo.this.repaint();
			}
			
		}
		
	}
	
	private static final long serialVersionUID = 1L;

	protected int width, height;
	private BufferedImage imgBuffer;
	private final Thread repainter;
	protected long sleep = 1000 / 25;
	
	public AbstractDemo(Dimension dim) {
		width = dim.width;
		height = dim.height;
		repainter = new Thread(new Repainter());
		repainter.setDaemon(true);
		this.setBackground(Color.BLACK);
	}
	
	public void start() {
		repainter.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		if (width != w || height != h || imgBuffer == null) {
			width = w;
			height = h;
			imgBuffer = getGraphicsConfiguration().createCompatibleImage(width,
					height);
		}

		Graphics2D g2 = imgBuffer.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
//		g2.setColor(Color.BLACK);
		g2.setColor(getBackground());
		g2.fillRect(0, 0, w, h);
		doPaint(g2);
		g2.dispose();

		((Graphics2D) g).drawImage(imgBuffer, null, 0, 0);
	}
	
	protected abstract void doPaint(Graphics2D g2);
	protected abstract void doUpdate(long elapsed);
	
	
	public static void show(AbstractDemo demo, Dimension dim) {
		JFrame f = new JFrame();
		f.setContentPane(demo);
		f.setSize(dim);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.start();
		f.setVisible(true);
	}
	
}
