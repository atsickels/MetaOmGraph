package edu.iastate.metnet.metaomgraph.ui.VersionFrame.layouts.MainLayout;

import edu.iastate.metnet.simpleui.*;

import java.awt.*;

public class Manifest extends AbstractLayout {

    public ISimpleConstraint constraint() {
        StackConstraint constraint = new StackConstraint();
        constraint.applyGutters(new Dimension(30, 30));
        constraint.anchor = GridBagConstraints.NORTHWEST;
        constraint.fill   = GridBagConstraints.BOTH;

        return constraint;
    }

    @Override
    public AbstractComponent[] components() {
        return new AbstractComponent[]{
                new Textual_A(),
                new Buttons_B()
        };
    }
}
