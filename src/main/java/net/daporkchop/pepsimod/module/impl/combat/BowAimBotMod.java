/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2017 Team Pepsi
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it.
 * Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from Team Pepsi.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: Team Pepsi), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.daporkchop.pepsimod.module.impl.combat;

import net.daporkchop.pepsimod.module.ModuleCategory;
import net.daporkchop.pepsimod.module.api.Module;
import net.daporkchop.pepsimod.module.api.ModuleOption;
import net.daporkchop.pepsimod.totally.not.skidded.RenderUtils;
import net.daporkchop.pepsimod.totally.not.skidded.RotationUtils;
import net.daporkchop.pepsimod.util.PepsiUtils;
import net.daporkchop.pepsimod.util.ReflectionStuff;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BowAimBotMod extends Module {
    public static BowAimBotMod INSTANCE;

    {
        INSTANCE = this;
    }

    public EntityLivingBase target;
    public float velocity;

    public BowAimBotMod() {
        super("BowAimBot");
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void onRenderGUI(float partialTicks, int width, int height, GuiIngame gui) {
        if (!FastBowMod.INSTANCE.state.enabled && velocity != -1.0f) {
            if (velocity > 0.0f) {
                gui.drawCenteredString(mc.fontRenderer, "Ready!", width / 2, height / 2 - 20, 16777215);
            } else {
                gui.drawCenteredString(mc.fontRenderer, "Charging...", width / 2, height / 2 - 20, 16740352);
            }
        }
    }

    @Override
    public void onRender(float partialTicks) {
        if (this.target != null) {
            double[] pos = PepsiUtils.interpolate(this.target);
            double x = pos[0] - ReflectionStuff.getRenderPosX();
            double y = pos[1] - ReflectionStuff.getRenderPosY();
            double z = pos[2] - ReflectionStuff.getRenderPosZ();

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-this.target.rotationYaw, 0.0F, 1.0F, 0.0F);
            PepsiUtils.glColor(Color.RED);
            RenderUtils.drawOutlinedBox(new AxisAlignedBB(this.target.width / 2.0D, 0.0D, -(this.target.width / 2.0D), -this.target.width / 2.0D, this.target.height + 0.1D, this.target.width / 2.0D));
            GL11.glPopMatrix();
        }

        this.target = null;
        if (mc.player.inventory.getCurrentItem() != null) {
            if (mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow &&
                    mc.gameSettings.keyBindUseItem.isKeyDown()) {
                this.target = PepsiUtils.getClosestEntityWithoutReachFactor();
                aimAtTarget();
                return;
            }
        }
        this.velocity = -1.0F;
    }

    private void aimAtTarget() {
        if (this.target == null) {
            return;
        }
        this.velocity = ((72000 - mc.player.getItemInUseCount()) / 20.0F);
        this.velocity = ((this.velocity * this.velocity + this.velocity * 2.0F) / 3.0F);
        if (this.velocity > 1.0F) {
            this.velocity = 1.0F;
        }
        if (FastBowMod.INSTANCE.state.enabled) {
            this.velocity = 1.0F;
        }
        if (this.velocity < 0.1D) {
            if ((this.target instanceof EntityLivingBase)) {
                RotationUtils.faceEntityClient(this.target);
                RotationUtils.faceEntityPacket(this.target);
            }
            return;
        }
        if (this.velocity > 1.0F) {
            this.velocity = 1.0F;
        }
        double posX = this.target.posX - mc.player.posX;
        double posY = this.target.posY + this.target.getEyeHeight() - 0.15D -
                mc.player.posY -
                mc.player.getEyeHeight();
        double posZ = this.target.posZ - mc.player.posZ;

        float yaw = (float) (Math.atan2(posZ, posX) * 180.0D / 3.141592653589793D) - 90.0F;
        double y2 = Math.sqrt(posX * posX + posZ * posZ);
        float g = 0.006F;
        float tmp = (float) (this.velocity * this.velocity * this.velocity * this.velocity -
                g * (g * (y2 * y2) + 2.0D * posY * (this.velocity * this.velocity)));
        float pitch = (float) -Math.toDegrees(
                Math.atan((this.velocity * this.velocity - Math.sqrt(tmp)) / (g * y2)));
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    @Override
    public void init() {
        INSTANCE = this;
    }

    @Override
    public ModuleOption[] getDefaultOptions() {
        return new ModuleOption[0];
    }

    public ModuleCategory getCategory() {
        return ModuleCategory.COMBAT;
    }
}
