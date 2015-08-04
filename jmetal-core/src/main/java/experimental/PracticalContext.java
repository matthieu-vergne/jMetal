package experimental;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Example of context for which we would like to solve the TSP problem. The
 * context is kept simple so that simple algorithms can be applied to obtain a
 * correct result. This context provides its own concepts (e.g. {@link City},
 * length) due to the fact that it represents a practical context on which we
 * would like to apply some algorithms to solve some problems.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class PracticalContext {

	/**
	 * A {@link City} instance contains the data related to a given city.
	 * Because we design a practical context, a {@link City} can have something
	 * more than a mere (x,y) coordinate: we could add the name of the city,
	 * information about the clients there, etc. While from a TSP perspective
	 * all of that could appear as irrelevant, a practical context could surely
	 * need this information. By showing that we can reuse this data "as is"
	 * with the algorithm, we show one of the advantages of the genericity of
	 * the framework: we can adapt the existing data for using it in the
	 * algorithm, without needing to re-implement anything (problem nor
	 * algorithm).
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	public static class City {
		private final double x;
		private final double y;
		private final String name;
		private final int clients;

		public City(double x, double y, String name, int clients) {
			this.x = x;
			this.y = y;
			this.name = name;
			this.clients = clients;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public String getName() {
			return name;
		}

		public int getClients() {
			return clients;
		}

		@Override
		public String toString() {
			return name + "(" + x + ", " + y + ")";
		}
	}

	/**
	 * The list of cities we care about in this context, with all their
	 * information.
	 */
	private final List<City> cities = Arrays.asList(
			new City(2, 1, "Paris", 20), new City(0, 1, "Washington", 15),
			new City(1, 1, "London", 5), new City(0, 0, "Mexico", 3), new City(
					2, 0, "Rome", 2));

	/**
	 * This method provides an access to the cities, so that they can be reused
	 * by other entities to setup an algorithm.
	 */
	public List<City> getCities() {
		return cities;
	}

	/**
	 * Because we intend to solve a TSP problem, what we want is to minimize the
	 * length of the travel. Thus, computing the length of a path is part of the
	 * knowledge available in our context, which is provided through this
	 * method.
	 */
	public Double calculatePathLength(List<City> path) {
		double length = 0;
		City previous = null;
		for (City next : path) {
			if (previous == null) {
				// nothing to compute yet
			} else {
				double dx = next.getX() - previous.getX();
				double dy = next.getY() - previous.getY();
				length += Math.sqrt(dx * dx + dy * dy);
			}
			previous = next;
		}
		return length;
	}

	/**
	 * Because we are providing here a practical context, it is probable that
	 * some facilities has been provided to display the information in a
	 * context-relevant way. This method provides such a facility by displaying
	 * a path in a textual and graphical way.
	 * 
	 * @param path
	 */
	public void displayResult(List<City> path) {
		System.out.println("Display path: " + path);

		Canvas canvas = new Canvas(cities);
		canvas.setPath(path);

		JFrame frame = new JFrame("TSP");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(300, 200));
		frame.setLayout(new GridLayout(1, 1));
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Code of the component for the graphical display of the path.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	public class Canvas extends JPanel {

		private final List<City> cities;
		private final double xMin;
		private final double xMax;
		private final double yMin;
		private final double yMax;
		private List<City> path;

		public Canvas(List<City> cities) {
			this.cities = cities;

			double xMin = Double.POSITIVE_INFINITY;
			double xMax = Double.NEGATIVE_INFINITY;
			double yMin = Double.POSITIVE_INFINITY;
			double yMax = Double.NEGATIVE_INFINITY;
			for (City city : cities) {
				xMin = Math.min(xMin, city.getX());
				xMax = Math.max(xMax, city.getX());
				yMin = Math.min(yMin, city.getY());
				yMax = Math.max(yMax, city.getY());
			}
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
		}

		public void setPath(List<City> path) {
			if (cities.containsAll(path)) {
				this.path = path;
			} else {
				throw new IllegalArgumentException(
						"The path use different cities.");
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int availableHeight = getSize().height;
			int availableWidth = getSize().width;

			int dotRadius = 5;
			int padding = 50;
			int allowedHeight = availableHeight - 2 * padding;
			int allowedWidth = availableWidth - 2 * padding;

			CoordTransform xTransform = new CoordTransform(xMin, xMax, padding,
					padding + allowedWidth);
			CoordTransform yTransform = new CoordTransform(yMin, yMax, padding,
					padding + allowedHeight, availableHeight);

			for (City city : cities) {
				g.setColor(Color.BLUE);
				int x = xTransform.toPixels(city.x);
				int y = yTransform.toPixels(city.y);
				g.drawOval(x - dotRadius, y - dotRadius, 2 * dotRadius,
						2 * dotRadius);
				Rectangle2D textArea = g.getFontMetrics().getStringBounds(
						city.name, g);
				g.drawString(city.name, (int) (x - textArea.getCenterX()), y
						- dotRadius);
			}

			if (path == null) {
				// don't draw path
			} else {
				City previous = null;
				for (City next : path) {
					if (previous == null) {
						// nothing to draw
					} else {
						g.setColor(Color.RED);
						int xFrom = xTransform.toPixels(previous.x);
						int yFrom = yTransform.toPixels(previous.y);
						int xTo = xTransform.toPixels(next.x);
						int yTo = yTransform.toPixels(next.y);
						g.drawLine(xFrom, yFrom, xTo, yTo);
					}
					previous = next;
				}
			}
		}

		public class CoordTransform {

			private final double inMin;
			private final double factor;
			private final double outMin;
			private final int reversedDelta;

			public CoordTransform(double inMin, double inMax, int outMin,
					int outMax) {
				this(inMin, inMax, outMin, outMax, 0);
			}

			public CoordTransform(double inMin, double inMax, int outMin,
					int outMax, int reversedDelta) {
				this.inMin = inMin;
				this.outMin = outMin;
				this.factor = (double) (outMax - outMin) / (inMax - inMin);
				this.reversedDelta = reversedDelta;
			}

			public int toPixels(double coord) {
				int pixels = (int) ((coord - inMin) * factor + outMin);
				if (reversedDelta == 0) {
					return pixels;
				} else {
					return reversedDelta - pixels;
				}
			}
		}
	}
}
