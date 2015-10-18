package romelo333.notenoughwands;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import romelo333.notenoughwands.varia.GlobalCoordinate;

import java.util.HashSet;
import java.util.Set;

public class ProtectedBlocks extends WorldSavedData{
    public static final String NAME = "NEWProtectedBlocks";
    private static ProtectedBlocks instance;

    private Set<GlobalCoordinate> blocks = new HashSet<GlobalCoordinate>();

    public ProtectedBlocks(String name) {
        super(name);
    }

    public void save (World world){
        world.mapStorage.setData(NAME,this);
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

    public void protect (World world,int x,int y, int z){
        blocks.add(new GlobalCoordinate(x,y,z,world.provider.dimensionId));
        save(world);
    }

    public void unprotect (World world,int x,int y, int z){
        blocks.remove(new GlobalCoordinate(x, y, z, world.provider.dimensionId));
        save(world);
    }

    public boolean isProtected (World world,int x,int y, int z){
        return blocks.contains(new GlobalCoordinate(x,y,z,world.provider.dimensionId));
    }

    public boolean hasProtections() {
        return !blocks.isEmpty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        blocks.clear();
        NBTTagList list = tagCompound.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i<list.tagCount();i++){
            NBTTagCompound tc = list.getCompoundTagAt(i);
            GlobalCoordinate block = new GlobalCoordinate(tc.getInteger("x"),tc.getInteger("y"),tc.getInteger("z"),tc.getInteger("dim"));
            blocks.add(block);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList list = new NBTTagList();
        for (GlobalCoordinate block : blocks) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("x",block.getX());
            tc.setInteger("y",block.getY());
            tc.setInteger("z",block.getZ());
            tc.setInteger("dim",block.getDim());
            list.appendTag(tc);
        }
        tagCompound.setTag("blocks",list);
    }
}