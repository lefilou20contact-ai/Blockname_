package dev.dominique.blockname.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

/**
 * Intégration ModMenu — affiche un écran de configuration dédié.
 * Nécessite que ModMenu soit installé (dépendance optionnelle).
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new BlockNameConfigScreen(parent);
    }
}
