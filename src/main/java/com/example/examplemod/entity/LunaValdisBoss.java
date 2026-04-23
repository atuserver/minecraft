package com.example.examplemod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LunaValdisBoss extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.examplemod.luna_valdis"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private enum AttackType {
        NONE,
        MOONFALL,
        CROSS
    }

    private AttackType telegraphAttack = AttackType.NONE;
    private int telegraphTicks = 0;
    private int attackCooldown = 40;
    private boolean phaseTwo = false;
    private int phaseShiftLockTicks = 0;

    public LunaValdisBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 80;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.ARMOR, 8.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            updateBossBar();
            tickPhaseTransition();
            tickTelegraphAttack();
        }
    }

    private void updateBossBar() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    private void tickPhaseTransition() {
        if (!phaseTwo && this.getHealth() <= this.getMaxHealth() * 0.5f) {
            phaseTwo = true;
            phaseShiftLockTicks = 16;
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12.5D);
            }
        }

        if (phaseShiftLockTicks > 0) {
            phaseShiftLockTicks--;
        }
    }

    private void tickTelegraphAttack() {
        LivingEntity target = this.getTarget();
        if (!(target instanceof Player) || !target.isAlive()) {
            return;
        }

        if (phaseShiftLockTicks > 0) {
            this.getNavigation().stop();
            spawnPhaseShiftParticles();
            return;
        }

        if (telegraphTicks > 0) {
            telegraphTicks--;
            spawnTelegraphParticles();
            if (telegraphTicks == 0) {
                executeTelegraphAttack();
                telegraphAttack = AttackType.NONE;
                attackCooldown = phaseTwo ? 24 : 34;
            }
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        this.getNavigation().stop();
        RandomSource random = this.getRandom();
        if (phaseTwo && random.nextFloat() < 0.55f) {
            telegraphAttack = AttackType.CROSS;
            telegraphTicks = 12;
        } else {
            telegraphAttack = AttackType.MOONFALL;
            telegraphTicks = 30;
        }
    }

    private void executeTelegraphAttack() {
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (telegraphAttack == AttackType.MOONFALL) {
            doMoonfall(serverLevel);
        } else if (telegraphAttack == AttackType.CROSS) {
            doCrossSlash(serverLevel);
        }
    }

    private void doMoonfall(ServerLevel serverLevel) {
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D));
        for (Player player : players) {
            double distance = player.distanceTo(this);
            boolean inOuter = distance <= 7.0D && distance > 2.8D;
            boolean inCenter = distance <= 1.4D;
            if (inOuter || inCenter) {
                player.hurt(this.damageSources().mobAttack(this), phaseTwo ? 13.0F : 10.0F);
            }
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.1F, 0.9F);
    }

    private void doCrossSlash(ServerLevel serverLevel) {
        AABB area = this.getBoundingBox().inflate(7.0D, 2.0D, 7.0D);
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area)) {
            double dx = Math.abs(player.getX() - this.getX());
            double dz = Math.abs(player.getZ() - this.getZ());
            if ((dx <= 1.2D || dz <= 1.2D) && player.distanceTo(this) <= 7.0D) {
                player.hurt(this.damageSources().mobAttack(this), 11.0F);
            }
        }
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.7F);
    }

    private void spawnTelegraphParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (telegraphAttack == AttackType.MOONFALL) {
            int points = 24;
            double radius = Mth.lerp((30 - telegraphTicks) / 30.0D, 1.6D, 7.0D);
            for (int i = 0; i < points; i++) {
                double angle = i * (Math.PI * 2D / points);
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, this.getY() + 0.1D, z, 1, 0, 0.02D, 0, 0.0D);
            }
        } else if (telegraphAttack == AttackType.CROSS) {
            for (int i = -6; i <= 6; i++) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX() + i, this.getY() + 0.1D, this.getZ(), 1, 0, 0, 0, 0);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 0.1D, this.getZ() + i, 1, 0, 0, 0, 0);
            }
        }
    }

    private void spawnPhaseShiftParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            double angle = (Math.PI * 2D / 10D) * i + (this.tickCount * 0.15D);
            double x = this.getX() + Math.cos(angle) * 1.2D;
            double z = this.getZ() + Math.sin(angle) * 1.2D;
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, this.getY() + 1.4D, z, 1, 0, 0.01D, 0, 0.0D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (phaseShiftLockTicks > 0) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.playSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1.0F, 0.7F);
    }
}
