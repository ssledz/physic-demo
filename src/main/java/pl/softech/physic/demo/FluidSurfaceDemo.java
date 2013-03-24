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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class FluidSurfaceDemo extends AbstractDemo {

	private class FluidSurface {

		private float[] s, b, u, h, vs;
		private final int cells = 300;
		private float dx, dx2;
		private Point2D start;
		private Dimension dim;
		private final float g = 9.81f;
		private final float dt = 0.0001f;

		FluidSurface(Point2D start, Dimension dim, int surfaceGeneratorIndex) {

			this.start = start;
			this.dim = dim;

			s = new float[cells];
			b = new float[cells];
			u = new float[cells];
			h = new float[cells];
			vs = new float[cells];

			Arrays.fill(u, 0);
			Arrays.fill(b, 0);
			Arrays.fill(vs, 0);

			dx = (float) dim.width / (float) cells;
			dx2 = dx * dx;
			getSurfaceGenerator(surfaceGeneratorIndex).run();
		}

		private Runnable getSurfaceGenerator(int index) {

			List<Runnable> ret = new LinkedList<Runnable>();
			ret.add(new Runnable() {

				@Override
				public void run() {
					double dr = Math.PI / cells;
					for (int i = 0; i < b.length; i++) {
						s[i] = dim.height / 2
								+ (float) (Math.sin(dr * i) * dim.height / 4);

						b[i] = (float) (Math.sin(dr * i) * dim.height / 6
								+ dim.height / 6 + Math.sin(2 * dr * i)
								* dim.height / 6);
					}
				}
			});

			ret.add(new Runnable() {

				@Override
				public void run() {
					double dr = 2 * Math.PI / cells;
					for (int i = 0; i < b.length; i++) {
						s[i] = dim.height / 2
								+ (float) (Math.sin(dr * i) * dim.height / 6);

						b[i] = 0;
					}
				}
			});

			return ret.get(index);

		}

		public float convertXFromModelTOView(float x) {
			return (float) (start.getX() + x);
		}

		public float convertYFromModelTOView(float y) {
			return (float) (start.getY() + dim.height - y);
		}

		public void paint(Graphics2D g2) {
			g2.setColor(Color.WHITE);
			Path2D path = new Path2D.Double();
			path.moveTo(start.getX(), start.getY());
			float x = 0;
			for (int i = 0; i < b.length; i++) {
				path.lineTo(convertXFromModelTOView(x),
						convertYFromModelTOView(b[i]));
				x += dx;
			}
			path.lineTo(convertXFromModelTOView(x - dx),
					convertYFromModelTOView(dim.height));
			path.closePath();
			g2.fill(path);

			g2.setColor(Color.BLUE);
//			 renderFluid1(g2, path);
//			 renderFluid2(g2, path);
			renderFluid3(g2);

		}

		@SuppressWarnings("unused")
		private void renderFluid2(Graphics2D g2, Path2D clip) {
			g2.setClip(clip);
			float x = convertXFromModelTOView(0);
			for (int i = 0; i < s.length - 1; i++) {
				float y = convertYFromModelTOView(s[i]);
				float h = Math.max(s[i], s[i + 1]);
				Rectangle2D rec = new Rectangle2D.Float(x, y, dx, h);
				g2.fill(rec);
				x += dx;
			}

		}

		@SuppressWarnings("unused")
		private void renderFluid1(Graphics2D g2, Path2D clip) {
			g2.setClip(clip);
			float x = 0;
			Path2D path = new Path2D.Double();
			path.moveTo(convertXFromModelTOView(0), convertYFromModelTOView(0));
			for (int i = 0; i < s.length; i++) {
				path.lineTo(convertXFromModelTOView(x),
						convertYFromModelTOView(s[i]));
				x += dx;
			}
			path.lineTo(convertXFromModelTOView(x - dx),
					convertYFromModelTOView(0));
			path.closePath();
			g2.fill(path);
		}

		private void renderFluid3(Graphics2D g2) {
			float x = 0;
			Path2D path = new Path2D.Double();
			path.moveTo(convertXFromModelTOView(0),
					convertYFromModelTOView(b[0]));
			for (int i = 0; i < s.length; i++) {
				path.lineTo(convertXFromModelTOView(x),
						convertYFromModelTOView(s[i]));
				x += dx;
			}

			for (int i = b.length - 1; i >= 0; i--) {
				x -= dx;
				path.lineTo(convertXFromModelTOView(x),
						convertYFromModelTOView(b[i]));
			}

			path.closePath();
			g2.fill(path);
		}

		public void update(long elapsed) {

			for (int i = 0; i < h.length; i++) {
				if (s[i] < b[i]) {
					s[i] = b[i];
				}
				h[i] = s[i] - b[i];
			}

			for (int i = 1; i < vs.length - 1; i++) {
				double dvs = h[i] * (s[i - 1] - 2 * s[i] + s[i + 1]);
				dvs += h[i - 1] * (s[i - 1] - s[i]);
				dvs += h[i + 1] * (s[i + 1] - s[i]);
				dvs /= 2 * dx2;
				dvs *= g;

				vs[i] = (float) (vs[i] + dvs * dt);
			}

			for (int i = 0; i < s.length; i++) {
				s[i] = s[i] + vs[i] * dt;
			}
		}

	}

	private FluidSurface fluidSurface1, fluidSurface2;

	public FluidSurfaceDemo(Dimension dim) {
		super(dim);
		sleep = 0;
		Point2D start = new Point2D.Double(dim.width * 0.05, dim.height * 0.2);
		dim = new Dimension((int) (dim.width * 0.4), (int) (dim.height * 0.6));
		fluidSurface1 = new FluidSurface(start, dim, 0);

		start = new Point2D.Double(start.getX() * 2 + dim.width, start.getY());
		fluidSurface2 = new FluidSurface(start, dim, 1);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPaint(Graphics2D g2) {
		fluidSurface1.paint(g2);
		fluidSurface2.paint(g2);
	}

	@Override
	protected void doUpdate(long elapsed) {
		fluidSurface1.update(elapsed);
		fluidSurface2.update(elapsed);
	}

	public static void main(String[] args) {
		Dimension dim = new Dimension(1624, 768);
		show(new FluidSurfaceDemo(dim), dim);
	}

}
