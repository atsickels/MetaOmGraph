package edu.iastate.metnet.simpleui;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StackConstraint extends GridBagConstraints implements ISimpleConstraint {

    protected Dimension gutters;
    protected Dimension container;

    public StackConstraint() {
        this(10, 0);
    }

    public StackConstraint(int paddingWidth, int paddingHeight) {
        super(0, 0,
              0, 0,
              1.5f, 1.5f,
              GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
              new Insets(0, 0, 0, 0),
              paddingWidth, paddingHeight);

        this.gutters = new Dimension(0, 0);

        // NOTE(Johnny): Leave this null by default, we don't wanna use zero-coords.
        this.container = null;
    }

    /**
     * Applies a gutter effect onto the layout.
     *
     * <p>
     * |---------|
     * |    acx |
     * |    xaax|
     * |    xa  |
     * ^
     * \----------- gutter
     *
     * @param dim
     */
    public void applyGutters(Dimension dim) {
        this.gutters = dim;
    }

    /**
     * Applies a container effect onto the layout.
     * <p>
     * |------------|
     * |    acx     |
     * |    xaax    |
     * |    xa      |
     * |---|    |---|
     * ^        ^
     * container
     *
     * @param dim
     */
    public void applyContainer(Dimension dim) {
        this.container = dim;
    }

    Container getRootFrame(Container container) {
        Container parent = container.getParent();
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }

        return parent;
    }


    @Override
    public void beforeInsert(final Container container, final Container preInsertItem) {
        Container root  = getRootFrame(container);
        int       width = root.getWidth(), height = root.getHeight();

        // Gutters/Margins
        this.insets.left   = Math.min(Math.max(this.gutters.width, this.insets.left), width);
        this.insets.right  = Math.min(Math.max(this.gutters.width, this.insets.right), width);
        this.insets.top    = Math.min(Math.max(this.gutters.height, this.insets.top), height);
        this.insets.bottom = Math.min(Math.max(this.gutters.height, this.insets.bottom), height);

        // Containers/Padding
        if (this.container != null) {
            this.insets.left   = Math.max(this.insets.left, width - this.container.width);
            this.insets.right  = Math.max(this.insets.right, width - this.container.width);
            this.insets.top    = Math.max(this.insets.top, height - this.container.height);
            this.insets.bottom = Math.max(this.insets.bottom, height - this.container.height);
        }
    }

    @Override
    public void afterInsert(final Container container, @NotNull final Container postInsertedItem) {

        Dimension size = postInsertedItem.getPreferredSize();

        if (this.ipadx > 0) this.gridx++;
        if (this.ipady > 0) this.gridy++;

        int height = (0 < this.ipady ? size.height : 0);
        int width  = (0 < this.ipadx ? size.width : 0);

        Insets insets = this.insets;
        switch (this.anchor) {
            case GridBagConstraints.NORTHWEST:
            case GridBagConstraints.SOUTHWEST:
                this.insets = new Insets(
                        height + insets.top + this.ipady,
                        width + insets.left + this.ipadx,
                        height + insets.bottom + this.ipady,
                        width + insets.right + this.ipadx
                );
                break;

            case GridBagConstraints.NORTHEAST:
            case GridBagConstraints.SOUTHEAST:
            default:
                this.insets = new Insets(
                        0,
                        0,
                        0,
                        insets.right + width + (ipadx * 2)
                );

                break;
        }
    }
}
