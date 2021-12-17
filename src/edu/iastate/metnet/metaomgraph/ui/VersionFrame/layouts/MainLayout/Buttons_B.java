package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.simpleui.AbstractComponent;
import edu.iastate.metnet.simpleui.AbstractLayout;
import edu.iastate.metnet.simpleui.ISimpleConstraint;
import edu.iastate.metnet.simpleui.StackConstraint;
import edu.iastate.metnet.simpleui.components.Button;
import edu.iastate.metnet.simpleui.components.Header;
import edu.iastate.metnet.simpleui.components.Label;

import javax.swing.*;
import java.awt.*;

public class Buttons_B extends AbstractLayout {

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint(20, 0);
        constraint.anchor = GridBagConstraints.SOUTHEAST;
        constraint.fill   = GridBagConstraints.NONE;

        return constraint;
    }

    @Override
    public AbstractComponent[] components() {
        return new AbstractComponent[]{
                new Button("Install JDK 11"),
                new Button("Exit")
        };
    }
}
