package net.tclproject.mysteriumlib.asm.fixes;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;

import com.google.common.cache.Cache;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraftforge.common.ForgeChunkManager;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.teresetfix.TEResetFix;

public class MysteriumPatchesFixesTE {

	private static final MethodHandle dormantChunkCacheGet = createDormantChunkCacheGet();

	private static MethodHandle createDormantChunkCacheGet() {
		try {
			Field field2 = ForgeChunkManager.class.getDeclaredField("dormantChunkCache");
			field2.setAccessible(true);
			return MethodHandles.publicLookup().unreflectGetter(field2);
		} catch (Exception e) {
			TEResetFix.logger.error("Cannot get dormantChunkCache. The mod will not work properly and you should report this as a bug.", e);
			return null;
		}
	}

	@Fix(insertOnExit=true)
	public static void fetchDormantChunk(ForgeChunkManager fcm, long coords, World world)
	{
		try {
			Map<World, Cache<Long, Chunk>> dormantChunkCache = null;
			try {
				dormantChunkCache = (Map<World, Cache<Long, Chunk>>) dormantChunkCacheGet.invokeExact();
			} catch (Throwable e) {
				TEResetFix.logger.error("Cannot invoke dormantChunkCache! The mod will not work properly and you should report this as a bug.", e);
			}
			Cache<Long, Chunk> cache = dormantChunkCache.get(world);
			if (cache == null) return;
			cache.invalidate(coords);
		} catch (Exception e) {
			TEResetFix.logger.error("Something went wrong. The mod will not work properly and you should report this as a bug.", e);
		}
	}



// EXAMPLE: (more examples in the official wiki)
	
      /**
       * Target: every time the window is resized, print the new size
       */
//      @Fix
//      @SideOnly(Side.CLIENT)
//      public static void resize(Minecraft mc, int x, int y) {
//          System.out.println("Resize, x=" + x + ", y=" + y);
//     }

}
