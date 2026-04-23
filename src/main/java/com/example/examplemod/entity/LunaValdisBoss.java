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
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LunaValdisBoss extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.examplemod.luna_valdis"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private enum AttackType {
        NONE,
        RUNE_SLASH,
        SHADOW_THRUST,
        MOON_BARRAGE,
        MOON_BLADE,
        MOONFALL,
        CROSS,
        RESONANCE,
        OATH
    }

    private AttackType telegraphAttack = AttackType.NONE;
    private int telegraphTicks = 0;
    private int attackCooldown = 30;
    private boolean phaseTwo = false;
    private int phaseShiftLockTicks = 0;
    private int oathBuffTicks = 0;

    public LunaValdisBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 220;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 24.0F));
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
            tickOathBuff();
            tickTelegraphAttack();
        }
    }

    private void updateBossBar() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    private void tickOathBuff() {
        if (oathBuffTicks > 0) {
            oathBuffTicks--;
            if (oathBuffTicks == 0 && this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(phaseTwo ? 12.5D : 10.0D);
            }
        }
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
        if (!(target instanceof Player player) || !target.isAlive()) {
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
                executeTelegraphAttack(player);
                telegraphAttack = AttackType.NONE;
                attackCooldown = phaseTwo ? 18 : 28;
            }
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        this.getNavigation().stop();
        chooseNextAttack(player);
    }

    private void chooseNextAttack(Player player) {
        RandomSource random = this.getRandom();
        double distance = this.distanceTo(player);
        float roll = random.nextFloat();

        if (phaseTwo && roll < 0.12f) {
            telegraphAttack = AttackType.OATH;
            telegraphTicks = 36;
            return;
        }
        if (phaseTwo && roll < 0.26f) {
            telegraphAttack = AttackType.RESONANCE;
            telegraphTicks = 40;
            return;
        }
        if (phaseTwo && roll < 0.42f) {
            telegraphAttack = AttackType.CROSS;
            telegraphTicks = 12;
            return;
        }
        if (phaseTwo && roll < 0.56f) {
            telegraphAttack = AttackType.MOON_BLADE;
            telegraphTicks = 16;
            return;
        }
        if (distance > 8.0D && roll < 0.72f) {
            telegraphAttack = AttackType.MOON_BARRAGE;
            telegraphTicks = 24;
            return;
        }
        if (distance < 5.0D && roll < 0.86f) {
            telegraphAttack = AttackType.SHADOW_THRUST;
            telegraphTicks = 18;
            return;
        }
        if (roll < 0.93f) {
            telegraphAttack = AttackType.RUNE_SLASH;
            telegraphTicks = 14;
            return;
        }
        telegraphAttack = AttackType.MOONFALL;
        telegraphTicks = 30;
    }

    private void executeTelegraphAttack(Player target) {
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        switch (telegraphAttack) {
            case RUNE_SLASH -> doRuneSlash(serverLevel);
            case SHADOW_THRUST -> doShadowThrust(serverLevel, target);
            case MOON_BARRAGE -> doMoonBarrage(serverLevel, target);
            case MOON_BLADE -> doMoonBlade(serverLevel);
            case MOONFALL -> doMoonfall(serverLevel);
            case CROSS -> doCrossSlash(serverLevel);
            case RESONANCE -> doResonance(serverLevel);
            case OATH -> doOath();
            default -> {
            }
        }
    }

    private void doRuneSlash(ServerLevel level) {
        AABB area = this.getBoundingBox().inflate(3.8D, 1.5D, 3.8D);
        for (Player p : level.getEntitiesOfClass(Player.class, area)) {
            p.hurt(this.damageSources().mobAttack(this), phaseTwo ? 9.5F : 8.0F);
        }
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.85F);
    }

    private void doShadowThrust(ServerLevel level, Player target) {
        Vec3 dir = new Vec3(target.getX() - this.getX(), 0, target.getZ() - this.getZ()).normalize();
        this.setDeltaMovement(dir.scale(1.4D));
        this.hurtMarked = true;

        AABB area = this.getBoundingBox().inflate(1.8D, 1.0D, 1.8D);
        for (Player p : level.getEntitiesOfClass(Player.class, area)) {
            p.hurt(this.damageSources().mobAttack(this), phaseTwo ? 11.0F : 9.0F);
        }
        this.playSound(SoundEvents.ENDERMAN_SCREAM, 0.6F, 1.25F);
    }

    private void doMoonBarrage(ServerLevel level, Player target) {
        for (int i = 0; i < (phaseTwo ? 5 : 3); i++) {
            Vec3 toTarget = new Vec3(target.getX() - this.getX(), (target.getY() + 1.0D) - (this.getY() + 1.0D), target.getZ() - this.getZ()).normalize();
            double spreadX = (this.random.nextDouble() - 0.5D) * 0.4D;
            double spreadZ = (this.random.nextDouble() - 0.5D) * 0.4D;
            level.sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 1.4D, this.getZ(), 10, 0.1D, 0.1D, 0.1D, 0.02D);

            AABB hitBox = target.getBoundingBox().inflate(0.3D + (i * 0.05D));
            Vec3 probePos = this.position().add(toTarget.scale(4.0D + i).add(spreadX, 0, spreadZ));
            if (hitBox.contains(probePos)) {
                target.hurt(this.damageSources().mobAttack(this), phaseTwo ? 7.0F : 5.5F);
            }
        }
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.9F);
    }

    private void doMoonBlade(ServerLevel level) {
        for (Player p : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(9.0D))) {
            double d = p.distanceTo(this);
            if (d > 3.0D && d < 9.0D) {
                p.hurt(this.damageSources().mobAttack(this), 8.5F);
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, p.getX(), p.getY() + 1.0D, p.getZ(), 6, 0.15D, 0.2D, 0.15D, 0.0D);
            }
        }
        this.playSound(SoundEvents.TRIDENT_RETURN, 1.0F, 0.75F);
    }

    private void doMoonfall(ServerLevel level) {
        List<Player> players = level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D));
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

    private void doCrossSlash(ServerLevel level) {
        AABB area = this.getBoundingBox().inflate(7.0D, 2.0D, 7.0D);
        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            double dx = Math.abs(player.getX() - this.getX());
            double dz = Math.abs(player.getZ() - this.getZ());
            if ((dx <= 1.2D || dz <= 1.2D) && player.distanceTo(this) <= 7.0D) {
                player.hurt(this.damageSources().mobAttack(this), 11.0F);
            }
        }
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.7F);
    }

    private void doResonance(ServerLevel level) {
        for (Player p : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0D))) {
            double dx = p.getX() - this.getX();
            double dz = p.getZ() - this.getZ();
            boolean isUnsafe = (dx > 0 && dz > 0) || (dx < 0 && dz < 0);
            if (isUnsafe && p.distanceTo(this) <= 11.0D) {
                p.hurt(this.damageSources().mobAttack(this), 12.0F);
            }
        }
        this.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 0.7F);
    }

    private void doOath() {
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(phaseTwo ? 14.5D : 11.5D);
        }
        oathBuffTicks = 200;
        this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 0.8F);
    }

    private void spawnTelegraphParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        switch (telegraphAttack) {
            case MOONFALL -> {
                int points = 24;
                double radius = Mth.lerp((30 - telegraphTicks) / 30.0D, 1.6D, 7.0D);
                for (int i = 0; i < points; i++) {
                    double angle = i * (Math.PI * 2D / points);
                    double x = this.getX() + Math.cos(angle) * radius;
                    double z = this.getZ() + Math.sin(angle) * radius;
                    serverLevel.sendParticles(ParticleTypes.END_ROD, x, this.getY() + 0.1D, z, 1, 0, 0.02D, 0, 0.0D);
                }
            }
            case CROSS -> {
                for (int i = -6; i <= 6; i++) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX() + i, this.getY() + 0.1D, this.getZ(), 1, 0, 0, 0, 0);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY() + 0.1D, this.getZ() + i, 1, 0, 0, 0, 0);
                }
            }
            case RESONANCE -> {
                for (int i = 0; i < 4; i++) {
                    double x = this.getX() + (i < 2 ? 6 : -6);
                    double z = this.getZ() + ((i % 2 == 0) ? 6 : -6);
                    serverLevel.sendParticles(ParticleTypes.SOUL, x, this.getY() + 0.2D, z, 12, 0.4D, 0.3D, 0.4D, 0.01D);
                }
            }
            case OATH -> serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.1D, this.getZ(), 22, 0.8D, 0.9D, 0.8D, 0.01D);
            case RUNE_SLASH, SHADOW_THRUST, MOON_BARRAGE, MOON_BLADE ->
                    serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.0D, this.getZ(), 8, 0.4D, 0.4D, 0.4D, 0.0D);
            default -> {
            }
        }
    }

    private void spawnPhaseShiftParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2D / 12D) * i + (this.tickCount * 0.15D);
            double x = this.getX() + Math.cos(angle) * 1.4D;
            double z = this.getZ() + Math.sin(angle) * 1.4D;
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
