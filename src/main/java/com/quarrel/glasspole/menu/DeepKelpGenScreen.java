package com.quarrel.glasspole.menu;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.quarrel.glasspole.GlassPole;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DeepKelpGenScreen extends AbstractContainerScreen<DeepKelpGenMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GlassPole.MODID, "textures/gui/deepkelpgengui.png");

    public DeepKelpGenScreen(DeepKelpGenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
        
        int energyBarHeight = menu.getEnergy() * 50 / 100000;	// magic numbers bad!
        this.blit(pPoseStack, x+145, y+70-energyBarHeight, 203, 70-energyBarHeight, 12, energyBarHeight);

        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 145, 20, 12, 50)) {
            renderTooltip(pPoseStack, List.of(new TextComponent(menu.getEnergy() + " FE")),
                    Optional.empty(), pMouseX, pMouseY);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int sizeX, int sizeY) {
        return (mouseX >= x && mouseX <= x + sizeX) && (mouseY >= y && mouseY <= y + sizeY);
    }
    
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
    
}