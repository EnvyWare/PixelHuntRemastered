package com.envyful.pixel.hunt.remastered.forge.config.typeadapter;

import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.registry.Registry;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ParticleDataTypeAdapter extends ScalarSerializer<IParticleData> {
    public ParticleDataTypeAdapter() {
        super(IParticleData.class);
    }

    @Override
    public IParticleData deserialize(Type type, Object obj) throws SerializationException {
        return (IParticleData) Registry.PARTICLE_TYPE.get(ResourceLocationHelper.of(obj.toString()));
    }

    @Override
    protected Object serialize(IParticleData item, Predicate<Class<?>> typeSupported) {
        return item.getType().getRegistryName().toString();
    }
}
