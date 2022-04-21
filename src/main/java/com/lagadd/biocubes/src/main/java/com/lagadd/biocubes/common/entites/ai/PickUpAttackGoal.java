package com.lagadd.biocubes.common.entites.ai;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import com.lagadd.biocubes.common.entites.LapisRobberEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

public class PickUpAttackGoal extends Goal {

	    private LapisRobberEntity lapisrobber;
	    private Entity pickupMonster = null;
	    private int tryPickupCheckIn = 0;
	    private int punchCooldown = 0;
	    private int punchTicks = 0;
	    public PickUpAttackGoal(LapisRobberEntity badloon) {
	        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	        this.lapisrobber = badloon;
	    }


	    public void stop() {
	        this.pickupMonster = null;
	        this.punchTicks = 0;
	    }
	    public void tick(){
	    	lapisrobber.setNoActionTime(0);
	        boolean moveTowardsTarget = true;
	        float extraY = 1.3F;
	        Entity hand = this.lapisrobber.getChild();
	        if(hand == null){
	            return;
	        }
	        if(this.punchCooldown > 0){
	            this.punchCooldown--;
	        }
	        if(pickupMonster != null){
	            if(pickupMonster.isPassengerOfSameVehicle(hand)){
	                if(hand.getY() > lapisrobber.getTarget().getY() + pickupMonster.getBbHeight() && getXZDistanceTo(lapisrobber.getTarget().position()) < 0.5F){
	                    dropMob();
	                }
	            }else{
	                if(pickupMonster.isPassenger()){
	                    pickupMonster = null;
	                }else{
	                    this.lapisrobber.getMoveControl().setWantedPosition(pickupMonster.getX(), pickupMonster.getEyeY() + 1.2F, pickupMonster.getZ(), 1.0F);
	                    if(hand.distanceTo(pickupMonster) < 3){
	                        pickupMonster.startRiding(hand, true);
	                    }
	                    moveTowardsTarget = false;
	                }
	            }
	        } else{
	            if(tryPickupCheckIn > 0){
	                tryPickupCheckIn--;
	            }else{
	                findMobToPickup();
	                tryPickupCheckIn = 50;
	            }
	        }
	        if(lapisrobber.getTarget().fallDistance >= 3.0D){
	            if(this.pickupMonster != null && this.pickupMonster.isPassengerOfSameVehicle(hand)){
	                dropMob();
	            }
	        }else if(moveTowardsTarget){
	            double targetX = lapisrobber.getTarget().getX();
	            double targetZ = lapisrobber.getTarget().getZ();

	            if (lapisrobber.verticalCollision && !lapisrobber.isOnGround() && !lapisrobber.hasLineOfSight(lapisrobber.getTarget())) {
	                Vec3 lookRotated = new Vec3(0F, 0F, 2F).yRot(-lapisrobber.getYRot() * (float)(Math.PI / 180F));
	                targetX = lapisrobber.getX() + lookRotated.x;
	                targetZ = lapisrobber.getZ() + lookRotated.z;
	            }
	            this.lapisrobber.getMoveControl().setWantedPosition(targetX, lapisrobber.getTarget().getEyeY() + extraY, targetZ, 1.0F);
	        }
	        if(pickupMonster == null && this.punchCooldown == 0 && (hand.distanceTo(this.lapisrobber.getTarget()) < this.lapisrobber.getTarget().getBbWidth() + 0.5F || hand.getBoundingBox().intersects(this.lapisrobber.getTarget().getBoundingBox()))){
	            punchTicks++;
	            if(punchTicks > 3){
	                this.lapisrobber.getTarget().hurt(DamageSource.mobAttack(this.lapisrobber), 2);
	                punchTicks = 0;
	                this.punchCooldown = 5 + lapisrobber.getRandom().nextInt(10);
	            }

	        }
	    }


	    public void findMobToPickup(){
	        Predicate<Entity> monsterAway = (animal) -> animal instanceof Monster && !(animal instanceof LapisRobberEntity) && animal.distanceTo(lapisrobber.getTarget()) > 5 && !animal.isPassenger();
	        List<Mob> list = lapisrobber.level.getEntitiesOfClass(Mob.class, lapisrobber.getTarget().getBoundingBox().inflate(30, 12, 30), EntitySelector.NO_SPECTATORS.and(monsterAway));
	        list.sort(Comparator.comparingDouble(lapisrobber::distanceToSqr));
	        if (!list.isEmpty()) {
	            pickupMonster = list.get(0);
	        }
	    }
	    public void dropMob(){
	        pickupMonster.stopRiding();
	        if(pickupMonster instanceof Creeper){
	            ((Creeper)pickupMonster).ignite();
	        }
	        this.punchCooldown = 30;
	        pickupMonster = null;
	    }

	    public double getXZDistanceTo(Vec3 vec3){
	        return Mth.sqrt((float) lapisrobber.distanceToSqr(vec3.x, lapisrobber.getY(), vec3.z));
	    }


		@Override
		public boolean canUse() {
			// TODO Auto-generated method stub
			return false;
		}


	}
