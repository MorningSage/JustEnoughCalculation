package me.towdium.jecalculation.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.gui.JecaGui.COLOR_GUI_GREY;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WIcon extends WTooltip {
    public int xPos, yPos, xSize, ySize;
    public Resource normal, focused;

    public WIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res) {
        this(xPos, yPos, xSize, ySize, res.one, res.two, null);
    }

    public WIcon(int xPos, int yPos, int xSize, int ySize, Resource.ResourceGroup res, String name) {
        this(xPos, yPos, xSize, ySize, res.one, res.two, name);
    }

    private WIcon(int xPos, int yPos, int xSize, int ySize,
                  Resource normal, Resource focused, @Nullable String name) {
        super(name);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.normal = normal;
        this.focused = focused;
    }

    @Override
    public boolean onDraw(MatrixStack matrixStack, JecaGui gui, int xMouse, int yMouse) {
        super.onDraw(matrixStack, gui, xMouse, yMouse);
        gui.drawRectangle(matrixStack, xPos, yPos, xSize, ySize, COLOR_GUI_GREY);
        Resource r = mouseIn(xMouse, yMouse) ? focused : normal;
        gui.drawResource(matrixStack, r, (xSize - r.getXSize()) / 2 + xPos, (ySize - r.getYSize()) / 2 + yPos);
        return false;
    }

    @Override
    public boolean mouseIn(int xMouse, int yMouse) {
        return JecaGui.mouseIn(xPos + (xSize - normal.getXSize()) / 2,
                yPos + (ySize - normal.getYSize()) / 2, normal.getXSize(), normal.getYSize(), xMouse, yMouse);
    }
}
