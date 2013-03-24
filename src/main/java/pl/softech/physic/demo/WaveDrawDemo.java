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
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class WaveDrawDemo extends AbstractDemo {

	private static byte NORMAL = 0;
	private static byte SRC = 1 << 0;
	private static byte BND = 1 << 1;

	private class Wave {

		private Point2D start;
		private int meshSize;
		private double sind;
		private double dx, dt;
		private double velocity = 200;

		private double[] U, V, T;

		private byte[] status;

		public Wave(Point2D start, Point2D end, int meshSize) {
			this.start = start;
			this.meshSize = meshSize;

			U = new double[meshSize];
			V = new double[meshSize];
			T = new double[meshSize];

			Arrays.fill(U, 0);
			Arrays.fill(V, 0);
			Arrays.fill(T, 0);

			int left = 1;
			int right = (int) (meshSize * 0.999);
			status = new byte[meshSize];
			Arrays.fill(status, 0, left, (byte) (BND | SRC));
			Arrays.fill(status, left, right, NORMAL);
			Arrays.fill(status, right, meshSize, BND);

			dx = (end.getX() - start.getX()) / meshSize;
			dt = 0.0001;
			
			start = new Point2D.Double(start.getX() + left * dx, start.getY());

		}

		private void addSinusImpuls() {

			if (sind >= Math.PI) {
				return;
			}

			for (int i = 0; (status[i] & SRC) == SRC; i++) {
				V[i] = 150000 * Math.sin(sind);
			}

			sind += Math.PI / 180;

			return;

		}

		public void addBounds() {
			for (int i = 0; i < meshSize; i++) {
				if ((status[i] & BND) == BND) {
					U[i] = 0;
					V[i] = 0;
					T[i] = 0;
				}
			}
		}

		public void update(long elapsed) {

			addSinusImpuls();

			for (int i = 1; i < meshSize - 1; i++) {

				if ((status[i] & BND) == BND && (status[i + 1] & BND) == BND) {
					continue;
				}

				V[i] = (V[i - 1] + V[i + 1]) / 2.0 + velocity * (dt / dx)
						* (T[i + 1] - T[i]);

				T[i] = (T[i - 1] + T[i + 1]) / 2.0 + velocity * (dt / dx)
						* (V[i + 1] - V[i]);
			}

			for (int i = 0; i < meshSize; i++) {

				if ((status[i] & BND) == BND) {
					continue;
				}

				U[i] = U[i] + V[i] * dt;
			}

			for (int i = 1; i < meshSize - 1; i++) {

				if ((status[i] & BND) == BND && (status[i + 1] & BND) == BND) {
					continue;
				}

				T[i] = velocity * (U[i + 1] - U[i - 1]) / (2 * dx);
			}

			addBounds();

		}

		public void doPaint(Graphics2D g2) {

			double x = start.getX();
			for (int i = 0; i < meshSize - 1; i++) {
				Line2D line = new Line2D.Double(x, start.getY() - U[i], x + dx,
						start.getY() - U[i + 1]);
				g2.setColor(Color.GREEN);
				g2.draw(line);
				x += dx;
			}

		}
	}

	private static final long serialVersionUID = 1L;

	private Wave[] waves;

	public WaveDrawDemo(Dimension dim) {
		super(dim);
		sleep = 0;
		
		waves = new Wave[5];
		for(int i = 0; i < waves.length; i++) {
			Point2D start = new Point2D.Double(dim.getWidth() * 0.1,
					dim.getHeight() * (4+i) / 10);
			Point2D end = new Point2D.Double(dim.getWidth() * 0.9, start.getY());
			waves[i] = new Wave(start, end, 3000);
		}
		
	}

	@Override
	protected void doPaint(Graphics2D g2) {
		for(Wave w : waves) {
			w.doPaint(g2);
		}
	}

	@Override
	protected void doUpdate(long elapsed) {
		for(Wave w : waves) {
			w.update(elapsed);
		}
	}

	public static void main(String[] args) {
		Dimension dim = new Dimension(1624, 768);
		show(new WaveDrawDemo(dim), dim);
	}

}
