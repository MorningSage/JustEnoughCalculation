package me.towdium.jecalculation.gui.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.structure.Recipes;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.Utilities.I18n;
import me.towdium.jecalculation.utils.wrappers.Pair;
import me.towdium.jecalculation.utils.wrappers.Quad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.towdium.jecalculation.gui.Resource.BTN_YES;
import static me.towdium.jecalculation.gui.Resource.ICN_TEXT;

/**
 * Author: Towdium
 * Date: 18-12-6
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GuiImport extends WContainer implements IGui, ISearchable {
    List<Quad<Boolean, String, String, Recipes>> data;  // selected, file, group, recipes
    List<Quad<Boolean, String, String, Recipes>> filtered;
    WSearch search = new WSearch(26, 25, 90, this);
    WSwitcher page;
    WButton confirm = new WButtonIcon(149, 25, 20, 20, BTN_YES, "import.confirm").setDisabled(true)
            .setListener(i -> {
                data.stream().filter(j -> j.one).forEach(j -> Controller.inport(j.four, j.three));
                JecaGui.displayParent();
            });
    List<Pair<WTick, WText>> content = new ArrayList<>();

    public GuiImport() {
        add(new WHelp("import"), new WPanel());
        add(new WIcon(7, 25, 20, 20, ICN_TEXT, "common.search"));
        add(search, confirm);
        IntStream.range(0, 7).forEach(i -> {
            WTick tick = new WTick(7, 49 + 16 * i, 13, 13, "import.tick").setDisabled(true)
                    .setListener(j -> {
                        filtered.get(page.getIndex() * 7 + i).one = j.selected();
                        confirm.setDisabled(data.stream().noneMatch(k -> k.one));
                    });
            WText text = new WTextExpand(49 + 16 * i, "");
            add(tick, text);
            content.add(new Pair<>(tick, text));
        });
    }

    @Override
    public void onVisible(JecaGui gui) {
        data = Controller.discover().stream()
                .flatMap(i -> i.two.stream().map(j -> new Quad<>(false, i.one, j.one, i.two)))
                .collect(Collectors.toList());
        search.refresh();
    }

    public void refresh() {
        for (int i = 0; i < content.size(); i++) {
            Pair<WTick, WText> pair = content.get(i);
            int index = i + 7 * page.getIndex();
            if (index >= filtered.size()) {
                pair.one.setSelected(false).setDisabled(true);
                pair.two.key = "";
            } else {
                Quad<Boolean, String, String, Recipes> quad = filtered.get(index);
                pair.one.setSelected(quad.one).setDisabled(false);
                pair.two.key = quad.two + " -> " + quad.three;
            }
        }
    }

    @Override
    public boolean setFilter(String s) {
        if (s.isEmpty()) filtered = new ArrayList<>(data);
        else filtered = data.stream()
                .filter(i -> I18n.contains(i.two, s) || I18n.contains(i.three, s))
                .collect(Collectors.toList());
        remove(page);
        page = new WSwitcher(7, 7, 162, (filtered.size() + 6) / 7).setListener(i -> refresh());
        add(page);
        refresh();
        return !filtered.isEmpty();
    }

    private static class WTextExpand extends WText {
        boolean expand = false;

        public WTextExpand(int yPos, String key) {
            super(25, yPos + 2, 140, JecaGui.Font.SHADOW, key, false);
        }

        //WRectangle rect = new WRectangle(22, 49 + 16 * i, 146, 13, JecaGui.COLOR_GUI_GREY);
        @Override
        public boolean onDraw(MatrixStack matrixStack, JecaGui gui, int xMouse, int yMouse) {
            gui.drawRectangle(matrixStack, 22, yPos - 2, 146, 13, JecaGui.COLOR_GUI_GREY);
            super.onDraw(matrixStack, gui, xMouse, yMouse);
            if (expand) {
                if (JecaGui.mouseIn(22, yPos - 2, gui.getStringWidth(key) + 6, 13, xMouse, yMouse)) {
                    gui.drawRectangle(matrixStack, 22, yPos - 2, gui.getStringWidth(key) + 6, 13, JecaGui.COLOR_GUI_GREY);
                    gui.drawText(matrixStack, 25, yPos, JecaGui.Font.SHADOW, key);
                } else expand = false;
            } else if (JecaGui.mouseIn(22, yPos - 2, 146, 13, xMouse, yMouse) && gui.getStringWidth(key) > 140) {
                gui.drawRectangle(matrixStack, 22, yPos - 2, gui.getStringWidth(key) + 6, 13, JecaGui.COLOR_GUI_GREY);
                gui.drawText(matrixStack, 25, yPos, JecaGui.Font.SHADOW, key);
                expand = true;
            }
            return false;
        }

        @Override
        public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
            return JecaGui.mouseIn(22, yPos - 2, expand ? gui.getStringWidth(key) + 6 : 146, 13, xMouse, yMouse);
        }
    }
}
