package rewrite.villagerhelper;

import rewrite.villagerhelper.network.BlockPoiRequestPacket;
import rewrite.villagerhelper.network.BlockPoiResponsePacket;
import rewrite.villagerhelper.network.VillagerPoiRequestPacket;
import rewrite.villagerhelper.network.VillagerPoiResponsePacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.Optional;

public class VillagerHelperServer implements ModInitializer {
    private static final VHLogger LOGGER = new VHLogger(VillagerHelperServer.class);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(VillagerPoiRequestPacket.TYPE, VillagerPoiRequestPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VillagerPoiResponsePacket.TYPE, VillagerPoiResponsePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(BlockPoiRequestPacket.TYPE, BlockPoiRequestPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BlockPoiResponsePacket.TYPE, BlockPoiResponsePacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(VillagerPoiRequestPacket.TYPE, (payload, ctx) -> {
            ctx.server().execute(() -> {
                Entity entity = ctx.player().level().getEntity(payload.entityId());
                if (!(entity instanceof Villager villager)) return;

                Optional<BlockPos> bedPos = villager.getBrain()
                    .getMemory(MemoryModuleType.HOME)
                    .map(GlobalPos::pos);

                Optional<BlockPos> jobPos = villager.getBrain()
                    .getMemory(MemoryModuleType.JOB_SITE)
                    .map(GlobalPos::pos);

                ServerPlayNetworking.send(ctx.player(), new VillagerPoiResponsePacket(payload.entityId(), bedPos, jobPos));
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BlockPoiRequestPacket.TYPE, (payload, ctx) -> {
            ctx.server().execute(() -> {
                ServerLevel level = (ServerLevel) ctx.player().level();
                BlockPos target = payload.blockPos();

                level.getEntitiesOfClass(Villager.class, AABB.ofSize(target.getCenter(), 64, 64, 64))
                    .stream()
                    .filter(v -> {
                        Optional<BlockPos> home = v.getBrain().getMemory(MemoryModuleType.HOME).map(GlobalPos::pos);
                        Optional<BlockPos> job  = v.getBrain().getMemory(MemoryModuleType.JOB_SITE).map(GlobalPos::pos);
                        return home.filter(target::equals).isPresent() || job.filter(target::equals).isPresent();
                    })
                    .min(Comparator.comparingDouble((Villager v) -> v.distanceToSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5)))
                    .ifPresentOrElse(villager -> {
                        Optional<BlockPos> bedPos = villager.getBrain().getMemory(MemoryModuleType.HOME).map(GlobalPos::pos);
                        Optional<BlockPos> jobPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).map(GlobalPos::pos);
                        LOGGER.info("Found villager id={} bed={} job={}", villager.getId(), bedPos, jobPos);
                        ServerPlayNetworking.send(ctx.player(), new BlockPoiResponsePacket(true, villager.getId(), bedPos, jobPos));
                    }, () -> {
                        LOGGER.info("No villager found for block {}", target);
                        ServerPlayNetworking.send(ctx.player(), new BlockPoiResponsePacket(false, -1, Optional.empty(), Optional.empty()));
                    });
            });
        });
    }
}
