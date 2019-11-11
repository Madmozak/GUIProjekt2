import java.util.Random;

public class Grid {
	private int height;
	private int width;
	private int pixelsPerSquare;
	Food food;

	public Grid(int width, int height, int pixelsPerSquare) {
		this.width = width;
		this.height = height;
		this.pixelsPerSquare = pixelsPerSquare;
		food = new Food(width / 2, height / 2);
	}

	public void reset() {
		food = new Food(width / 2, height / 2);
	}

	public boolean foundFood(Snake snake) {
		boolean isIntersected = false;

		if (snake.getHeadLocation().equals(food.getLocation())) {
			isIntersected = true;
		}

		return isIntersected;
	}

	public void addFood() {
		Random rand = new Random();
		int y = rand.nextInt(height);
		int x = rand.nextInt(width);

		x = Math.round(x / pixelsPerSquare) * pixelsPerSquare;
		y = Math.round(x / pixelsPerSquare) * pixelsPerSquare;

		food = new Food(x, y);
		
	}

	public Food getFood() {
		return food;
	}

}