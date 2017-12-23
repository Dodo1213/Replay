package net.giantgames.replay.session.action.entity;

import net.giantgames.replay.session.action.IAction;
import net.giantgames.replay.session.object.PacketEntity;
import org.bukkit.inventory.ItemStack;

public class EquipAction<E extends PacketEntity> implements IAction<E> {

    private final PacketEntity.Slot slot;
    private final int id, damage;
    private final int beforeId, beforeDamage;

    public EquipAction(PacketEntity.Slot slot, ItemStack before, ItemStack itemStack) {
        this.slot = slot;

        if (before == null) {
            this.beforeId = 0;
            this.beforeDamage = 0;
        } else {
            this.beforeId = before.getTypeId();
            this.beforeDamage = before.getDurability();
        }

        if (itemStack != null) {
            this.id = itemStack.getTypeId();
            this.damage = itemStack.getDurability();
        } else {
            this.id = 0;
            this.damage = 0;
        }
    }

    @Override
    public void apply(int velocity, PacketEntity object) {
        ItemStack itemStack = new ItemStack(velocity > 0 ? id : beforeId, 1, (short) (velocity > 0 ? damage : beforeDamage));
        object.equip(slot, itemStack);
    }
}
