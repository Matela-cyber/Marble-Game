package org.example.marblecity;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;


public class PlayController3 {
    @FXML
    private Rectangle box;
    @FXML
    private Rectangle box2;
    @FXML
    private Rectangle box1;
    @FXML
    private Rectangle box3;
    @FXML
    private Rectangle box4;
    @FXML
    private Rectangle box5;
    @FXML
    private Label turnLabel; // Label to show whose turn it is
    @FXML
    private Label marblePoints; // Label to display Red's points
    @FXML
    private Label marble2points; // Label to display Blue's points

    private boolean isRedTurn = true; // Tracks the current turn (true for red, false for blue)

    @FXML
    private Circle marble; // The first marble object

    @FXML
    private Circle marble2; // The second marble object

    @FXML
    private Rectangle mainPane; // The main game area

    @FXML
    private Line slingLine; // The first slingshot tension line

    @FXML
    private Line slingLine2; // The second slingshot tension line

    private double initialX, initialY; // Initial marble position for marble
    private double initialX2, initialY2; // Initial marble position for marble2

    private double velocityX, velocityY; // Velocity for marble movement
    private double velocityX2, velocityY2; // Velocity for marble2 movement

    private final double damping = 0.98; // Damping factor to simulate friction
    private boolean isMoving = false; // Marble movement state
    private boolean isMoving2 = false; // Marble2 movement state
    private boolean pointAwarded = false; // New flag

    private int redPoints = 0; // Red player's score
    private int bluePoints = 0; // Blue player's score

    private AnimationTimer animationTimer;

    private void handleCollisionBetweenMarbles() {
        double deltaX = marble2.getLayoutX() - marble.getLayoutX();
        double deltaY = marble2.getLayoutY() - marble.getLayoutY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double combinedRadius = marble.getRadius() + marble2.getRadius();

        if (distance < combinedRadius) {
            // Resolve overlap
            double overlap = combinedRadius - distance;
            double pushX = (deltaX / distance) * overlap / 2;
            double pushY = (deltaY / distance) * overlap / 2;

            marble.setLayoutX(marble.getLayoutX() - pushX);
            marble.setLayoutY(marble.getLayoutY() - pushY);
            marble2.setLayoutX(marble2.getLayoutX() + pushX);
            marble2.setLayoutY(marble2.getLayoutY() + pushY);

            // Collision normal and tangent
            double normalX = deltaX / distance;
            double normalY = deltaY / distance;
            double tangentX = -normalY;
            double tangentY = normalX;

            // Project velocities onto normal and tangent
            double v1n = velocityX * normalX + velocityY * normalY;
            double v1t = velocityX * tangentX + velocityY * tangentY;
            double v2n = velocityX2 * normalX + velocityY2 * normalY;
            double v2t = velocityX2 * tangentX + velocityY2 * tangentY;

            // Swap normal velocities
            double temp = v1n;
            v1n = v2n;
            v2n = temp;

            // Update velocities
            velocityX = v1n * normalX + v1t * tangentX;
            velocityY = v1n * normalY + v1t * tangentY;
            velocityX2 = v2n * normalX + v2t * tangentX;
            velocityY2 = v2n * normalY + v2t * tangentY;

            // Award points based on turn
            if (!pointAwarded) {
                if (!isRedTurn && isMoving) {
                    redPoints++;
                    marblePoints.setText(String.valueOf(redPoints));

                    if (redPoints==12){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("redWin-view.fxml"));
                        Parent root = null;
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Stage stage = (Stage) marble.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();

                    }

                } else if (isRedTurn && isMoving2) {
                    bluePoints++;
                    marble2points.setText(String.valueOf(bluePoints));

                    if (bluePoints==12){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("BlueWin-view.fxml"));
                        Parent root = null;
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Stage stage = (Stage) marble.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    }



                }
                pointAwarded = true; // Prevent further points for the same turn
            }
            // Update movement states
            isMoving = true;
            isMoving2 = true; // Both marbles can move after a collision
        }
    }
    @FXML
    public void initialize() {
        // Store initial positions for both marbles
        initialX = marble.getLayoutX();
        initialY = marble.getLayoutY();
        initialX2 = marble2.getLayoutX();
        initialY2 = marble2.getLayoutY();

        // Set sling lines to be hidden initially
        slingLine.setVisible(false);
        slingLine2.setVisible(false);

        // Initialize points display
        marblePoints.setText(String.valueOf(redPoints));
        marble2points.setText(String.valueOf(bluePoints));

        // Add mouse listeners to the marbles
        marble.setOnMousePressed(this::handleMousePressed);
        marble.setOnMouseDragged(this::handleMouseDragged);
        marble.setOnMouseReleased(this::handleMouseReleased);

        marble2.setOnMousePressed(this::handleMousePressed2);
        marble2.setOnMouseDragged(this::handleMouseDragged2);
        marble2.setOnMouseReleased(this::handleMouseReleased2);

        // Set up the AnimationTimer for smooth movement and collision detection
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isMoving) {
                    updateMarblePosition();
                }
                if (isMoving2) {
                    updateMarblePosition2();
                }
            }
        };
        animationTimer.start();
    }
    // Handle mouse pressed event for the first marble
    private void handleMousePressed(MouseEvent event) {
        if (!isRedTurn || velocityY != 0 || velocityX != 0) { // Block during Blue's turn or while marble is moving
            return;
        }
        slingLine.setVisible(true);
        slingLine.setStartX(marble.getLayoutX());
        slingLine.setStartY(marble.getLayoutY());

    }
    // Handle mouse dragged event for the first marble
    private void handleMouseDragged(MouseEvent event) {
        if (!isRedTurn || velocityY != 0 || velocityX != 0) { // Block during Blue's turn or while marble is moving
            return;
        }
        double deltaX = event.getSceneX() - slingLine.getStartX();
        double deltaY = event.getSceneY() - slingLine.getStartY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 250) {
            double scale = 250 / distance;
            deltaX *= scale;
            deltaY *= scale;
        }
        slingLine.setEndX(slingLine.getStartX() + deltaX);
        slingLine.setEndY(slingLine.getStartY() + deltaY);
    }
    // Handle mouse released event for the first marble
    private void handleMouseReleased(MouseEvent event) {
        if (!isRedTurn || velocityY != 0 || velocityX != 0) { // Block during Blue's turn or while marble is moving
            return;
        }
        slingLine.setVisible(false);

        velocityX = (slingLine.getStartX() - event.getSceneX()) * 0.6;
        velocityY = (slingLine.getStartY() - event.getSceneY()) * 0.6;

        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > 40) {
            double scale = 40 / speed;
            velocityX *= scale;
            velocityY *= scale;
        }
        isRedTurn = false; // Switch to Red's turn
        turnLabel.setText("Blue's Turn");
        isMoving = true;
        pointAwarded = false; // Reset for the new turn
    }
    // Handle mouse pressed event for the second marble
    private void handleMousePressed2(MouseEvent event) {
        if (isRedTurn || velocityY2 != 0 || velocityX2 != 0) { // Block during Red's turn or while marble2 is moving
            return;
        }
        slingLine2.setVisible(true);
        slingLine2.setStartX(marble2.getLayoutX());
        slingLine2.setStartY(marble2.getLayoutY());
    }
    // Handle mouse dragged event for the second marble
    private void handleMouseDragged2(MouseEvent event) {
        if (isRedTurn || velocityY2 != 0 || velocityX2 != 0) { // Block during Red's turn or while marble2 is moving
            return;
        }
        double deltaX = event.getSceneX() - slingLine2.getStartX();
        double deltaY = event.getSceneY() - slingLine2.getStartY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 250) {
            double scale = 250 / distance;
            deltaX *= scale;
            deltaY *= scale;
        }
        slingLine2.setEndX(slingLine2.getStartX() + deltaX);
        slingLine2.setEndY(slingLine2.getStartY() + deltaY);
    }
    // Handle mouse released event for the second marble
    private void handleMouseReleased2(MouseEvent event) {
        if (isRedTurn || velocityY2 != 0 || velocityX2 != 0) { // Block during Red's turn or while marble2 is moving
            return;
        }
        slingLine2.setVisible(false);

        velocityX2 = (slingLine2.getStartX() - event.getSceneX()) * 0.6;
        velocityY2 = (slingLine2.getStartY() - event.getSceneY()) * 0.6;

        double speed = Math.sqrt(velocityX2 * velocityX2 + velocityY2 * velocityY2);
        if (speed > 40) {
            double scale = 40 / speed;
            velocityX2 *= scale;
            velocityY2 *= scale;
        }
        isRedTurn = true; // Switch to Blue's turn
        turnLabel.setText("Red's Turn");
        isMoving2 = true;
        pointAwarded = false; // Reset for the new turn
    }
    @FXML
    private void quit(ActionEvent event) {
        // Close the current stage
        Stage currentStage;
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        try {
            // Load the hello-view.fxml file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("marble city");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log any loading errors
        }
    }
    private void updateMarblePosition() {
        double newX = marble.getLayoutX() + velocityX;
        double newY = marble.getLayoutY() + velocityY;
        // Boundary collision for marble
        if (newX - marble.getRadius() <= mainPane.getLayoutX() || newX + marble.getRadius() >= mainPane.getLayoutX() + mainPane.getWidth()) {
            velocityX = -velocityX * 0.99; // Reverse direction and apply damping
            // Ensure the marble doesn't get stuck by moving it slightly away from the wall
            if (newX - marble.getRadius() <= mainPane.getLayoutX()) {
                newX = mainPane.getLayoutX() + marble.getRadius() + 2;
            } else {
                newX = mainPane.getLayoutX() + mainPane.getWidth() - marble.getRadius() - 2;
            }
        }
        if (newY - marble.getRadius() <= mainPane.getLayoutY() || newY + marble.getRadius() >= mainPane.getLayoutY() + mainPane.getHeight()) {
            velocityY = -velocityY * 0.99; // Reverse direction and apply damping
            // Ensure the marble doesn't get stuck by moving it slightly away from the wall
            if (newY - marble.getRadius() <= mainPane.getLayoutY()) {
                newY = mainPane.getLayoutY() + marble.getRadius() + 2;
            } else {
                newY = mainPane.getLayoutY() + mainPane.getHeight() - marble.getRadius() - 2;
            }
        }
//box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box.getLayoutX();
            double boxRight = box.getLayoutX() + box.getWidth();
            double boxTop = box.getLayoutY();
            double boxBottom = box.getLayoutY() + box.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }

        //box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box1, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box1.getLayoutX();
            double boxRight = box1.getLayoutX() + box1.getWidth();
            double boxTop = box1.getLayoutY();
            double boxBottom = box1.getLayoutY() + box1.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }
        //box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box2, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box2.getLayoutX();
            double boxRight = box2.getLayoutX() + box2.getWidth();
            double boxTop = box2.getLayoutY();
            double boxBottom = box2.getLayoutY() + box2.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }
        //box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box3, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box3.getLayoutX();
            double boxRight = box3.getLayoutX() + box3.getWidth();
            double boxTop = box3.getLayoutY();
            double boxBottom = box3.getLayoutY() + box3.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }
//box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box4, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box4.getLayoutX();
            double boxRight = box4.getLayoutX() + box4.getWidth();
            double boxTop = box4.getLayoutY();
            double boxBottom = box4.getLayoutY() + box4.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }
//box11111111111111111111111111111111111111111111111111111111111111111111
        if (willCollide(box5, marble, velocityX, velocityY)) {
            double marbleCenterX = marble.getLayoutX();
            double marbleCenterY = marble.getLayoutY();
            double boxLeft = box5.getLayoutX();
            double boxRight = box5.getLayoutX() + box5.getWidth();
            double boxTop = box5.getLayoutY();
            double boxBottom = box5.getLayoutY() + box5.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                    velocityX = 0;
                    velocityY = 0;
                    isMoving = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX = -velocityX * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble.setLayoutX(boxLeft - marble.getRadius() - 2);
                } else {
                    marble.setLayoutX(boxRight + marble.getRadius() + 2);
                }
            }
            if (collisionFromTopOrBottom) {
                velocityY = -velocityY * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxTop - marble.getRadius() - 2);
                } else {
                    if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
                        velocityX = 0;
                        velocityY = 0;
                        isMoving = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble.setLayoutY(boxBottom + marble.getRadius() + 2);
                }
            }
        }

        // Apply damping and stop if velocity is low
        velocityX *= damping;
        velocityY *= damping;
        if (Math.abs(velocityX) < 0.4 && Math.abs(velocityY) < 0.4) {
            velocityX = 0;
            velocityY = 0;
            isMoving = false; // Stop movement when velocity is very low
            return;
        }

        // Update position
        marble.setLayoutX(newX);
        marble.setLayoutY(newY);

        // Collision handling
        handleCollisionBetweenMarbles();
    }
    private void updateMarblePosition2() {
        double newX2 = marble2.getLayoutX() + velocityX2;
        double newY2 = marble2.getLayoutY() + velocityY2;

        // Boundary collision for marble2
        if (newX2 - marble2.getRadius() <= mainPane.getLayoutX() || newX2 + marble2.getRadius() >= mainPane.getLayoutX() + mainPane.getWidth()) {
            velocityX2 = -velocityX2 * 0.99; // Reverse direction and apply damping
            // Ensure the marble2 doesn't get stuck by moving it slightly away from the wall
            if (newX2 - marble2.getRadius() <= mainPane.getLayoutX()) {
                newX2 = mainPane.getLayoutX() + marble2.getRadius() + 2;
            } else {
                newX2 = mainPane.getLayoutX() + mainPane.getWidth() - marble2.getRadius() - 2;
            }
        }
        if (newY2 - marble2.getRadius() <= mainPane.getLayoutY() || newY2 + marble2.getRadius() >= mainPane.getLayoutY() + mainPane.getHeight()) {
            velocityY2 = -velocityY2 * 0.99; // Reverse direction and apply damping
            // Ensure the marble2 doesn't get stuck by moving it slightly away from the wall
            if (newY2 - marble2.getRadius() <= mainPane.getLayoutY()) {
                newY2 = mainPane.getLayoutY() + marble2.getRadius() + 2;
            } else {
                newY2 = mainPane.getLayoutY() + mainPane.getHeight() - marble2.getRadius() - 2;
            }
        }
        if (willCollide(box3, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box3.getLayoutX();
            double boxRight = box3.getLayoutX() + box3.getWidth();
            double boxTop = box3.getLayoutY();
            double boxBottom = box3.getLayoutY() + box3.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }
        if (willCollide(box4, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box4.getLayoutX();
            double boxRight = box4.getLayoutX() + box4.getWidth();
            double boxTop = box4.getLayoutY();
            double boxBottom = box4.getLayoutY() + box4.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }
        if (willCollide(box5, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box5.getLayoutX();
            double boxRight = box5.getLayoutX() + box5.getWidth();
            double boxTop = box5.getLayoutY();
            double boxBottom = box5.getLayoutY() + box5.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }
        //boxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        if (willCollide(box, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box.getLayoutX();
            double boxRight = box.getLayoutX() + box.getWidth();
            double boxTop = box.getLayoutY();
            double boxBottom = box.getLayoutY() + box.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }  //boxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        if (willCollide(box1, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box1.getLayoutX();
            double boxRight = box1.getLayoutX() + box1.getWidth();
            double boxTop = box1.getLayoutY();
            double boxBottom = box1.getLayoutY() + box1.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }  //boxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        if (willCollide(box2, marble2, velocityX2, velocityY2)) {
            double marbleCenterX = marble2.getLayoutX();
            double marbleCenterY = marble2.getLayoutY();
            double boxLeft = box2.getLayoutX();
            double boxRight = box2.getLayoutX() + box2.getWidth();
            double boxTop = box2.getLayoutY();
            double boxBottom = box2.getLayoutY() + box2.getHeight();

            // Determine if collision is horizontal or vertical
            boolean collisionFromLeftOrRight = marbleCenterX < boxLeft || marbleCenterX > boxRight;
            boolean collisionFromTopOrBottom = marbleCenterY < boxTop || marbleCenterY > boxBottom;

            if (collisionFromLeftOrRight) {
                if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                    velocityX2 = 0;
                    velocityY2 = 0;
                    isMoving2 = false; // Stop movement when velocity is very low
                    return;
                }
                velocityX2 = -velocityX2 * 0.9999;
                // Prevent sticking
                if (marbleCenterX < boxLeft) {
                    marble2.setLayoutX(boxLeft - marble2.getRadius() - 2);
                } else {
                    marble2.setLayoutX(boxRight + marble2.getRadius() + 2);
                }
            }

            if (collisionFromTopOrBottom) {
                velocityY2 = -velocityY2 * 0.9999;
                // Prevent sticking
                if (marbleCenterY < boxTop) {
                    marble2.setLayoutY(boxTop - marble2.getRadius() - 2);
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                } else {
                    if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
                        velocityX2 = 0;
                        velocityY2 = 0;
                        isMoving2 = false; // Stop movement when velocity is very low
                        return;
                    }
                    marble2.setLayoutY(boxBottom + marble2.getRadius() + 2);
                }

            }
        }
        // Apply damping and stop if velocity is low
        velocityX2 *= damping;
        velocityY2 *= damping;

        if (Math.abs(velocityX2) < 0.4 && Math.abs(velocityY2) < 0.4) {
            velocityX2 = 0;
            velocityY2 = 0;
            isMoving2 = false; // Stop movement when velocity is very low
            return;
        }

        // Update position
        marble2.setLayoutX(newX2);
        marble2.setLayoutY(newY2);

        // Collision handling
        handleCollisionBetweenMarbles();
    }
    private boolean willCollide(Rectangle box, Circle marble, double velocityX, double velocityY) {
        double nextX = marble.getLayoutX() + velocityX;
        double nextY = marble.getLayoutY() + velocityY;

        return nextX + marble.getRadius() >= box.getLayoutX() &&
                nextX - marble.getRadius() <= box.getLayoutX() + box.getWidth() &&
                nextY + marble.getRadius() >= box.getLayoutY() &&
                nextY - marble.getRadius() <= box.getLayoutY() + box.getHeight();
    }
}
