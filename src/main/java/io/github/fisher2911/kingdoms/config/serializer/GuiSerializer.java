package io.github.fisher2911.kingdoms.config.serializer;

import io.github.fisher2911.kingdoms.gui.BaseGuiItem;
import io.github.fisher2911.kingdoms.gui.Gui;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiSerializer {

    private static final String ID_PATH = "id";
    private static final String TITLE_PATH = "title";
    private static final String ROWS_PATH = "rows";
    private static final String BORDER_ITEMS_PATH = "borders";
    private static final String ITEMS_PATH = "items";
    private static final boolean CANCEL_CLICKS_PATH = true;

    private static final String GUI_FILLERS_PATH = "gui-fillers";

    public static Gui.Builder deserialize(ConfigurationNode source) throws SerializationException {
        final String id = source.node(ID_PATH).getString();
        final String title = source.node(TITLE_PATH).getString("Kingdoms");
        final int rows = source.node(ROWS_PATH).getInt();
        final boolean cancelClicks = source.node(CANCEL_CLICKS_PATH).getBoolean();
        final var borderNode = source.node(BORDER_ITEMS_PATH);
        final List<BaseGuiItem> borders = new ArrayList<>();
        for (var entry : borderNode.childrenMap().entrySet()) {
            borders.add(GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue()));
        }
        final var itemsNode = source.node(ITEMS_PATH);
        final Map<Integer, BaseGuiItem> items = new HashMap<>();
        for (var entry : itemsNode.childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final Integer slot)) {
                continue;
            }
            items.put(slot, GuiItemSerializer.INSTANCE.deserialize(BaseGuiItem.class, entry.getValue()));
        }

        final var guiFillersNode = source.node(GUI_FILLERS_PATH);


        return Gui.builder(id)
                .name(title)
                .rows(rows)
                .items(items)
//                .filler(filler)
                .border(borders)
                .cancelAllClicks(cancelClicks);
    }

}
