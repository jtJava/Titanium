package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CreativeCheckRunner extends BaseCheck {
    /*
    This class is for running and handling all creative checks
     */
    private final List<CreativeCheck> checks;

    private final int maxRecursions = TitaniumConfig.getInstance().getCreativeConfig().getMaxRecursions();
    private final int maxItems = TitaniumConfig.getInstance().getCreativeConfig().getMaxItems();

    public CreativeCheckRunner(Collection<CreativeCheck> checks) {
        this.checks = new ArrayList<>(checks);
    }

    //TODO: Maybe only trigger checks on certain items to save performance
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            Player player = this.getPlayer(event);
            if (player != null && player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
                return;
            }

            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
            //No need to call creative checks if the item is null
            if (wrapper.getItemStack() == null) {
                return;
            }
            //No creative checks needed for items without NBT data
            if (wrapper.getItemStack().getNBT() == null) {
                return;
            }
            NBTCompound compound = wrapper.getItemStack().getNBT();
            //when the compound has block entity tag, do recursion to find nested/hidden items
            if (compound.getTags().containsKey("BlockEntityTag")) {
                NBTCompound blockEntityTag = compound.getCompoundTagOrNull("BlockEntityTag");
                //reset recursion count to prevent false kicks
                playerData.resetRecursion();
                recursion(event, playerData, wrapper.getItemStack(), blockEntityTag);
            } else {
                //if this gets called, it's not a container, so we don't need to do recursion
                for (CreativeCheck check : checks) {
                    //Maybe add a check result class, so that we can have more detailed verbose output...
                    if (check.handleCheck(wrapper.getItemStack(), compound)) {
                        flagPacket(event, "Item: " + wrapper.getItemStack().getType().getName());
                    }
                }
            }
        }
    }

    private void recursion(PacketReceiveEvent event, PlayerData data, ItemStack clickedItem, NBTCompound blockEntityTag) {
        //prevent recursion abuse with deeply nested items
        if (data.incrementRecursionCount() > maxRecursions) {
            flagPacket(event, "Too many recursions: " + data.getRecursionCount());
            return;
        }
        if (blockEntityTag.getTags().containsKey("Items")) {
            NBTList<NBTCompound> items = blockEntityTag.getCompoundListTagOrNull("Items");
            //This is super weird, when control + middle-clicking a chest this becomes null suddenly
            //Is this intentional behaviour? I have no idea how to fix this
            if (items == null) {
                return;
            }
            //it might be possible to send an item container via creative packets with a large amount of items in nbt
            //however I haven't actually found an exploit doing this
            if (items.size() > maxItems) {
                flagPacket(event, "Too many items: " + items.size());
                return;
            }
            //Loop through all items
            for (int i = 0; i < items.size(); i++) {
                NBTCompound item = items.getTag(i);

                //Check if the item has the tag "tag" meaning it got extra nbt (besides the default item data of damage, count, id etc.)
                if (item.getTags().containsKey("tag")) {
                    NBTCompound tag = item.getCompoundTagOrNull("tag");
                    //call creative checks to check for illegal tags
                    for (CreativeCheck check : checks) {
                        if (check.handleCheck(clickedItem, tag)) {
                            flagPacket(event, "Recursions: " + data.getRecursionCount() + " Item: " + clickedItem.getType().getName());
                            return;
                        }
                    }
                    //if that item has block entity tag do recursion to find potential nested/"hidden" items
                    if (tag.getTags().containsKey("BlockEntityTag")) {
                        NBTCompound recursionBlockEntityTag = tag.getCompoundTagOrNull("BlockEntityTag");
                        recursion(event, data, clickedItem, recursionBlockEntityTag);
                    }
                } else {
                    //this actually only needed for the crash anvil check, since the crash anvil actually works without having "tag"
                    //it sets the damage (legacy data) value of the item anvil to 3 which results in the client placing it crashing
                    //not a fan of this approach, it runs a few unnecessary checks
                    for (CreativeCheck check : checks) {
                        if (check.handleCheck(clickedItem, item)) {
                            flagPacket(event, "Recursions: " + data.getRecursionCount() + " Item: " + clickedItem.getType().getName());
                            return;
                        }
                    }
                }
            }
        }
    }
}
