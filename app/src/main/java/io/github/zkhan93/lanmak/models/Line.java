package io.github.zkhan93.lanmak.models;

/**
 * Created by zeeshan on 19/6/17.
 */

public class Line {
    int x1, x2, y1, y2;
    int alpha;
    int stroke;

    final float ALPHA_STEP = 10f;
    final int STROKE_STEP = 2;

    public Line(int x1, int x2, int y1, int y2, int alpha, int stroke) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.alpha = alpha;
        this.stroke = stroke;
    }

    @Override
    public String toString() {
        return "Line{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", alpha=" + alpha +
                ", stroke=" + stroke +
                '}';
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }

    public void decStep() {
        alpha -= ALPHA_STEP;
        stroke -= STROKE_STEP;
    }
}
