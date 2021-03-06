package be.isach.ultracosmetics.cosmetics.mounts.customentities.v1_9_R1;

import be.isach.ultracosmetics.cosmetics.morphs.customentities.v1_9_R1.CustomGuardian_1_9_R1;
import be.isach.ultracosmetics.cosmetics.pets.v1_9_R1.Pumpling1_9_R1;
import net.minecraft.server.v1_9_R1.BiomeBase;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityTypes;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum CustomEntities_1_9_R1 {

    FLYING_SQUID("FlyingSquid", EntityType.SQUID.getTypeId(), EntityType.SQUID, FlyingSquid_1_9_R1.class, FlyingSquid_1_9_R1.class),
    PUMPLING("Pumpling", EntityType.ZOMBIE.getTypeId(), EntityType.ZOMBIE, Pumpling1_9_R1.class, Pumpling1_9_R1.class),
    SLIME("CustomSlime", EntityType.SLIME.getTypeId(), EntityType.SLIME, CustomSlime_1_9_R1.class, CustomSlime_1_9_R1.class),
    RIDEABLE_SPIDER("RideableSpider", EntityType.SPIDER.getTypeId(), EntityType.SPIDER, RideableSpider_1_9_R1.class, RideableSpider_1_9_R1.class),
    CUSTOM_GUARDIAN("CustomGuardian", EntityType.GUARDIAN.getTypeId(), EntityType.GHAST, CustomGuardian_1_9_R1.class, CustomGuardian_1_9_R1.class);

    public static List<Entity> customEntities = new ArrayList();

    private String name;
    private int id;
    private EntityType entityType;
    private Class<? extends EntityInsentient> nmsClass;
    private Class<? extends EntityInsentient> customClass;

    CustomEntities_1_9_R1(String name, int id, EntityType entityType,
                   Class<? extends EntityInsentient> nmsClass,
                   Class<? extends EntityInsentient> customClass) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Class<? extends EntityInsentient> getNMSClass() {
        return nmsClass;
    }

    public Class<? extends EntityInsentient> getCustomClass() {
        return customClass;
    }

    public static void registerEntities() {
        for (CustomEntities_1_9_R1 entity : values())
            a(entity.getCustomClass(), entity.getName(), entity.getID());

        for (BiomeBase biomeBase : BiomeBase.i) {
            if (biomeBase == null)
                break;
            for (String field : new String[] { "u", "v", "w", "x" })
                try {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list.get(biomeBase);

                    for (BiomeBase.BiomeMeta meta : mobList)
                        for (CustomEntities_1_9_R1 entity : values())
                            if (entity.getNMSClass().equals(meta.b))
                                meta.b = entity.getCustomClass();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    @SuppressWarnings("rawtypes")
    public static void unregisterEntities() {
        for (CustomEntities_1_9_R1 entity : values()) {
            try {
                ((Map) getPrivateStatic(EntityTypes.class, "d")).remove(entity.getCustomClass());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((Map) getPrivateStatic(EntityTypes.class, "f")).remove(entity.getCustomClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (CustomEntities_1_9_R1 entity : values())
            try {
                a(entity.getNMSClass(), entity.getName(), entity.getID());
            } catch (Exception e) {
                e.printStackTrace();
            }

        for (BiomeBase biomeBase : BiomeBase.i) {
            if (biomeBase == null)
                break;

            for (String field : new String[] { "u", "v", "w", "x" })
                try {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<BiomeBase.BiomeMeta> mobList = (List<BiomeBase.BiomeMeta>) list.get(biomeBase);

                    for (BiomeBase.BiomeMeta meta : mobList)
                        for (CustomEntities_1_9_R1 entity : values())
                            if (entity.getCustomClass().equals(meta.b))
                                meta.b = entity.getNMSClass();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    @SuppressWarnings("rawtypes")
    private static Object getPrivateStatic(Class clazz, String f) throws Exception {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);
        return field.get(null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void a(Class paramClass, String paramString, int paramInt) {
        try {
            ((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
            ((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
            ((Map) getPrivateStatic(EntityTypes.class, "e")).put(paramInt,
                    paramClass);
            ((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass,
                    paramInt);
            ((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString,
                    paramInt);
        } catch (Exception ignored) {}
    }
}
