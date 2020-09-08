package me.towdium.jecalculation.data.label.labels;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.ILabel.Serializer.SerializationException;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   8/11/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LItemStack extends LStack<Item> {
    public static final String IDENTIFIER = "itemStack";

    public static final String KEY_ITEM = "item";
    public static final String KEY_NBT = "nbt";
    public static final String KEY_CAP = "cap";
    public static final String KEY_F_META = "fMeta";
    public static final String KEY_F_CAP = "fCap";
    public static final String KEY_F_NBT = "fNbt";

    Item item;
    CompoundNBT nbt;
    CompoundNBT cap;
    boolean fMeta;
    boolean fNbt;
    boolean fCap;
    transient ItemStack rep;

    // Convert from itemStack
    public LItemStack(ItemStack is) {
        super(is.getCount(), false);
        init(is.getItem(), getCap(is), is.getTag(), false, false, false);
    }

    public LItemStack(CompoundNBT tag) {
        super(tag);
        String id = tag.getString(KEY_ITEM);
        Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (i == null) throw new SerializationException("Item " + id + " cannot be resolved, ignoring");
        init(i, tag.contains(KEY_CAP) ? tag.getCompound(KEY_CAP) : null,
                tag.contains(KEY_NBT) ? tag.getCompound(KEY_NBT) : null,
                tag.getBoolean(KEY_F_META),
                tag.getBoolean(KEY_F_CAP),
                tag.getBoolean(KEY_F_NBT)
        );
    }

    @Override
    public Item get() {
        return item;
    }

    @Override
    public Context<Item> getContext() {
        return Context.ITEM;
    }

    private LItemStack(LItemStack lis) {
        super(lis);
        item = lis.item;
        nbt = lis.nbt == null ? null : lis.nbt.copy();
        cap = lis.cap == null ? null : lis.cap.copy();
        fMeta = lis.fMeta;
        fNbt = lis.fNbt;
        fCap = lis.fCap;
        rep = lis.rep;
    }

    private void init(@Nullable Item item, @Nullable CompoundNBT cap,
                      @Nullable CompoundNBT nbt, boolean fMeta, boolean fCap, boolean fNbt) {
        Objects.requireNonNull(item);
        this.item = item;
        this.cap = cap;
        this.nbt = nbt;
        this.fMeta = fMeta;
        this.fCap = fCap;
        this.fNbt = fNbt;
        rep = new ItemStack(item, 1, this.cap);
        rep.setTag(this.nbt);
    }

    @Nullable
    private static CompoundNBT getCap(ItemStack is) {
        CompoundNBT nbt = is.serializeNBT();
        return nbt.contains("ForgeCaps") ? nbt.getCompound("ForgeCaps") : null;
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LItemStack && b instanceof LItemStack) {
            LItemStack lisA = (LItemStack) a;
            LItemStack lisB = (LItemStack) b;

            if (lisA.rep.getDamage() != lisB.rep.getDamage()
                    && !lisA.fMeta && !lisB.fMeta) return false;
            if (!lisA.fNbt && !lisB.fNbt) {
                if (lisA.nbt == null) {
                    if (lisB.nbt != null) return false;
                } else if (lisB.nbt == null || !lisA.nbt.equals(lisB.nbt)) return false;
            }
            if (!lisA.fCap && !lisB.fCap) {
                if (lisA.cap == null) {
                    if (lisB.cap != null) return false;
                } else if (lisB.cap == null || !lisA.cap.equals(lisB.cap)) return false;
            }
            return lisA.item == lisB.item;
        }
        return false;
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        if (iss.size() == 0) return new ArrayList<>();
        for (ILabel i : iss) if (!(i instanceof LItemStack)) return new ArrayList<>();
        LItemStack lis = (LItemStack) iss.get(0);
        boolean fMeta = false;
        boolean fNbt = false;
        boolean fCap = false;
        for (ILabel i : iss) {
            LItemStack ii = (LItemStack) i;
            if (ii.item != lis.item) return new ArrayList<>();
            if (ii.rep.getDamage() != lis.rep.getDamage() || ii.fMeta) fMeta = true;
            if (!Objects.equals(ii.nbt, lis.nbt)) fNbt = true;
            if (!Objects.equals(ii.cap, lis.cap)) fCap = true;
        }
        if (fMeta || fNbt || fCap) return Collections.singletonList(
                lis.copy().setFCap(fCap).setFMeta(fMeta).setFNbt(fNbt));
        else return new ArrayList<>();
    }

    public static List<ILabel> fallback(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        List<ILabel> ret = new ArrayList<>();
        if (iss.size() == 1) {
            ILabel label = iss.get(0);
            if (!(label instanceof LItemStack)) return ret;
            LItemStack lis = (LItemStack) label;
            if (lis.fCap || lis.fNbt || lis.fMeta) return new ArrayList<>();
            ret.add(lis.copy().setFMeta(true));
            ret.add(lis.copy().setFNbt(true));
            ret.add(lis.copy().setFCap(true));
            ret.add(lis.copy().setFMeta(true).setFNbt(true).setFCap(true));
        }
        return ret;
    }

    public LItemStack setFMeta(boolean f) {
        fMeta = f;
        return this;
    }

    public LItemStack setFNbt(boolean f) {
        fNbt = f;
        return this;
    }

    public LItemStack setFCap(boolean f) {
        fCap = f;
        return this;
    }

    @Override
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        if (fMeta) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_meta"));
        if (fNbt) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_nbt"));
        if (fCap) existing.add(FORMAT_GREY + Utilities.I18n.get("label.item_stack.fuzzy_cap"));
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(item));
    }

    @Override
    public ItemStack getRepresentation() {
        return rep;
    }

    @Override
    public String getDisplayName() {
        return rep.getDisplayName().getString();//.getFormattedText();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LItemStack) {
            LItemStack lis = (LItemStack) l;
            return (nbt == null ? lis.nbt == null : nbt.equals(lis.nbt))
                    && (cap == null ? lis.cap == null : cap.equals(lis.cap))
                    && item == lis.item
                    && fNbt == lis.fNbt && super.matches(l)
                    && fCap == lis.fCap && fMeta == lis.fMeta;
        } else return false;
    }

    @Override
    public LItemStack copy() {
        return new LItemStack(this);
    }

    @Override
    public CompoundNBT toNbt() {
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
        if (rl == null) return ILabel.EMPTY.toNbt();
        CompoundNBT ret = super.toNbt();
        ret.putString(KEY_ITEM, rl.toString());
        if (nbt != null) ret.put(KEY_NBT, nbt);
        if (cap != null) ret.put(KEY_CAP, cap);
        if (fMeta) ret.putBoolean(KEY_F_META, true);
        if (fNbt) ret.putBoolean(KEY_F_NBT, true);
        if (fCap) ret.putBoolean(KEY_F_CAP, true);
        return ret;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawLabel(MatrixStack matrixStack, JecaGui gui) {
        gui.drawItemStack(0, 0, rep, false);
        if (fCap || fNbt || fMeta) gui.drawResource(matrixStack, Resource.LBL_FRAME, 0, 0);
        if (fCap) gui.drawResource(matrixStack, Resource.LBL_FR_LL, 0, 0);
        if (fNbt) gui.drawResource(matrixStack, Resource.LBL_FR_UL, 0, 0);
        if (fMeta) gui.drawResource(matrixStack, Resource.LBL_FR_UR, 0, 0);
    }

    @Override
    public int hashCode() {
        return (nbt == null ? 0 : nbt.hashCode()) ^ (cap == null ? 0 : cap.hashCode())
                ^ item.getTranslationKey().hashCode() ^ super.hashCode();
    }
}
