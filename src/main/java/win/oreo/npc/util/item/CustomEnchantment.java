package win.oreo.npc.util.item;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchantment {
    public static final Enchantment PRICE = new EnchantmentWrapper(101, "TEST", Integer.MAX_VALUE);

    public static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(PRICE);

        if (!registered) registerEnchantment(PRICE);
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            e.printStackTrace();
        }
        if (registered) {

        }
    }
}
