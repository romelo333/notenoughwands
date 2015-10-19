package romelo333.notenoughwands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnProtectedBlockCountHandler implements IMessageHandler<PacketReturnProtectedBlockCount, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnProtectedBlockCount message, MessageContext ctx) {
        ReturnProtectedBlockCountHelper.setProtectedBlocks(message);
        return null;
    }

}