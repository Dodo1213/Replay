package net.giantgames.replay.serialize;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SerializeItemStack implements Serializable {

    private Material material;
    private short durability;
    private HashMap<Integer, Integer> enchantments = new HashMap<>();

    public static SerializeItemStack from(ItemStack itemStack) {
        SerializeItemStack out = new SerializeItemStack();
        out.material = itemStack.getType();
        out.durability = itemStack.getDurability();
        for(Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            out.enchantments.put(enchantment.getId(), itemStack.getEnchantmentLevel(enchantment));
        }
        return out;
    }

    public static ItemStack to(SerializeItemStack s) {
        ItemStack out = new ItemStack(s.material, 1, s.durability);
        for(Integer id : s.enchantments.keySet()) {
            out.addEnchantment(Enchantment.getById(id), s.enchantments.get(id));
        }
        return out;
    }

}
