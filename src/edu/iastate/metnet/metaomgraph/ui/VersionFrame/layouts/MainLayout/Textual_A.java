package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.metaomgraph.ui.VersionFrame.components.BigHeader;
import edu.iastate.metnet.metaomgraph.ui.VersionFrame.components.FmtVersionLabel;
import edu.iastate.metnet.simpleui.AbstractComponent;
import edu.iastate.metnet.simpleui.AbstractLayout;
import edu.iastate.metnet.simpleui.ISimpleConstraint;
import edu.iastate.metnet.simpleui.StackConstraint;
import edu.iastate.metnet.simpleui.components.Label;

import javax.swing.*;
import java.awt.*;

public class Textual_A extends AbstractLayout {

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint(0, 10);
//        constraint.applyGutters(new Dimension(30, 15));

        return constraint;
    }

    @Override
    public AbstractComponent[] components() {
        Font defaultFont     = new JLabel().getFont();
        Font boldFontVariant = defaultFont.deriveFont(defaultFont.getStyle() | Font.BOLD);
        return new AbstractComponent[]{
                new BigHeader("UNSUPPORTED JAVA VERSION"),
                new FmtVersionLabel("You're using JDK %d. We only support JDK 11 and higher.", boldFontVariant),
                new Label("We can download and setup JDK 11 for you automatically or you can choose to do " +
                          "so manually. If you choose to allow us to automatically setup JDK 11, the binaries will be " +
                          "stored relative to MetaOmGraph.")
        };
    }
}
