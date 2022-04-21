package com.lagadd.biocubes.common.entites;

	import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.lagadd.biocubes.common.entites.ai.FlyAroundGoal;
import com.lagadd.biocubes.common.entites.ai.PickUpAttackGoal;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import software.bernie.geckolib3.core.IAnimatable;
	import software.bernie.geckolib3.core.PlayState;
	import software.bernie.geckolib3.core.builder.AnimationBuilder;
	import software.bernie.geckolib3.core.controller.AnimationController;
	import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
	import software.bernie.geckolib3.core.manager.AnimationData;
	import software.bernie.geckolib3.core.manager.AnimationFactory;

				public class LapisRobberEntity extends Animal implements IAnimatable  {
					private AnimationFactory factory = new AnimationFactory(this);
					private static final EntityDataAccessor<java.util.Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(LapisRobberEntity.class, EntityDataSerializers.OPTIONAL_UUID);
					 private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(LapisRobberEntity.class, EntityDataSerializers.INT);
					private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {					 
							if (event.isMoving()) {
								event.getController().setAnimation(new AnimationBuilder().addAnimation("flying", true));
								return PlayState.CONTINUE;
							}
						event.getController().setAnimation(new AnimationBuilder().addAnimation("flying idle", true));
						return PlayState.CONTINUE;
					}
					
					public LapisRobberEntity(EntityType<? extends Animal> p_i48567_1_, Level p_i48567_2_) {
					      super(p_i48567_1_, p_i48567_2_);
					   }
					   public static AttributeSupplier.Builder createAttributes() {
						   return Mob.createMobAttributes()
						    .add(Attributes.FOLLOW_RANGE, 40.0D)
							.add(Attributes.MAX_HEALTH, 100.0D)
							.add(Attributes.MOVEMENT_SPEED, 0.5F)
							.add(Attributes.ATTACK_DAMAGE, 10.0D);
				}
					@Override
					public void registerControllers(AnimationData data) 
					{
						data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
					}

					@Override
					public AnimationFactory getFactory() 
					{
						return this.factory;
					}

					@Override
					protected void registerGoals() {
						  this.goalSelector.addGoal(1, new PickUpAttackGoal(this));
					      this.goalSelector.addGoal(2, new FlyAroundGoal(this, 15, 7, 30, 1.0F));
						  this.goalSelector.addGoal(5, new TemptGoal(this, 1.25D, Ingredient.of(Items.ROTTEN_FLESH), false));
					      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
					      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Player.class, true));
					      this.targetSelector.addGoal(6, new HurtByTargetGoal(this));
						  super.registerGoals();
					}
				    public void addAdditionalSaveData(CompoundTag compound) {
				        super.addAdditionalSaveData(compound);
				        if (this.getChildId() != null) {
				            compound.putUUID("ChildUUID", this.getChildId());
				        }
				        
				    }
				   public Map<String, Vector3f> getModelRotationValues() {
					   return this.getModelRotationValues();
				   }
				   public boolean canBreatheUnderwater() {
					      return false;
				   }
				   public int getCommand() {
				        return this.entityData.get(COMMAND);
				    }

				    public void setCommand(int i) {
				        this.entityData.set(COMMAND, i);
				    }
				   public boolean isInSittingPose() {
				        return this.getCommand() == 1;
				    }

				    public void setInSittingPose(boolean sit) {
				        if (sit) {
				            this.setCommand(1);
				        }
				    }

				    public boolean stopFlying() {
				        return this.isInSittingPose();
				    }
				    protected PathNavigation createNavigation(Level worldIn) {
				        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
				            public boolean isStableDestination(BlockPos pos) {
				                return this.level.getBlockState(pos).isAir();
				            }
				        };
				        flyingpathnavigator.setCanOpenDoors(false);
				        flyingpathnavigator.setCanFloat(true);
				        flyingpathnavigator.setCanPassDoors(true);
				        return flyingpathnavigator;
				    }
			       public float getWalkTargetValue(BlockPos p_149140_, LevelReader p_149141_) {
					   return 0.0F;
				   }
				   public boolean isPushedByFluid() {
				      return false;
				   }
				   public Entity getChild() {
				        UUID id = getChildId();
				        if (id != null && !level.isClientSide) {
				            return ((ServerLevel) level).getEntity(id);
				        }
				        return null;
				    }

				    @Nullable
				    public UUID getChildId() {
				        return this.entityData.get(CHILD_UUID).orElse(null);
				    }
				   
				@Override
				public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
					return null;
				
					   }
					}
				