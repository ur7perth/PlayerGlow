package com.example.playerglow;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlayerListScreen extends Screen {

    private static final int PER_PAGE = 8;
    private int page = 0;
    private final List<PlayerListEntry> players = new ArrayList<>();

    public PlayerListScreen() {
        super(Text.literal("Player Glow"));
    }

    @Override
    protected void init() {
        players.clear();

        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler != null) {
            for (PlayerListEntry entry : handler.getPlayerList()) {
                if (client.player != null
                        && entry.getProfile().getId().equals(client.player.getUuid())) {
                    continue; // تجاهل نفسك
                }
                players.add(entry);
            }
            players.sort(Comparator.comparing(e -> e.getProfile().getName().toLowerCase()));
        }

        int maxPage = Math.max(0, (players.size() - 1) / PER_PAGE);
        page = Math.max(0, Math.min(page, maxPage));

        int w = 200;
        int x = width / 2 - w / 2;
        int y = 40;

        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, players.size());

        for (int i = start; i < end; i++) {
            PlayerListEntry entry = players.get(i);
            UUID id = entry.getProfile().getId();
            String name = entry.getProfile().getName();
            boolean on = GlowManager.isTracked(id);

            Text label = Text.literal((on ? "✔ " : "") + name)
                    .formatted(on ? Formatting.GREEN : Formatting.WHITE);

            addDrawableChild(ButtonWidget.builder(label, b -> {
                GlowManager.toggle(id);
                clearAndInit();
            }).dimensions(x, y + (i - start) * 24, w, 20).build());
        }

        int by = y + PER_PAGE * 24 + 6;

        ButtonWidget prev = addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> {
            page--;
            clearAndInit();
        }).dimensions(x, by, 40, 20).build());
        prev.active = page > 0;

        ButtonWidget next = addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
            page++;
            clearAndInit();
        }).dimensions(x + w - 40, by, 40, 20).build());
        next.active = page < maxPage;

        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> {
            GlowManager.clear();
            clearAndInit();
        }).dimensions(x + 45, by, 50, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(x + 105, by, 50, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); // يرسم الخلفية والأزرار

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);

        if (players.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("No other players online"), width / 2, 60, 0xAAAAAA);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
