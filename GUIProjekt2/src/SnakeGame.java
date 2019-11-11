import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SnakeGame extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	static long UPDATE = 100;
	static final long DELAY = UPDATE;
	static int TIME;
	static int WINDOW_HEIGHT = 600;
	static int WINDOW_WIDTH = 600;
	static int GRID_BLOCK_SIZE = 20;
	
	ObservableList<String> difficulty = FXCollections.observableArrayList(
			"Easy",
			"Normal",
			"Hard",
			"Insane");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	ComboBox comboDifficulty = new ComboBox(difficulty);
	
	Map<Integer, String> scoresMap = new HashMap<Integer,String>();
	
	private GraphicsContext graphicsContext;
	private Button startButton;
	private Button restartButton;
	private Button menuButton;
	private Button exitButton;
	private Snake snake;
	private Grid grid;
	private AnimationTimer animationTimer;
	private Timer timer;
	private TimerTask task;

	private boolean isGameInProgress = false;
	private boolean isGameOver = false;
	private boolean isPaused = false;

	@Override
	public void start(Stage GameStage) throws Exception {

		StackPane MenuStack = new StackPane();
		Stage MenuStage = new Stage();
		Scene MenuScene = new Scene(MenuStack, 300,300);
		MenuStage.setScene(MenuScene);
		VBox menuBox = new VBox(5);
		
		Button CreateGameButton = new Button("Create Game");
		Button scoresButton = new Button("High Scores");
		Label difficultyLabel = new Label("Difficulty");
		TextField speedText = new TextField();
		Button QuitButton = new Button("Quit");
		MenuStack.getChildren().add(menuBox);
		menuBox.getChildren().add(CreateGameButton);
		menuBox.getChildren().add(scoresButton);
		menuBox.getChildren().add(difficultyLabel);
		menuBox.getChildren().add(comboDifficulty);
		speedText.setPrefSize(100, 20);
		menuBox.getChildren().add(QuitButton);
		menuBox.setAlignment(Pos.CENTER);
		comboDifficulty.getSelectionModel().select(1);

		
		CreateGameButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(comboDifficulty.getValue()=="Easy") {
					UPDATE = 200;
				}
				else if(comboDifficulty.getValue() == "Normal") {
					UPDATE = 100;
				}
				else if(comboDifficulty.getValue() == "Hard") {
					UPDATE = 50;
				}
				else if(comboDifficulty.getValue() == "Insane") {
					UPDATE = 10;
				}
				MenuStage.close();
				GameStage.close();
				GameStage.setTitle("Snake");
				Group root = new Group();
				Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
				graphicsContext = canvas.getGraphicsContext2D();
				root.getChildren().add(canvas);
				Scene scene = new Scene(root);

				grid = new Grid(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
				snake = new Snake(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
				snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);

				drawGrid();

				startButton = new Button("Start!");
				startButton.setMinWidth(100);
				startButton.setMinHeight(36);
				
				restartButton = new Button("Restart!");
				restartButton.setMinWidth(100);
				restartButton.setMinHeight(36);

				menuButton = new Button("Menu");
				menuButton.setMinWidth(100);
				menuButton.setMinHeight(36);
				
				exitButton = new Button("Exit");
				exitButton.setMinWidth(100);
				exitButton.setMinHeight(36);
				
				VBox vBox = new VBox();
				vBox.prefWidthProperty().bind(canvas.widthProperty());
				vBox.prefHeightProperty().bind(canvas.heightProperty());
				vBox.setAlignment(Pos.CENTER);
				vBox.getChildren().add(startButton);
				vBox.getChildren().add(restartButton);
				vBox.getChildren().add(menuButton);
				vBox.getChildren().add(exitButton);
				restartButton.setVisible(false);
				exitButton.setVisible(false);
				menuButton.setVisible(false);
				
				startButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						isGameInProgress = true;
						isGameOver = false;
						startButton.setVisible(false);
						

					
						if (timer == null) {
							task = createTimerTask();
							timer = new Timer("Timer");
							timer.scheduleAtFixedRate(task, DELAY, UPDATE);
							animationTimer.start();
						}
					}
				});
				restartButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						isGameInProgress = true;
						isGameOver = false;
						restartButton.setVisible(false);
						exitButton.setVisible(false);
						menuButton.setVisible(false);
						

					
						if (timer == null) {
							task = createTimerTask();
							timer = new Timer("Timer");
							timer.scheduleAtFixedRate(task, DELAY, UPDATE);
							animationTimer.start();
						}
					}
				});

				menuButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						restartButton.setVisible(false);
						exitButton.setVisible(false);
						menuButton.setVisible(false);
						MenuStage.show();
						GameStage.close();
						
					}
					
				});
				exitButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						GameStage.close();
						
					}
					
				});
				root.getChildren().add(vBox);

				scene.setOnKeyPressed((e) -> {
					if (e.getCode() == KeyCode.UP && Snake.direction != Direction.DOWN) {
						snake.setDirection(Direction.UP);
					} else if (e.getCode() == KeyCode.DOWN && Snake.direction != Direction.UP) {
						snake.setDirection(Direction.DOWN);
					} else if (e.getCode() == KeyCode.LEFT && Snake.direction != Direction.RIGHT) {
						snake.setDirection(Direction.LEFT);
					} else if (e.getCode() == KeyCode.RIGHT && Snake.direction != Direction.LEFT) {
						snake.setDirection(Direction.RIGHT);
					} else if (e.getCode() == KeyCode.P) {
						if (isPaused) {
							task = createTimerTask();
							timer = new Timer("Timer");
							timer.scheduleAtFixedRate(task, DELAY, UPDATE);
							isPaused = false;
						} else {
							timer.cancel();
							isPaused = true;
						}
					}
				});

				GameStage.setScene(scene);
				GameStage.show();

				animationTimer = new AnimationTimer() {
					@Override
					public void handle(long timestamp) {
						if (isGameInProgress) {
							drawGrid();
							drawSnake();
							drawFood();
						} else if (isGameOver) {
							animationTimer.stop();
							showEndGameAlert();
							restartButton.setVisible(true);
							exitButton.setVisible(true);
							menuButton.setVisible(true);
							
							grid.reset();
							snake = new Snake(WINDOW_WIDTH, WINDOW_HEIGHT, GRID_BLOCK_SIZE);
							snake.setHeadLocation(GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
						}
					}
				};
				animationTimer.start();

				task = createTimerTask();
				timer = new Timer("Timer");
				timer.scheduleAtFixedRate(task, DELAY, UPDATE);
				
			}

			private TimerTask createTimerTask() {
				
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						
						if (isGameInProgress) {
							snake.snakeUpdate();
							TIME++;
							
							if (snake.collidedWithWall()) {
								endGame("collided with wall");
							} else if (snake.collidedWithTail()) {
								endGame("collided with tail");
							}

							boolean foundFood = grid.foundFood(snake);
							if (foundFood) {
								snake.addTailSegment();
								grid.addFood();
								
							}
						}
					}
				};
				return task;
			}
			
			private void endGame(String reason) {
				timer.cancel();
				timer = null;
				
				isGameInProgress = false;
				isGameOver = true;
				System.out.println("Game over: " + reason);
				
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						StackPane scorePane = new StackPane();
						Stage scoreStage = new Stage();
						Scene scoreScene = new Scene(scorePane, 200,200);
						scoreStage.setScene(scoreScene);
						scoreStage.setTitle("Game Over");
						VBox scoreBox = new VBox();
						Label scoreLabel = new Label("Score");
						Label scoreNumberLabel = new Label(""+(TIME + (snake.getTail().size() + 1) * (10000/UPDATE)));
						Label nameLabel = new Label("Name");
						TextField nameField = new TextField();
						Button saveButton = new Button("Save Score");
						
						scoreBox.setAlignment(Pos.CENTER);
						scoreBox.getChildren().add(scoreLabel);
						scoreBox.getChildren().add(scoreNumberLabel);
						scoreBox.getChildren().add(nameLabel);
						scoreBox.getChildren().add(nameField);
						scoreBox.getChildren().add(saveButton);
						
						scorePane.getChildren().add(scoreBox);
						scoreStage.show();
						
						saveButton.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								scoresMap.put((int)(Integer.parseInt(scoreNumberLabel.getText())), nameField.getText());
								Map<Integer, String> treeMap = new TreeMap<Integer,String>(
										new Comparator<Integer>() {

											@Override
											public int compare(Integer o1, Integer o2) {
												
												return o2.compareTo(o1);
											}
											
										});
								treeMap.putAll(scoresMap);
								String file = "HighScores.txt";
								try {
									FileWriter fw = new FileWriter(file);
									BufferedWriter bw = new BufferedWriter(fw);
									
									for(Map.Entry<Integer, String> entry : treeMap.entrySet()) {
										
										bw.write(entry.getKey() + "|" + entry.getValue() + "|");
										bw.newLine();
										//bw.flush();
										
										
										System.out.println(entry.getKey() + "|" + entry.getValue());
										
									}
									bw.close();
								} catch (IOException e) {
									
									e.printStackTrace();
									
								}

								scoreStage.close();
								
							}
							
						});
					}
					
				});
			
				
				
			}

			private void showEndGameAlert() {
				String gameOverText = "Game Over! Score: " + (TIME + (snake.getTail().size() + 1) * (10000/UPDATE));
				double textWidth = getTextWidth(gameOverText);
				TIME = 0;

				graphicsContext.setFill(Color.BLACK);
				graphicsContext.fillText(gameOverText, (WINDOW_WIDTH / 2) - (textWidth / 2), WINDOW_HEIGHT / 2 - 24);
			}

			private void drawGrid() {
				graphicsContext.setFill(Color.WHITE);
				graphicsContext.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

				graphicsContext.setStroke(Color.LIGHTGRAY);
				graphicsContext.setLineWidth(0.5);

				for (int x = 0; x < WINDOW_WIDTH; x += GRID_BLOCK_SIZE) {
					graphicsContext.strokeLine(x, 0, x, x + WINDOW_HEIGHT);
				}

				for (int y = 0; y < WINDOW_HEIGHT; y += GRID_BLOCK_SIZE) {
					graphicsContext.strokeLine(0, y, y + WINDOW_WIDTH, y);
				}
			}

			private void drawSnake() {
				graphicsContext.setFill(Color.GREEN);
				graphicsContext.fillRect(snake.getHeadLocation().getX(), snake.getHeadLocation().getY(), snake.getBlockSize(),
						snake.getBlockSize());
				for (Point tailSegment : snake.getTail()) {
					graphicsContext.fillRect(tailSegment.getX(), tailSegment.getY(), snake.getBlockSize(),
							snake.getBlockSize());
				}
			}

			private void drawFood() {
				graphicsContext.setFill(Color.BLUE);
				graphicsContext.fillRect(grid.getFood().getLocation().getX(), grid.getFood().getLocation().getY(),
						GRID_BLOCK_SIZE, GRID_BLOCK_SIZE);
			}

			private double getTextWidth(String string) {
				Text text = new Text(string);
				new Scene(new Group(text));
				text.applyCss();
				return text.getLayoutBounds().getWidth();
			}


					
		});
		

		scoresButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						GridPane stack = new GridPane();
						Label positionLabel = new Label("Pos");
						Label nameLabel = new Label("Name");
						Label scoreLabel = new Label("Score");
						
						ArrayList<TextField> at = new ArrayList<TextField>();
						stack.getColumnConstraints().add(new ColumnConstraints(50));
						stack.getColumnConstraints().add(new ColumnConstraints(150));
						stack.getColumnConstraints().add(new ColumnConstraints(150));
						stack.getRowConstraints().add(new RowConstraints(25));
						for(int i = 1; i<11;i++) {
							for(int j = 1; j<3;j++) {
								TextField textField = new TextField();
								textField.setEditable(false);
								stack.getChildren().add(textField);
								stack.setConstraints(textField, j, i);
								at.add(textField);
							}
							
						}
						for(int i = 1; i<11;i++) {
							Label posLabel = new Label(""+i);
							stack.getChildren().add(posLabel);
							stack.setConstraints(posLabel, 0, i);
							
						}
						stack.setConstraints(positionLabel, 0, 0);
						stack.setConstraints(nameLabel, 2, 0);
						stack.setConstraints(scoreLabel, 1, 0);
						stack.getChildren().addAll(positionLabel, nameLabel, scoreLabel);
						Stage scoreStage = new Stage();
						Scene scoreScene = new Scene(stack,350,300);
						
						
						try {
							BufferedReader br = new BufferedReader(new FileReader("HighScores.txt"));
							String file = "HighScores.txt";
							String read = new String(Files.readAllBytes(Paths.get(new File(file).getAbsolutePath())));
						
								String[] splitedScores = read.split("\\|");
								System.out.println(read);
								//System.out.println(splitedScores[1]);

								for(int i = 0; i<splitedScores.length;i++) {
									at.get(i).setText(splitedScores[i]);
									
								}
								
							

							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						scoreStage.setScene(scoreScene);
						scoreStage.show();
						
						
					}
				
				});
			}
			
		});
		
		QuitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				MenuStage.close();
				
			}
			
		});
		
		MenuStage.show();

		

	
	}
}
	

