package romelo333.notenoughwands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import romelo333.notenoughwands.Items.ProtectionWand;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.varia.Coordinate;

import java.util.HashSet;
import java.util.Set;

public class PacketGetProtectedBlocks implements IMessage,IMessageHandler<PacketGetProtectedBlocks, PacketReturnProtectedBlocks> {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    @Override
    public PacketReturnProtectedBlocks onMessage(PacketGetProtectedBlocks message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        World world = player.worldObj;
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        Set<Coordinate> blocks = new HashSet<Coordinate>();
        protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, ProtectionWand.blockShowRadius);
        return new PacketReturnProtectedBlocks(blocks);
    }

}