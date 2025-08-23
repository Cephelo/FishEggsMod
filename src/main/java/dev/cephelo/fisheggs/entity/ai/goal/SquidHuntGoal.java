package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SquidHuntGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(Config.HUNT_SEARCH_RANGE.get()).ignoreLineOfSight();
    protected final Squid animal;
    private final Class<? extends PathfinderMob> preyClass;
    protected final Level level;
    @Nullable
    protected PathfinderMob prey;
    private int calmDown;

    public SquidHuntGoal(Squid animal) {
        this(animal, PathfinderMob.class);
    }

    public SquidHuntGoal(Squid animal, Class<? extends PathfinderMob> preyClass) {
        this.animal = animal;
        this.level = animal.level();
        this.preyClass = preyClass;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else if (this.animal.getData(FishDataAttachments.FISHINLOVE) > 0
                || this.animal.getData(FishDataAttachments.BREED_COOLDOWN) > 0) {
            return false;
        } else {
            this.prey = this.getFreePartner();
            return this.prey != null;
        }
    }

    public boolean canContinueToUse() {
        if (this.prey == null) return false;
        return this.prey.isAlive();
    }

    public void start() {
        if (!canHunt(this.animal.getType()) || !canUse()) stop();
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), true);
    }

    public void stop() {
        this.prey = null;
        this.calmDown = reducedTickDelay(Config.SQUID_CALM_DOWN_TIME.get());
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), false);
    }

    public static boolean canHunt(EntityType type) {
        return Config.CANHUNT_BLACKLIST.get().contains(EntityType.getKey(type).toString())
                == !Config.CANHUNT_BLACKLIST_IS_WHITELIST.get();
    }

    public static boolean blacklistContainsPrey(EntityType type) {
        return Config.HUNT_BLACKLIST.get().contains(EntityType.getKey(type).toString())
                == !Config.HUNT_BLACKLIST_IS_WHITELIST.get();
    }

    public void tick() {
        if (this.prey == null || blacklistContainsPrey(this.prey.getType())) {
            stop();
            return;
        }

        int i = this.animal.getNoActionTime();
        if (i > 100) {
            this.animal.setMovementVector(0.0F, 0.0F, 0.0F);
        } else if (this.animal.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.animal.isInWater() || !this.animal.hasMovementVector()) {
            Vector3f lookVec = this.animal.getPosition(0).vectorTo(this.prey.getPosition(0)).toVector3f();
            lookVec = lookVec.div((float) Math.sqrt((lookVec.x * lookVec.x) + (lookVec.y * lookVec.y) + (lookVec.z * lookVec.z)));
            this.animal.setMovementVector(lookVec.x * 0.2f, lookVec.y * 0.2f, lookVec.z * 0.2f);
            this.animal.getLookControl().setLookAt(this.prey, 10.0F, (float)this.animal.getMaxHeadXRot());
        }

        //FishEggsMod.LOGGER.info("distsqrH {}", this.animal.distanceToSqr(this.prey));
        if (this.animal.distanceToSqr(this.prey) < Config.SQUID_DIST_HUNT.get()) {
            if (canUse()) this.feed();
        }
    }

    @Nullable
    private PathfinderMob getFreePartner() {
        List<? extends PathfinderMob> list = this.level.getNearbyEntities(this.preyClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(Config.HUNT_SEARCH_RANGE.get()));
        double d0 = Double.MAX_VALUE;
        PathfinderMob animal = null;

        for(PathfinderMob animal1 : list) {
            if (this.animal != animal1 && this.animal.distanceToSqr(animal1) < d0) {
                animal = animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }

        return animal;
    }

    protected void feed() {
        if (this.prey == null) {
            stop();
            return;
        }

        this.prey.hurt(new DamageSource(level.registryAccess().holderOrThrow(DamageTypes.MOB_ATTACK), this.animal, this.animal), Config.SQUID_HUNT_DAMAGE.get());
        if (Config.CONSUME_PREY.get() && this.prey.getHealth() <= 1) {
            this.prey.discard();
            if (!this.animal.isBaby()) {
                this.animal.setData(FishDataAttachments.FISHINLOVE, Config.SQUID_LOVE_TIME.get());
                this.animal.addEffect(new MobEffectInstance(MobEffects.LUCK, Config.SQUID_LOVE_TIME.get()));
            }
        } else if (!Config.CONSUME_PREY.get() && this.prey.getHealth() <= 0) {
            if (!this.animal.isBaby()) {
                this.animal.setData(FishDataAttachments.FISHINLOVE, Config.SQUID_LOVE_TIME.get());
                this.animal.addEffect(new MobEffectInstance(MobEffects.LUCK, Config.SQUID_LOVE_TIME.get()));
            }
        }

        level.playSound(null, this.animal.getOnPos(), ModSounds.SQUID_EATS.get(), SoundSource.NEUTRAL, 1.0f, 0.5f);
    }
}
