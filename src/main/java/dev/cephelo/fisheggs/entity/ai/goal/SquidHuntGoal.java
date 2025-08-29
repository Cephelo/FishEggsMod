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
import net.minecraft.world.entity.Mob;
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

    public SquidHuntGoal(Squid animal) {
        this(animal, PathfinderMob.class);
    }

    public SquidHuntGoal(Squid animal, Class<? extends PathfinderMob> preyClass) {
        this.animal = animal;
        this.level = animal.level();
        this.preyClass = preyClass;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean tooManySquids() {
        return this.level.getNearbyEntities(Squid.class, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(Config.SQUID_PREVENT_LOVE_RADIUS.get())).size()
                > Config.SQUID_PREVENT_LOVE_AMOUNT.get();
    }

    public boolean canUse() {
        if (this.animal.getData(FishDataAttachments.HUNT_COOLDOWN) > 0) {
            return false;
        } else if (this.animal.getData(FishDataAttachments.FISHINLOVE) > 0
                || this.animal.getData(FishDataAttachments.BREED_COOLDOWN) > 0) {
            return false;
        } else if (Config.SQUID_PREVENT_LOVE_HUNT.get() && tooManySquids()) {
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
        //this.animal.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10000));
        if (cannotHunt()) stop();
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), true);
    }

    public void stop() {
        //this.animal.removeEffect(MobEffects.GLOWING);
        this.prey = null;
        // if did not successfully eat
        if (this.animal.getData(FishDataAttachments.HUNT_COOLDOWN) <= 0) {
            this.animal.setData(FishDataAttachments.HUNT_COOLDOWN, Config.SQUID_CALM_DOWN_TIME_FAIL.get());
        }
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), false);
    }

    private boolean cannotHunt() {
        if (this.animal.getData(FishDataAttachments.HUNT_COOLDOWN) > 0) {
            return true;
        } else if (this.animal.getData(FishDataAttachments.FISHINLOVE) > 0
                || this.animal.getData(FishDataAttachments.BREED_COOLDOWN) > 0) {
            return true;
        } else if (Config.SQUID_PREVENT_LOVE_HUNT.get() && tooManySquids()) {
            return true;
        }
        return blacklistContainsSquid(this.animal.getType());
    }

    private static boolean blacklistContainsSquid(EntityType type) {
        return !(Config.CANHUNT_BLACKLIST.get().contains(EntityType.getKey(type).toString())
                == Config.CANHUNT_BLACKLIST_IS_WHITELIST.get());
    }

    private static boolean blacklistContainsPrey(EntityType type) {
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

        level.playSound(null, this.animal.getOnPos(), ModSounds.SQUID_EATS.get(), SoundSource.NEUTRAL, 1.0f, 0.5f);
        if (this.animal.isBaby()) return;

        if (this.prey.getHealth() <= Config.CONSUME_PREY.get()) {
            if (Config.CONSUME_PREY.get() > 0) this.prey.discard();

            if (level.getRandom().nextInt(100) <= Config.SQUID_HUNT_BREED_CHANCE.get() * 100) {
                if (tooManySquids()) {
                    animal.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 400));
                } else setLoveState(this.animal);
                stop();
            }
        }

    }

    public static void setLoveState(Mob mob) {
        mob.setData(FishDataAttachments.FISHINLOVE, Config.SQUID_LOVE_TIME.get());
        mob.setData(FishDataAttachments.HUNT_COOLDOWN, Config.SQUID_CALM_DOWN_TIME.get());

        // particle indicator
        mob.addEffect(new MobEffectInstance(MobEffects.LUCK, Config.SQUID_LOVE_TIME.get()));
        mob.removeEffect(MobEffects.UNLUCK);
    }
}
