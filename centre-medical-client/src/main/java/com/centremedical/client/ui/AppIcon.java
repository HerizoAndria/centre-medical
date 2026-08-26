package com.centremedical.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class AppIcon implements Icon {
    public enum Type {
        DASHBOARD,
        DOCTOR,
        PATIENT,
        VISIT,
        ADD,
        EDIT,
        DELETE,
        CLEAR,
        REFRESH,
        LIST,
        CALENDAR,
        CHEVRON_LEFT,
        CHEVRON_RIGHT,
        USERS,
        CHART,
        CLOCK,
        SEARCH
    }

    private final Type type;
    private final int size;
    private final Color fixedColor;

    private AppIcon(Type type, int size, Color fixedColor) {
        this.type = type;
        this.size = size;
        this.fixedColor = fixedColor;
    }

    public static AppIcon of(Type type) {
        return new AppIcon(type, 16, null);
    }

    public static AppIcon of(Type type, int size) {
        return new AppIcon(type, size, null);
    }

    public static AppIcon of(Type type, int size, Color color) {
        return new AppIcon(type, size, color);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(fixedColor != null ? fixedColor : component.getForeground());
        g.setStroke(new BasicStroke(Math.max(1.4f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD -> dashboard(g);
            case DOCTOR -> doctor(g);
            case PATIENT -> patient(g);
            case VISIT -> visit(g);
            case ADD -> add(g);
            case EDIT -> edit(g);
            case DELETE -> delete(g);
            case CLEAR -> clear(g);
            case REFRESH -> refresh(g);
            case LIST -> list(g);
            case CALENDAR -> calendar(g);
            case CHEVRON_LEFT -> chevron(g, true);
            case CHEVRON_RIGHT -> chevron(g, false);
            case USERS -> users(g);
            case CHART -> chart(g);
            case CLOCK -> clock(g);
            case SEARCH -> search(g);
        }
        g.dispose();
    }

    private double s(double value) {
        return value * size / 24.0;
    }

    private void dashboard(Graphics2D g) {
        g.draw(new RoundRectangle2D.Double(s(3), s(3), s(7), s(8), s(2), s(2)));
        g.draw(new RoundRectangle2D.Double(s(14), s(3), s(7), s(5), s(2), s(2)));
        g.draw(new RoundRectangle2D.Double(s(14), s(12), s(7), s(9), s(2), s(2)));
        g.draw(new RoundRectangle2D.Double(s(3), s(15), s(7), s(6), s(2), s(2)));
    }

    private void doctor(Graphics2D g) {
        g.drawOval((int) s(8), (int) s(3), (int) s(8), (int) s(8));
        g.draw(new RoundRectangle2D.Double(s(5), s(13), s(14), s(8), s(4), s(4)));
        g.drawLine((int) s(12), (int) s(14), (int) s(12), (int) s(20));
        g.drawLine((int) s(9), (int) s(17), (int) s(15), (int) s(17));
    }

    private void patient(Graphics2D g) {
        g.drawOval((int) s(8), (int) s(4), (int) s(8), (int) s(8));
        g.draw(new RoundRectangle2D.Double(s(5), s(14), s(14), s(7), s(4), s(4)));
    }

    private void users(Graphics2D g) {
        g.drawOval((int) s(8), (int) s(4), (int) s(7), (int) s(7));
        g.draw(new RoundRectangle2D.Double(s(6), s(14), s(11), s(6), s(4), s(4)));
        g.drawArc((int) s(2), (int) s(8), (int) s(7), (int) s(7), 140, 250);
        g.drawArc((int) s(15), (int) s(8), (int) s(7), (int) s(7), -30, 250);
    }

    private void visit(Graphics2D g) {
        calendar(g);
        g.drawLine((int) s(8), (int) s(14), (int) s(11), (int) s(17));
        g.drawLine((int) s(11), (int) s(17), (int) s(17), (int) s(11));
    }

    private void add(Graphics2D g) {
        g.drawLine((int) s(12), (int) s(5), (int) s(12), (int) s(19));
        g.drawLine((int) s(5), (int) s(12), (int) s(19), (int) s(12));
    }

    private void edit(Graphics2D g) {
        Path2D path = new Path2D.Double();
        path.moveTo(s(5), s(16));
        path.lineTo(s(4), s(20));
        path.lineTo(s(8), s(19));
        path.lineTo(s(18), s(9));
        path.lineTo(s(15), s(6));
        path.closePath();
        g.draw(path);
        g.drawLine((int) s(14), (int) s(7), (int) s(17), (int) s(10));
    }

    private void delete(Graphics2D g) {
        g.drawLine((int) s(5), (int) s(7), (int) s(19), (int) s(7));
        g.drawLine((int) s(9), (int) s(4), (int) s(15), (int) s(4));
        g.draw(new RoundRectangle2D.Double(s(7), s(7), s(10), s(13), s(2), s(2)));
        g.drawLine((int) s(10), (int) s(10), (int) s(10), (int) s(17));
        g.drawLine((int) s(14), (int) s(10), (int) s(14), (int) s(17));
    }

    private void clear(Graphics2D g) {
        g.drawLine((int) s(7), (int) s(7), (int) s(17), (int) s(17));
        g.drawLine((int) s(17), (int) s(7), (int) s(7), (int) s(17));
    }

    private void refresh(Graphics2D g) {
        g.drawArc((int) s(5), (int) s(5), (int) s(14), (int) s(14), 35, 260);
        Path2D arrow = new Path2D.Double();
        arrow.moveTo(s(18), s(5));
        arrow.lineTo(s(18), s(10));
        arrow.lineTo(s(21), s(7));
        g.draw(arrow);
    }

    private void list(Graphics2D g) {
        for (int i = 0; i < 3; i++) {
            int y = (int) s(7 + i * 5);
            g.fillOval((int) s(4), y - 1, (int) s(2), (int) s(2));
            g.drawLine((int) s(9), y, (int) s(20), y);
        }
    }

    private void calendar(Graphics2D g) {
        g.draw(new RoundRectangle2D.Double(s(4), s(5), s(16), s(15), s(2), s(2)));
        g.drawLine((int) s(4), (int) s(10), (int) s(20), (int) s(10));
        g.drawLine((int) s(8), (int) s(3), (int) s(8), (int) s(7));
        g.drawLine((int) s(16), (int) s(3), (int) s(16), (int) s(7));
    }

    private void chevron(Graphics2D g, boolean left) {
        Path2D path = new Path2D.Double();
        if (left) {
            path.moveTo(s(15), s(6));
            path.lineTo(s(9), s(12));
            path.lineTo(s(15), s(18));
        } else {
            path.moveTo(s(9), s(6));
            path.lineTo(s(15), s(12));
            path.lineTo(s(9), s(18));
        }
        g.draw(path);
    }

    private void chart(Graphics2D g) {
        g.drawLine((int) s(4), (int) s(20), (int) s(20), (int) s(20));
        g.drawLine((int) s(4), (int) s(20), (int) s(4), (int) s(5));
        g.fillRoundRect((int) s(7), (int) s(13), (int) s(3), (int) s(6), (int) s(1), (int) s(1));
        g.fillRoundRect((int) s(12), (int) s(9), (int) s(3), (int) s(10), (int) s(1), (int) s(1));
        g.fillRoundRect((int) s(17), (int) s(6), (int) s(3), (int) s(13), (int) s(1), (int) s(1));
    }

    private void clock(Graphics2D g) {
        g.drawOval((int) s(4), (int) s(4), (int) s(16), (int) s(16));
        g.drawLine((int) s(12), (int) s(8), (int) s(12), (int) s(13));
        g.drawLine((int) s(12), (int) s(13), (int) s(16), (int) s(15));
    }

    private void search(Graphics2D g) {
        g.drawOval((int) s(5), (int) s(5), (int) s(10), (int) s(10));
        g.drawLine((int) s(13), (int) s(13), (int) s(20), (int) s(20));
    }
}
