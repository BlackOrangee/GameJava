package com.fx.gamejava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;


public class GameApp  extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(800);
        settings.setTitle("Basic Game App");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.D, () -> {
            player.translateX(5);
//            inc("pixelsMoved", +5);
        });

        onKey(KeyCode.A, () -> {
            player.translateX(-5);
//            inc("pixelsMoved", +5);
        });

        onKey(KeyCode.W, () -> {
            player.translateY(-5);
//            inc("pixelsMoved", +5);
        });

        onKey(KeyCode.S, () -> {
            player.translateY(5);
//            inc("pixelsMoved", +5);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("COINS", 0);
        vars.put("HEALTH", 3);
    }

    private Entity player;

    public Entity brick;

    @Override
    protected void initGame() {
        player = entityBuilder()
                .type(EntityType.PLAYER)
                .at(getWindowService().getPrefWidth() / 2, 700)
                .viewWithBBox("ball.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();

       run(() -> getGameWorld().addEntity(
               FXGLMath.random(0, 100) > 30
                       ? EntityFactory.createBrick()
                       : EntityFactory.createCoin()),
               Duration.seconds(0.3));
    }

    @Override
    protected void initUI() {
        Circle circle = new Circle(15, 15, 15, Color.YELLOW);
        circle.setTranslateX(20);
        circle.setTranslateY(20);
        getGameScene().addUINode(circle);

        Text textPixels = new Text();
        textPixels.setTranslateX(30);
        textPixels.setTranslateY(39);

        textPixels.textProperty().bind(getWorldProperties().intProperty("COINS").asString());

        getGameScene().addUINode(textPixels);

        var heart = getAssetLoader().loadTexture("heart.png");
        heart.setTranslateX(550);
        heart.setTranslateY(20);
        getGameScene().addUINode(heart);



    }

    public enum EntityType {
        PLAYER, COIN, BRICK
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
                inc("HEALTH", -1);
                brick.removeFromWorld();
            }
        });
    }

    @Override
    protected void onUpdate(double tpf){
        getGameWorld().getEntitiesByType(EntityType.BRICK).forEach(brick -> brick.translateY(350 * tpf));
        getGameWorld().getEntitiesByType(EntityType.COIN).forEach(coin -> coin.translateY(350 * tpf));
//        onIntChange("HEALTH")
    }
    public void updateHealth(){

    }


    public static void main(String[] args) {
        launch(args);
    }
}