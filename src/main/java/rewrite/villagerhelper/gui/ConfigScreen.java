package rewrite.villagerhelper.gui;

import rewrite.villagerhelper.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("villagerhelper.gui.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int btnWidth = 200;
        int x = this.width / 2 - btnWidth / 2;
        int y = 60;

        addRenderableWidget(Button.builder(
            Component.translatable("villagerhelper.gui.config.toggle", boolText(Configs.ENABLE)),
            button -> {
                Configs.ENABLE = !Configs.ENABLE;
                button.setMessage(Component.translatable("villagerhelper.gui.config.toggle", boolText(Configs.ENABLE)));
            }
        ).bounds(x, y, btnWidth, 20).build());

        y += 25;
        addRenderableWidget(Button.builder(
            CommonComponents.GUI_DONE,
            button -> onClose()
        ).bounds(x, y, btnWidth, 20).build());
    }

    private static Component boolText(boolean b) {
        return b
            ? CommonComponents.OPTION_ON.copy().withStyle(ChatFormatting.GREEN)
            : CommonComponents.OPTION_OFF.copy().withStyle(ChatFormatting.RED);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(font, title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }


}
