package rewrite.villagerhelper;

import rewrite.villagerhelper.config.Configs;
import rewrite.villagerhelper.network.BlockPoiRequestPacket;
import rewrite.villagerhelper.network.BlockPoiResponsePacket;
import rewrite.villagerhelper.network.PoiCache;
import rewrite.villagerhelper.network.SelectedVillagers;
import rewrite.villagerhelper.network.VillagerPoiRequestPacket;
import rewrite.villagerhelper.network.VillagerPoiResponsePacket;
import rewrite.villagerhelper.renderers.PoiRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
public class VillagerHelperMod implements ClientModInitializer {
    public static final VHLogger LOGGER = new VHLogger(VillagerHelperMod.class);
    public static final String MOD_ID = "villagerhelper";

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(VillagerPoiResponsePacket.TYPE, (payload, ctx) ->
            PoiCache.put(payload.entityId(), payload.bedPos(), payload.jobPos())
        );

        ClientPlayNetworking.registerGlobalReceiver(BlockPoiResponsePacket.TYPE, (payload, ctx) -> {
            if (!payload.found()) {
                Minecraft.getInstance().gui.setOverlayMessage(
                    Component.literal("No villager owns this block"), false);
                return;
            }
            boolean nowSelected = SelectedVillagers.toggle(payload.entityId());
            if (nowSelected) {
                PoiCache.put(payload.entityId(), payload.bedPos(), payload.jobPos());
            } else {
                PoiCache.remove(payload.entityId());
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PoiCache.clear();
            SelectedVillagers.clear();
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!Configs.ENABLE) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!(entity instanceof Villager villager)) return InteractionResult.PASS;
            if (!world.isClientSide()) return InteractionResult.PASS;

            int id = villager.getId();
            boolean nowSelected = SelectedVillagers.toggle(id);

            if (nowSelected && ClientPlayNetworking.canSend(VillagerPoiRequestPacket.TYPE)) {
                ClientPlayNetworking.send(new VillagerPoiRequestPacket(id));
            }

            return InteractionResult.SUCCESS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!Configs.ENABLE) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;
            if (!world.isClientSide()) return InteractionResult.PASS;
            if (!PoiTypes.hasPoi(world.getBlockState(hitResult.getBlockPos()))) return InteractionResult.PASS;
            if (ClientPlayNetworking.canSend(BlockPoiRequestPacket.TYPE)) {
                ClientPlayNetworking.send(new BlockPoiRequestPacket(hitResult.getBlockPos()));
            }
            return InteractionResult.PASS;
        });

        LevelRenderEvents.BEFORE_GIZMOS.register(ctx -> {
            if (!Configs.ENABLE) return;
            PoiRenderer.render();
        });
    }
}
