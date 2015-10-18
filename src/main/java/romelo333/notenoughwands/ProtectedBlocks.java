package romelo333.notenoughwands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.GlobalCoordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProtectedBlocks extends WorldSavedData{
    public static final String NAME = "NEWProtectedBlocks";
    private static ProtectedBlocks instance;

    // Persisted data
    private Map<GlobalCoordinate, Integer> blocks = new HashMap<GlobalCoordinate, Integer>();
    private int lastId = 1;

    public ProtectedBlocks(String name) {
        super(name);
    }

    public void save (World world){
        world.mapStorage.setData(NAME, this);
        markDirty();
    }

    public static ProtectedBlocks getProtectedBlocks (World world){
        if (world.isRemote){
            return null;
        }
        if (instance != null){
            return instance;
        }
        instance = (ProtectedBlocks)world.mapStorage.loadData(ProtectedBlocks.class,NAME);
        if (instance == null){
            instance = new ProtectedBlocks(NAME);
        }
        return instance;
    }

    public int getNewId(World world) {
        lastId++;
        save(world);
        return lastId-1;
    }

    public boolean protect(EntityPlayer player, World world, int x, int y, int z, int id) {
        GlobalCoordinate key = new GlobalCoordinate(x, y, z, world.provider.dimensionId);
        if (id != -1 && blocks.containsKey(key)) {
            Tools.error(player, "This block is already protected!");
            return false;
        }
        blocks.put(key, id);
        save(world);
        return true;
    }

    public boolean unprotect(EntityPlayer player, World world, int x, int y, int z, int id) {
        GlobalCoordinate key = new GlobalCoordinate(x, y, z, world.provider.dimensionId);
        if (!blocks.containsKey(key)) {
            Tools.error(player, "This block is not prorected!");
            return false;
        }
        if (id != -1 && blocks.get(key) != id) {
            Tools.error(player, "You have no permission to unprotect this block!");
            return false;
        }
        blocks.remove(key);
        save(world);
        return true;
    }

    public boolean isProtected(World world, int x, int y, int z){
        return blocks.containsKey(new GlobalCoordinate(x, y, z, world.provider.dimensionId));
    }

    public boolean hasProtections() {
        return !blocks.isEmpty();
    }

    public void fetchProtectedBlocks(Set<Coordinate> coordinates, World world, int x, int y, int z, float radius, int id) {
        radius *= radius;
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id || (id == -2 && entry.getValue() != -1)) {
                GlobalCoordinate block = entry.getKey();
                if (block.getDim() == world.provider.dimensionId) {
                    float sqdist = (x - block.getX()) * (x - block.getX()) + (y - block.getY()) * (y - block.getY()) + (z - block.getZ()) * (z - block.getZ());
                    if (sqdist < radius) {
                        coordinates.add(block);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        lastId = tagCompound.getInteger("lastId");
        blocks.clear();
        NBTTagList list = tagCompound.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i<list.tagCount();i++){
            NBTTagCompound tc = list.getCompoundTagAt(i);
            GlobalCoordinate block = new GlobalCoordinate(tc.getInteger("x"),tc.getInteger("y"),tc.getInteger("z"),tc.getInteger("dim"));
            blocks.put(block, tc.getInteger("id"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("lastId", lastId);
        NBTTagList list = new NBTTagList();
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            GlobalCoordinate block = entry.getKey();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("x", block.getX());
            tc.setInteger("y", block.getY());
            tc.setInteger("z", block.getZ());
            tc.setInteger("dim", block.getDim());
            tc.setInteger("id", entry.getValue());
            list.appendTag(tc);
        }
        tagCompound.setTag("blocks",list);
    }
}