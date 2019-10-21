package com.hbm.render.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.lib.RefStrings;
import com.hbm.render.amlfrom1710.Vec3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;

public class RenderFallout extends Render<EntityFalloutRain> {

	private Minecraft mc;
	private Random random = new Random();
	float[] rainXCoords;
	float[] rainYCoords;
	private int rendererUpdateCount;
	long lastTime = System.nanoTime();
	private static final ResourceLocation falloutTexture = new ResourceLocation(RefStrings.MODID, "textures/entity/fallout.png");
	
	public RenderFallout(RenderManager renderManager) {
		super(renderManager);
		this.mc = Minecraft.getMinecraft();
		this.rainXCoords = new float[1024];
		this.rainYCoords = new float[1024];
	}
	
	@Override
	public void doRender(EntityFalloutRain entity, double x, double y, double z, float entityYaw, float partialTicks) {

		Entity ent = this.mc.getRenderViewEntity();
		Vec3 vector = Vec3.createVectorHelper(ent.posX - entity.posX,
				ent.posY - entity.posY, ent.posZ - entity.posZ);
		
		double d = vector.lengthVector();
		
		if (d <= entity.getScale()) {
			rendererUpdateCount++;
			long time = System.nanoTime();
			float t = (time - lastTime) / 50000000;
			if (t <= 1.0F)
				renderRainSnow(t);
			else
				renderRainSnow(1.0F);

			lastTime = time;
		}
	}
	
	protected void renderRainSnow(float p_78474_1_) {
		MutableBlockPos pos = new BlockPos.MutableBlockPos();
		IRenderHandler renderer = null;
		if ((renderer = this.mc.world.provider.getWeatherRenderer()) != null) {
			renderer.render(p_78474_1_, this.mc.world, mc);
			return;
		}

		// float f1 = this.mc.theWorld.getRainStrength(p_78474_1_);

		float f1 = 1;

		if (f1 > 0.0F) {
			// this.enableLightmap((double)p_78474_1_);

			if (this.rainXCoords == null) {
				this.rainXCoords = new float[1024];
				this.rainYCoords = new float[1024];

				for (int i = 0; i < 32; ++i) {
					for (int j = 0; j < 32; ++j) {
						float f2 = j - 16;
						float f3 = i - 16;
						float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
						this.rainXCoords[i << 5 | j] = -f3 / f4;
						this.rainYCoords[i << 5 | j] = f2 / f4;
					}
				}
			}

			Entity entitylivingbase = this.mc.getRenderViewEntity();
			WorldClient worldclient = this.mc.world;
			int k2 = MathHelper.floor(entitylivingbase.posX);
			int l2 = MathHelper.floor(entitylivingbase.posY);
			int i3 = MathHelper.floor(entitylivingbase.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			double d0 = entitylivingbase.lastTickPosX
					+ (entitylivingbase.posX - entitylivingbase.lastTickPosX) * p_78474_1_;
			double d1 = entitylivingbase.lastTickPosY
					+ (entitylivingbase.posY - entitylivingbase.lastTickPosY) * p_78474_1_;
			double d2 = entitylivingbase.lastTickPosZ
					+ (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * p_78474_1_;
			int k = MathHelper.floor(d1);
			byte b0 = 5;

			if (this.mc.gameSettings.fancyGraphics) {
				b0 = 10;
			}

			boolean flag = false;
			byte b1 = -1;
			float f5 = this.rendererUpdateCount + p_78474_1_;

			if (this.mc.gameSettings.fancyGraphics) {
				b0 = 10;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			flag = false;

			for (int l = i3 - b0; l <= i3 + b0; ++l) {
				for (int i1 = k2 - b0; i1 <= k2 + b0; ++i1) {
					int j1 = (l - i3 + 16) * 32 + i1 - k2 + 16;
					float f6 = this.rainXCoords[j1] * 0.5F;
					float f7 = this.rainYCoords[j1] * 0.5F;
					pos.setPos(i1, 50, l);
					Biome biomegenbase = worldclient.getBiomeForCoordsBody(pos);

					if (true) {
						int k1 = worldclient.getPrecipitationHeight(pos).getY();
						int l1 = l2 - b0;
						int i2 = l2 + b0;

						if (l1 < k1) {
							l1 = k1;
						}

						if (i2 < k1) {
							i2 = k1;
						}

						float f8 = 1.0F;
						int j2 = k1;

						if (k1 < k) {
							j2 = k;
						}

						if (l1 != i2) {
							pos.setY(l1);
							this.random.setSeed(i1 * i1 * 3121 + i1 * 45238971 ^ l * l * 418711 + l * 13761);
							float f9 = biomegenbase.getTemperature(pos);
							float f10;
							double d4;

							/*
							 * if (false) { if (b1 != 0) { if (b1 >= 0) {
							 * tessellator.draw(); }
							 * 
							 * b1 = 0;
							 * this.mc.getTextureManager().bindTexture(this.
							 * falloutTexture); tessellator.startDrawingQuads();
							 * //System.out.println("Called!"); }
							 * 
							 * f10 = ((float)(this.rendererUpdateCount + i1 * i1
							 * * 3121 + i1 * 45238971 + l * l * 418711 + l *
							 * 13761 & 31) + p_78474_1_) / 32.0F * (3.0F +
							 * this.random.nextFloat()); double d3 =
							 * (double)((float)i1 + 0.5F) -
							 * entitylivingbase.posX; d4 = (double)((float)l +
							 * 0.5F) - entitylivingbase.posZ; float f12 =
							 * MathHelper.sqrt_double(d3 * d3 + d4 * d4) /
							 * (float)b0; float f13 = 1.0F;
							 * tessellator.setBrightness(worldclient.
							 * getLightBrightnessForSkyBlocks(i1, j2, l, 0));
							 * tessellator.setColorRGBA_F(f13, f13, f13, ((1.0F
							 * - f12 * f12) * 0.5F + 0.5F) * f1);
							 * tessellator.setTranslation(-d0 * 1.0D, -d1 *
							 * 1.0D, -d2 * 1.0D);
							 * tessellator.addVertexWithUV((double)((float)i1 -
							 * f6) + 0.5D, (double)l1, (double)((float)l - f7) +
							 * 0.5D, (double)(0.0F * f8), (double)((float)l1 *
							 * f8 / 4.0F + f10 * f8));
							 * tessellator.addVertexWithUV((double)((float)i1 +
							 * f6) + 0.5D, (double)l1, (double)((float)l + f7) +
							 * 0.5D, (double)(1.0F * f8), (double)((float)l1 *
							 * f8 / 4.0F + f10 * f8));
							 * tessellator.addVertexWithUV((double)((float)i1 +
							 * f6) + 0.5D, (double)i2, (double)((float)l + f7) +
							 * 0.5D, (double)(1.0F * f8), (double)((float)i2 *
							 * f8 / 4.0F + f10 * f8));
							 * tessellator.addVertexWithUV((double)((float)i1 -
							 * f6) + 0.5D, (double)i2, (double)((float)l - f7) +
							 * 0.5D, (double)(0.0F * f8), (double)((float)i2 *
							 * f8 / 4.0F + f10 * f8));
							 * tessellator.setTranslation(0.0D, 0.0D, 0.0D); }
							 * else
							 */
							{
								if (b1 != 1) {
									if (b1 >= 0) {
										tessellator.draw();
									}
									b1 = 1;
									this.mc.getTextureManager().bindTexture(RenderFallout.falloutTexture);
									tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);;
								}

								f10 = ((this.rendererUpdateCount & 511) + p_78474_1_) / 512.0F;
								float f16 = this.random.nextFloat() + f5 * 0.01F * (float) this.random.nextGaussian();
								float f11 = this.random.nextFloat() + f5 * (float) this.random.nextGaussian() * 0.001F;
								d4 = i1 + 0.5F - entitylivingbase.posX;
								double d5 = l + 0.5F - entitylivingbase.posZ;
								float f14 = MathHelper.sqrt(d4 * d4 + d5 * d5) / b0;
								float f15 = 1.0F;
								BufferBuilder buf = tessellator.getBuffer();
								int bright = (int)(worldclient.getLightBrightness(pos.setPos(i1, j2, l)) * 3 + 15728880 / 4);
								buf.putBrightness4(bright, bright, bright, bright);
								buf.color(f15, f15, f15, ((1.0F - f14 * f14) * 0.3F + 0.5F) * f1);
								buf.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
								
								buf.pos(i1 - f6 + 0.5D, l1, l - f7 + 0.5D).tex(0.0F * f8 + f16, l1 * f8 / 4.0F + f10 * f8 + f11).endVertex();
								buf.pos(i1 + f6 + 0.5D, l1, l + f7 + 0.5D).tex(1.0F * f8 + f16, l1 * f8 / 4.0F + f10 * f8 + f11).endVertex();
								buf.pos(i1 + f6 + 0.5D, i2, l + f7 + 0.5D).tex(1.0F * f8 + f16, i2 * f8 / 4.0F + f10 * f8 + f11).endVertex();
								buf.pos(i1 - f6 + 0.5D, i2, l - f7 + 0.5D).tex(0.0F * f8 + f16, i2 * f8 / 4.0F + f10 * f8 + f11).endVertex();
								buf.setTranslation(0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}

			if (b1 >= 0) {
				tessellator.draw();
				// System.out.println("Fired!");
			}

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			// this.disableLightmap((double)p_78474_1_);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFalloutRain entity) {
		return null;
	}

}
