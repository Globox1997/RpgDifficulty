package net.rpgdifficulty.zone;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class DifficultyZone {

    public enum Shape {
        BOX,
        SPHERE
    }

    private final UUID id;
    private final String dimension;
    private final Shape shape;
    private final double factor;

    // Box
    private final BlockPos boxMin;
    private final BlockPos boxMax;

    // Sphere
    private final BlockPos center;
    private final double radius;

    private DifficultyZone(UUID id, String dimension, Shape shape, double factor, BlockPos boxMin, BlockPos boxMax, BlockPos center, double radius) {
        this.id = id;
        this.dimension = dimension;
        this.shape = shape;
        this.factor = factor;
        this.boxMin = boxMin;
        this.boxMax = boxMax;
        this.center = center;
        this.radius = radius;
    }

    public static DifficultyZone createBox(String dimension, BlockPos pos1, BlockPos pos2, double factor) {
        BlockPos min = new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ()));
        BlockPos max = new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ()));
        return new DifficultyZone(UUID.randomUUID(), dimension, Shape.BOX, factor, min, max, null, 0);
    }

    public static DifficultyZone createSphere(String dimension, BlockPos center, double radius, double factor) {
        return new DifficultyZone(UUID.randomUUID(), dimension, Shape.SPHERE, factor, null, null, center, radius);
    }

    public boolean contains(String dimensionKey, double x, double y, double z) {
        if (!dimension.equals(dimensionKey)) {
            return false;
        }

        if (shape == Shape.BOX) {
            return x >= boxMin.getX() && x <= boxMax.getX() + 1 && y >= boxMin.getY() && y <= boxMax.getY() + 1 && z >= boxMin.getZ() && z <= boxMax.getZ() + 1;
        } else {
            double dx = x - (center.getX() + 0.5);
            double dy = y - (center.getY() + 0.5);
            double dz = z - (center.getZ() + 0.5);
            return dx * dx + dy * dy + dz * dz <= radius * radius;
        }
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("Id", id);
        nbt.putString("Dimension", dimension);
        nbt.putString("Shape", shape.name());
        nbt.putDouble("Factor", factor);
        if (shape == Shape.BOX) {
            nbt.putIntArray("BoxMin", new int[]{boxMin.getX(), boxMin.getY(), boxMin.getZ()});
            nbt.putIntArray("BoxMax", new int[]{boxMax.getX(), boxMax.getY(), boxMax.getZ()});
        } else {
            nbt.putIntArray("Center", new int[]{center.getX(), center.getY(), center.getZ()});
            nbt.putDouble("Radius", radius);
        }
        return nbt;
    }

    public static DifficultyZone fromNbt(NbtCompound nbt) {
        UUID id = nbt.getUuid("Id");
        String dimension = nbt.getString("Dimension");
        Shape shape = Shape.valueOf(nbt.getString("Shape"));
        double factor = nbt.getDouble("Factor");

        if (shape == Shape.BOX) {
            int[] min = nbt.getIntArray("BoxMin");
            int[] max = nbt.getIntArray("BoxMax");
            return new DifficultyZone(id, dimension, shape, factor, new BlockPos(min[0], min[1], min[2]), new BlockPos(max[0], max[1], max[2]), null, 0);
        } else {
            int[] c = nbt.getIntArray("Center");
            double radius = nbt.getDouble("Radius");
            return new DifficultyZone(id, dimension, shape, factor, null, null, new BlockPos(c[0], c[1], c[2]), radius);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getDimension() {
        return dimension;
    }

    public Shape getShape() {
        return shape;
    }

    public double getFactor() {
        return factor;
    }

    public BlockPos getBoxMin() {
        return boxMin;
    }

    public BlockPos getBoxMax() {
        return boxMax;
    }

    public BlockPos getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public String describe() {
        if (shape == Shape.BOX) {
            return String.format("Box [%d, %d, %d] -> [%d, %d, %d] in %s, Faktor %.2f", boxMin.getX(), boxMin.getY(), boxMin.getZ(), boxMax.getX(), boxMax.getY(), boxMax.getZ(), dimension, factor);
        } else {
            return String.format("Sphere um [%d, %d, %d] r=%.1f in %s, Faktor %.2f", center.getX(), center.getY(), center.getZ(), radius, dimension, factor);
        }
    }
}