package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DEntryGroup implements IDrawable {
    ArrayList<DEntry> widgets = new ArrayList<>();

    public DEntryGroup(int xPos, int yPos, int column, int row, DEntry.enumMode mode) {
        this(xPos, yPos, column, row, 18, 18, mode);
    }

    public DEntryGroup(int xPos, int yPos, int column, int row, int xSize, int ySize, DEntry.enumMode mode) {
        IntStream.range(0, column).forEach(c -> IntStream.range(0, row).forEach(r ->
                widgets.add(new DEntry(xPos + c * xSize, yPos + r * ySize, xSize, ySize, mode))));
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        widgets.forEach(w -> w.onDraw(gui, xMouse, yMouse));
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return widgets.stream().anyMatch(w -> w.onClicked(gui, xMouse, yMouse, button));
    }

    public Optional<DEntry> getEntryAt(JecGui gui, int xMouse, int yMouse) {
        return widgets.stream().filter(w -> w.mouseIn(gui, xMouse, yMouse)).findFirst();
    }
}