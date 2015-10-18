package romelo333.notenoughwands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import romelo333.notenoughwands.varia.Coordinate;

import java.util.HashSet;
import java.util.Set;

public class PacketReturnProtectedBlocks implements IMessage {
    private Set<Coordinate> blocks;

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        blocks = new HashSet<Coordinate>(size);
        for (int i = 0 ; i < size ; i++) {
            blocks.add(new Coordinate(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blocks.size());
        for (Coordinate block : blocks) {
            buf.writeInt(block.getX());
            buf.writeInt(block.getY());
            buf.writeInt(block.getZ());
        }
    }


    public Set<Coordinate> getBlocks() {
        return blocks;
    }

    public PacketReturnProtectedBlocks() {
    }

    public PacketReturnProtectedBlocks(Set<Coordinate> blocks) {
        this.blocks = blocks;
    }
}