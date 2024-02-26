package com.fx.gamejava;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppWidth;

public class EntityFactory {

    public static Entity createBrick() {
        return FXGL.entityBuilder()
                .type(GameApp.EntityType.BRICK)
                .at(FXGLMath.random(0, getAppWidth() - 64), 0)
                .viewWithBBox("brick.png")
                .with(new CollidableComponent(true))
                .build();
    }

    public static Entity createCoin() {
        return FXGL.entityBuilder()
                .type(GameApp.EntityType.COIN)
                .at(FXGLMath.random(0, getAppWidth() - 15), 0)
                .viewWithBBox(new Circle(15, 15, 15, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }
}
