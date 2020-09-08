package me.towdium.jecalculation.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 16/02/19
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WOverlay extends WContainer {
    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        if (super.onKeyPressed(gui, key, modifier)) return true;
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            gui.root.setOverlay(null);
            return true;
        } else return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!super.onMouseClicked(gui, xMouse, yMouse, button)) gui.root.setOverlay(null);
        return true;
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (!super.onMouseScroll(gui, xMouse, yMouse, diff)) gui.root.setOverlay(null);
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onDraw(MatrixStack matrixStack, JecaGui gui, int mouseX, int mouseY) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, 100);
        super.onDraw(matrixStack, gui, mouseX, mouseY);
        RenderSystem.popMatrix();
        return false;
    }
}
