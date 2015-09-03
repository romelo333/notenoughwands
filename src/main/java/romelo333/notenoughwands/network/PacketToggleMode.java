package romelo333.notenoughwands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import romelo333.notenoughwands.Items.GenericWand;

public class PacketToggleMode implements IMessage, IMessageHandler<PacketToggleMode, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    @Override
    public IMessage onMessage(PacketToggleMode message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
        ItemStack heldItem = playerEntity.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof GenericWand) {
            GenericWand genericWand = (GenericWand) (heldItem.getItem());
            genericWand.toggleMode(playerEntity, heldItem);
        }
        return null;
    }

}
