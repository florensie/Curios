/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.curios.api.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.CurioType;

import javax.annotation.Nonnull;

public interface ICurio {

  /**
   * @param identifier       The {@link CurioType} identifier of the
   *                         *                         ItemStack's slot
   * @param index            The index of the ItemStack's slot
   * @param entityLivingBase The wearer of the ItemStack
   */
  default void onCurioTick(String identifier, int index, LivingEntity entityLivingBase) {

  }

  /**
   * Called every tick while the ItemStack is equipped
   * Deprecated - use index-sensitive version {@link ICurio#onCurioTick(String, int, LivingEntity)}
   *
   * @param identifier       The {@link CurioType} identifier of the
   *                         ItemStack's slot
   * @param entityLivingBase The wearer of the ItemStack
   */
  @Deprecated
  default void onCurioTick(String identifier, LivingEntity entityLivingBase) {

  }

  /**
   * Called when the ItemStack is equipped into a slot
   *
   * @param identifier       The {@link CurioType} identifier of the slot
   *                         being equipped into
   * @param entityLivingBase The wearer of the ItemStack
   */
  default void onEquipped(String identifier, LivingEntity entityLivingBase) {

  }

  /**
   * Called when the ItemStack is unequipped from a slot
   *
   * @param identifier       The {@link CurioType} identifier of the slot
   *                         being unequipped from
   * @param entityLivingBase The wearer of the ItemStack
   */
  default void onUnequipped(String identifier, LivingEntity entityLivingBase) {

  }

  /**
   * Determines if the ItemStack can be equipped into a slot
   *
   * @param identifier       The {@link CurioType} identifier of the slot
   *                         being equipped into
   * @param entityLivingBase The wearer of the ItemStack
   * @return True if the ItemStack can be equipped/put in, false if not
   */
  default boolean canEquip(String identifier, LivingEntity entityLivingBase) {

    return true;
  }

  /**
   * Determines if the ItemStack can be unequipped from a slot
   *
   * @param identifier       The {@link CurioType} identifier of the slot
   *                         being unequipped from
   * @param entityLivingBase The wearer of the ItemStack
   * @return True if the ItemStack can be unequipped/taken out, false if not
   */
  default boolean canUnequip(String identifier, LivingEntity entityLivingBase) {

    return true;
  }

  /**
   * A map of AttributeModifier associated with the ItemStack and the
   * {@link CurioType} identifier
   *
   * @param identifier The CurioType identifier for the context
   * @return A map of attribute modifiers to apply
   */
  default Multimap<String, AttributeModifier> getAttributeModifiers(String identifier) {

    return HashMultimap.create();
  }

  /**
   * Plays a sound server-side when a curio is equipped from right-clicking the
   * ItemStack in hand.
   * This can be overridden to play nothing, but it is advised to always play
   * something as an auditory feedback for players.
   *
   * @param entityLivingBase The wearer of the ItemStack
   */
  default void playEquipSound(LivingEntity entityLivingBase) {

    entityLivingBase.world.playSound(null, entityLivingBase.getPosition(),
                                     SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL,
                                     1.0f, 1.0f);
  }

  /**
   * Determines if the ItemStack can be automatically equipped into the first
   * available slot when right-clicked.
   *
   * @return True to enable right-clicking auto-equip, false to disable
   */
  default boolean canRightClickEquip() {

    return false;
  }


  /**
   * Called when rendering break animations and sounds client-side when a worn
   * curio item is broken
   *
   * @param stack        The ItemStack that was broken
   * @param livingEntity The entity that broke the curio
   */
  default void renderBrokenCurio(ItemStack stack, LivingEntity livingEntity) {

    if (!stack.isEmpty()) {

      if (!livingEntity.isSilent()) {
        livingEntity.world.playSound(livingEntity.posX, livingEntity.posY, livingEntity.posZ,
                                     SoundEvents.ENTITY_ITEM_BREAK, livingEntity.getSoundCategory(),
                                     0.8F, 0.8F + livingEntity.world.rand.nextFloat() * 0.4F,
                                     false);
      }

      for (int i = 0; i < 5; ++i) {
        Vec3d vec3d = new Vec3d(((double) livingEntity.getRNG().nextFloat() - 0.5D) * 0.1D,
                                Math.random() * 0.1D + 0.1D, 0.0D);
        vec3d = vec3d.rotatePitch(-livingEntity.rotationPitch * ((float) Math.PI / 180F));
        vec3d = vec3d.rotateYaw(-livingEntity.rotationYaw * ((float) Math.PI / 180F));
        double d0 = (double) (-livingEntity.getRNG().nextFloat()) * 0.6D - 0.3D;
        Vec3d vec3d1 =
            new Vec3d(((double) livingEntity.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
        vec3d1 = vec3d1.rotatePitch(-livingEntity.rotationPitch * ((float) Math.PI / 180F));
        vec3d1 = vec3d1.rotateYaw(-livingEntity.rotationYaw * ((float) Math.PI / 180F));
        vec3d1 =
            vec3d1.add(livingEntity.posX, livingEntity.posY + (double) livingEntity.getEyeHeight(),
                       livingEntity.posZ);

        if (livingEntity.world instanceof ServerWorld) {
          ((ServerWorld) livingEntity.world).spawnParticle(
              new ItemParticleData(ParticleTypes.ITEM, stack), vec3d1.x, vec3d1.y, vec3d1.z, 1,
              vec3d.x, vec3d.y + 0.05D, vec3d.z, 0.0D);
        } else {
          livingEntity.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), vec3d1.x,
                                         vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }
      }
    }
  }

  /**
   * Compares the current ItemStack and the previous ItemStack in the slot to
   * detect any changes and returns true if the change should be synced to all
   * tracking clients.
   * Note that this check occurs every tick so implementations need to code
   * their own timers for other intervals.
   *
   * @param identifier       The identifier of the {@link CurioType} of the slot
   * @param entityLivingBase The EntityLivingBase that is wearing the ItemStack
   * @return True to curios the ItemStack change to all tracking clients, false
   * to do nothing
   */
  default boolean shouldSyncToTracking(String identifier, LivingEntity entityLivingBase) {

    return false;
  }

  /**
   * Gets a tag that is used to sync extra curio data from the server to the
   * client.
   * Only used when {@link ICurio#shouldSyncToTracking(String, LivingEntity)}
   * returns true.
   *
   * @return Data to be sent to the client
   */
  @Nonnull
  default CompoundNBT getSyncTag() {

    return new CompoundNBT();
  }

  /**
   * Used client-side to read data tags created by {@link ICurio#getSyncTag()}
   * received from the server.
   *
   * @param compound Data received from the server
   */
  default void readSyncTag(CompoundNBT compound) {

  }

  /**
   * Determines if the ItemStack has rendering
   *
   * @param identifier       The identifier of the {@link CurioType} of the slot
   * @param entityLivingBase The EntityLivingBase that is wearing the ItemStack
   * @return True if the ItemStack has rendering, false if it does not
   */
  default boolean hasRender(String identifier, LivingEntity entityLivingBase) {

    return false;
  }

  /**
   * Performs rendering of the ItemStack if {@link ICurio#hasRender(String, LivingEntity)}
   * returns true.
   * Note that vertical sneaking translations are automatically applied before
   * this rendering method is called.
   *
   * @param identifier         The identifier of the {@link CurioType} of the slot
   * @param entitylivingbaseIn The EntityLivingBase that is wearing the ItemStack
   */
  default void doRender(String identifier, LivingEntity entitylivingbaseIn, float limbSwing,
                        float limbSwingAmount, float partialTicks, float ageInTicks,
                        float netHeadYaw, float headPitch, float scale) {

  }

  /**
   * Some helper methods for rendering curios
   */
  final class RenderHelper {

    /**
     * Rotates the rendering for the curio if the entity is sneaking.
     * The rotation angle is based on the body of a player model when sneaking,
     * so this is typically used for items being rendered on the body.
     *
     * @param entitylivingbaseIn The wearer of the curio
     */
    public static void rotateIfSneaking(final LivingEntity entitylivingbaseIn) {

      if (entitylivingbaseIn.isSneaking()) {
        GlStateManager.rotatef(90.0F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
      }
    }

    /**
     * Rotates the rendering for the model renderers based on the entity's head
     * movement.
     * This will align the model renderers with the movements and rotations of
     * the head.
     * This will do nothing if the entity render object does not implement
     * {@link LivingRenderer} or if the model does not have a head (does not
     * implement {@link BipedModel}).
     *
     * @param entitylivingbaseIn The wearer of the curio
     * @param renderers          The list of model renderers to align to the
     *                           head movement
     */
    public static void followHeadRotations(final LivingEntity entitylivingbaseIn,
                                           RendererModel... renderers) {

      EntityRenderer render =
          Minecraft.getInstance().getRenderManager().getRenderer(entitylivingbaseIn);

      if (render instanceof LivingRenderer) {
        EntityModel model = ((LivingRenderer) render).getEntityModel();

        if (model instanceof BipedModel) {

          for (RendererModel renderer : renderers) {
            renderer.copyModelAngles(((BipedModel) model).bipedHead);
          }
        }
      }
    }
  }
}
