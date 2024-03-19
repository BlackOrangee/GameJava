package com.fx.gamejava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getGameScene;


public class GameApp  extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(800);
        settings.setTitle("The wall of death");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.D, () -> {
            if (player.getRightX() < getGameScene().getWidth()) {
                player.translateX(5);
            }
        });

        onKey(KeyCode.A, () -> {
            if (player.getX() > 0) {
                player.translateX(-5);
            }
        });

        onKey(KeyCode.W, () -> {
            if (player.getY() > 0) {
                player.translateY(-5);
            }
        });

        onKey(KeyCode.S, () -> {
            if (player.getBottomY() < getGameScene().getHeight()) {
                player.translateY(5);
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("COINS", 0);
        vars.put("HEALTH", 5);
    }

    private Entity player;

    @Override
    protected void initGame() {
        getGameWorld().addEntity(EntityFactory.createBackground());

        player = entityBuilder()
                .type(EntityType.PLAYER)
                .at(getWindowService().getPrefWidth() / 2, 700)
                .viewWithBBox("ball.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();


       run(() -> getGameWorld().addEntity(
               FXGLMath.random(0, 100) > 20
                       ? EntityFactory.createBrick()
                       : EntityFactory.createCoin()),
               Duration.seconds(0.25));
    }

    /**
     * Initializes the user interface by adding coins, health, and their corresponding textures.
     */
    @Override
    protected void initUI() {
        // Load coin texture and set position
        var coin = getAssetLoader().loadTexture("coin.png");
        coin.setTranslateX(20);
        coin.setTranslateY(20);
        getGameScene().addUINode(coin);

        // Create text for displaying coins and set position and font
        Text coins = new Text();
        coins.setTranslateX(50);
        coins.setTranslateY(44);
        coins.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        coins.setFill(Color.rgb(255, 255, 255));

        // Bind text to the "COINS" property in the WorldProperties
        coins.textProperty().bind(getWorldProperties().intProperty("COINS").asString());

        // Add coins text to the UI
        getGameScene().addUINode(coins);

        // Load heart texture and set position
        var heart = getAssetLoader().loadTexture("heart.png");
        heart.setTranslateX(550);
        heart.setTranslateY(22);
        getGameScene().addUINode(heart);

        // Create text for displaying health and set position and font
        Text health = new Text();
        health.setTranslateX(535);
        health.setTranslateY(44);
        health.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        health.setFill(Color.rgb(255, 255, 255));

        // Bind text to the "HEALTH" property in the WorldProperties
        health.textProperty().bind(getWorldProperties().intProperty("HEALTH").asString());

        // Add health text to the UI
        getGameScene().addUINode(health);
    }

    public enum EntityType {
        PLAYER, COIN, BRICK, BACKGROUND, BLUR
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {

            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.removeFromWorld();
                inc("COINS", +1);
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.BRICK) {
            @Override
            protected void onCollisionBegin(Entity player, Entity brick) {
                IntegerProperty health = getip("HEALTH");
                if (health.get() >  1){
                    inc("HEALTH", -1);
                }else {
                    endGame();
                }
                brick.removeFromWorld();
            }
        });
    }

    private void endGame() {
        getGameWorld().addEntity(EntityFactory.createBlur());

        getGameScene().getUINodes().forEach(n -> n.setVisible(false));

        player.removeFromWorld();

        int coinsCollected = getWorldProperties().getInt("COINS");

        Text gameOverText = new Text("Game Over!");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gameOverText.setFill(Color.valueOf("BB6941FF"));
        gameOverText.setTextAlignment(TextAlignment.CENTER);

        gameOverText.setTranslateX(300 - gameOverText.getLayoutBounds().getWidth() / 2);
        gameOverText.setTranslateY(250 - gameOverText.getLayoutBounds().getHeight() / 2);

        getGameScene().addUINode(gameOverText);

        Text coinsCollectedText = new Text("Score: " + coinsCollected);
        coinsCollectedText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        coinsCollectedText.setFill(Color.valueOf("BB6941FF"));
        coinsCollectedText.setTextAlignment(TextAlignment.CENTER);

        coinsCollectedText.setTranslateX(300 - coinsCollectedText.getLayoutBounds().getWidth() / 2);
        coinsCollectedText.setTranslateY(350 - coinsCollectedText.getLayoutBounds().getHeight() / 2);

        getGameScene().addUINode(coinsCollectedText);
    }

    @Override
    protected void onUpdate(double tpf){
        double windowHeight = getWindowService().getPrefHeight();

        getGameWorld().getEntitiesByType(EntityType.BRICK).forEach(brick -> {
            if (brick.getY() > windowHeight) {
                brick.removeFromWorld();
            } else {
                brick.translateY(350 * tpf);
            }
        });

        getGameWorld().getEntitiesByType(EntityType.COIN).forEach(coin -> {
            if (coin.getY() > windowHeight) {
                coin.removeFromWorld();
            } else {
                coin.translateY(350 * tpf);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}