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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class PendulumDemo extends AbstractDemo {

	private static final long serialVersionUID = 1L;

	private class Pendulum {

		private Point2D start;
		private int r;
		private double g = 9.81f;
		private double theta, omega;
		private double vx, vy;

		public Pendulum(int startX, int startY, int r, float theta) {
			this.start = new Point2D.Float(startX, startY);
			this.r = r;
			this.theta = theta;
		}

		private double getX() {
			return r * Math.sin(theta);
		}

		private double getY() {
			return r * Math.cos(theta);
		}

		public Point2D getEnd() {
			return new Point2D.Double(start.getX() + getX(), start.getY()
					+ getY());
		}

		public Point2D getStart() {
			return start;
		}

		public Point2D getV() {
			double scale = 20;
			return new Point2D.Double(scale * vx, scale * vy);
		}

		public float getMRadius() {
			return r / 20;
		}

		public void update(long elapsed) {
			double dt = (double) elapsed / 1000.0;
			omega = omega - g / r * Math.sin(theta) * dt;
			theta = theta + omega * dt;

			double omega2 = omega - g / r * Math.sin(theta) * dt;
			double theta2 = theta + omega2 * dt;
			vx = -getX() + r * Math.sin(theta2);
			vy = -getY() + r * Math.cos(theta2);
		}

		public void doPaint(Graphics2D g2) {

			Point2D start = getStart();
			Point2D end = getEnd();
			Line2D line = new Line2D.Double(start, end);
			g2.setColor(Color.GREEN);
			g2.draw(line);
			Point2D v = getV();

			line = new Line2D.Double(end, new Point2D.Double(end.getX()
					+ v.getX(), end.getY() + v.getY()));
			g2.setColor(Color.BLUE);
			g2.draw(line);
			Ellipse2D m = new Ellipse2D.Double(end.getX() - getMRadius() / 2,
					end.getY() - getMRadius() / 2, getMRadius(), getMRadius());
			g2.setColor(Color.RED);
			g2.fill(m);
		}
	}

	private Pendulum pendulum1, pendulum2;
	
	public PendulumDemo(Dimension dim) {
		super(dim);
		pendulum1 = new Pendulum(dim.width / 2, (int) (dim.height * 0.2),
				(int) (dim.height * 0.6), (float) (Math.PI / 5));
		pendulum2 = new Pendulum(dim.width / 2, (int) (dim.height * 0.2),
				(int) (dim.height * 0.6), (float) (Math.PI / 2));
	}
	
	@Override
	protected void doPaint(Graphics2D g2) {
		pendulum1.doPaint(g2);
		pendulum2.doPaint(g2);

	}

	@Override
	protected void doUpdate(long elapsed) {
		elapsed = 100;
		pendulum1.update(elapsed);
		pendulum2.update(elapsed);

	}
	
	public static void main(String[] args) {
		Dimension dim = new Dimension(800, 600);
		show(new PendulumDemo(dim), dim);
	}

}
