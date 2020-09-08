package me.towdium.jecalculation.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WContainer implements IContainer {
    protected List<IWidget> widgets = new ArrayList<>();
    protected IWidget overlay = null;

    public void add(IWidget w) {
        widgets.add(w);
    }

    public void add(IWidget... w) {
        if (w.length == 1) widgets.add(w[0]);
        else widgets.addAll(Arrays.asList(w));
    }

    public void remove(IWidget... w) {
        if (w.length == 1) widgets.remove(w[0]);
        else widgets.removeAll(Arrays.asList(w));
    }

    public void setOverlay(IWidget overlay) {
        this.overlay = overlay;
    }

    public void clear() {
        widgets.clear();
    }

    public boolean contains(IWidget w) {
        return widgets.contains(w);
    }

    @Override
    public boolean onDraw(MatrixStack matrixStack, JecaGui gui, int mouseX, int mouseY) {
        IWidget[] w = new IWidget[1];
        widgets.forEach(i -> {
            if (i.onDraw(matrixStack, gui, mouseX, mouseY)) w[0] = i;
        });
        if (w[0] != null) w[0].onDraw(matrixStack, gui, mouseX, mouseY);
        if (overlay != null) overlay.onDraw(matrixStack, gui, mouseX, mouseY);
        return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseClicked(gui, xMouse, yMouse, button);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseClicked(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        boolean b = overlay != null && overlay.onKeyPressed(gui, key, modifier);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onKeyPressed(gui, key, modifier));
    }

    @Override
    public boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        boolean b = overlay != null && overlay.onKeyReleased(gui, key, modifier);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onKeyReleased(gui, key, modifier));
    }

    @Override
    public boolean onMouseReleased(JecaGui gui, int xMouse, int yMouse, int button) {
        boolean b = overlay != null && overlay.onMouseReleased(gui, xMouse, yMouse, button);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseReleased(gui, xMouse, yMouse, button));
    }

    @Override
    public boolean onChar(JecaGui gui, char ch, int modifier) {
        boolean b = overlay != null && overlay.onChar(gui, ch, modifier);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onChar(gui, ch, modifier));
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        boolean b = overlay != null && overlay.onMouseScroll(gui, xMouse, yMouse, diff);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseScroll(gui, xMouse, yMouse, diff));
    }

    @Override
    public boolean onMouseDragged(JecaGui gui, int xMouse, int yMouse, int xDrag, int yDrag) {
        boolean b = overlay != null && overlay.onMouseDragged(gui, xMouse, yMouse, xDrag, yDrag);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onMouseDragged(gui, xMouse, yMouse, xDrag, yDrag));
    }

    @Override
    public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
        boolean b = overlay != null && overlay.onTooltip(gui, xMouse, yMouse, tooltip);
        return b || new Utilities.ReversedIterator<>(widgets).stream()
                .anyMatch(i -> i.onTooltip(gui, xMouse, yMouse, tooltip));
    }

    @Nullable
    @Override
    public WLabel getLabelUnderMouse(int xMouse, int yMouse) {
        return new Utilities.ReversedIterator<>(widgets).stream()
                .map(i -> i.getLabelUnderMouse(xMouse, yMouse))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }
}
