package dev.dominique.blockname.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin minimal requis par blockname.mixins.json.
 * Peut être étendu pour intercepter le rendu si nécessaire.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    // Actuellement vide — présent pour satisfaire la déclaration mixin.
    // Futur usage : interception du rendu 3D pour la hitbox debug.
}
