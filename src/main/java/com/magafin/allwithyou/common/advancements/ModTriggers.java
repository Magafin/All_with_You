package com.magafin.allwithyou.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

// Добавляем автоматическую подписку на шину мода
@EventBusSubscriber(modid = "all_with_you", bus = EventBusSubscriber.Bus.MOD)
public class ModTriggers {

    // Объект нашего триггера
    public static final FullBackpackTrigger FULL_BACKPACK = new FullBackpackTrigger();

    // Метод регистрации, который вызывается NeoForge в нужный момент
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.TRIGGER_TYPE, helper -> {
            // "all_with_you:full_backpack" — этот ID должен совпадать с тем, что ты написал в JSON-файле достижения
            helper.register(ResourceLocation.fromNamespaceAndPath("all_with_you", "full_backpack"), FULL_BACKPACK);
        });
    }

    public static class FullBackpackTrigger extends SimpleCriterionTrigger<FullBackpackTrigger.Instance> {
        @Override
        public Codec<Instance> codec() {
            return Instance.CODEC;
        }

        public void trigger(ServerPlayer player) {
            this.trigger(player, instance -> true);
        }

        public record Instance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
            public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player)
            ).apply(inst, Instance::new));
        }
    }
}