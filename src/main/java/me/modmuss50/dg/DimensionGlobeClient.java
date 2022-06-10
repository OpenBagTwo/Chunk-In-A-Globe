package me.modmuss50.dg;

import me.modmuss50.dg.globe.GlobeBlockEntityRenderer;
import me.modmuss50.dg.utils.GlobeSection;
import me.modmuss50.dg.utils.GlobeSectionManagerClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class DimensionGlobeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(DimensionGlobe.globeBlockEntityType, (context) -> new GlobeBlockEntityRenderer());
		ClientPlayNetworking.register(new Identifier(DimensionGlobe.MOD_ID, "section_update"), (packetContext, packetByteBuf) -> {
			final int id = packetByteBuf.readInt();
			final boolean inner = packetByteBuf.readBoolean();
			final boolean blocks = packetByteBuf.readBoolean();
			final NbtCompound data = packetByteBuf.readNbt();

			packetContext.getTaskQueue().execute(() -> {
				final GlobeSection section = GlobeSectionManagerClient.getGlobeSection(id, inner);
				if (blocks) {
					section.fromBlockTag(data);
				} else {
					if (packetContext.getPlayer() != null) {
						section.fromEntityTag(data, packetContext.getPlayer().world);
					}
				}
				GlobeSectionManagerClient.provideGlobeSectionUpdate(inner, id, section);
			});

		});
		GlobeSectionManagerClient.register();
	}
}
