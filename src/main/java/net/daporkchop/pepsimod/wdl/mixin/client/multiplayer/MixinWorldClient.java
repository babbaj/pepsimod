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

package net.daporkchop.pepsimod.wdl.mixin.client.multiplayer;

import net.daporkchop.pepsimod.wdl.WDLHooks;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient {
    @Inject(method = "tick", at = @At("RETURN"))
    public void postTick(CallbackInfo callbackInfo) {
        WDLHooks.onWorldClientTick(WorldClient.class.cast(this));
    }

    @Inject(method = "doPreChunk", at = @At("HEAD"))
    public void preDoPreChunk(int chunkX, int chunkZ, boolean loadChunk, CallbackInfo callbackInfo) {
        WDLHooks.onWorldClientDoPreChunk(WorldClient.class.cast(this), chunkX, chunkZ, loadChunk);
    }

    @Inject(method = "removeEntityFromWorld", at = @At("HEAD"))
    public void preRemoveEntityFromWorld(int eid, CallbackInfoReturnable<Entity> callbackInfo) {
        WDLHooks.onWorldClientRemoveEntityFromWorld(WorldClient.class.cast(this), eid);
    }
}
