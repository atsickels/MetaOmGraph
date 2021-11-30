package edu.iastate.metnet.metaomgraph.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class WelcomeHeader extends Header {
    Image icon;

    public WelcomeHeader(String text, Image icon) {
        super(text);
        this.icon = icon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        GradientPaint gradient = new GradientPaint(0.0F, 0.0F, Color.WHITE, 0.0F,
                getHeight(), new Color(255, 255, 255, 0));


        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.drawImage(icon, 10, getHeight() / 2 - icon.getHeight(null) / 2,
                null);
        g2d.setPaint(Color.BLACK);
        g2d.drawString(this.getText(), 15 + icon.getWidth(null), getHeight() / 2 +
                g2d.getFontMetrics().getAscent() / 2);
    }
}
