package romelo333.notenoughwands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PacketReturnProtectedBlockCount implements IMessage {
    private int count;

    @Override
    public void fromBytes(ByteBuf buf) {
        count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(count);
    }

    public int getCount() {
        return count;
    }

    public PacketReturnProtectedBlockCount() {
    }

    public PacketReturnProtectedBlockCount(int count) {
        this.count = count;
    }
}