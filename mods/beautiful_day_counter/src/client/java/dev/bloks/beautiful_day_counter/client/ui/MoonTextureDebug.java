package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Attempts to draw the system moon texture using reflection against DrawContext's
 * mapped drawTexture signature (with RenderPipeline). If it fails, returns false.
 */
final class MoonTextureDebug {
    private static final Logger LOGGER = LoggerFactory.getLogger("beautiful_day_counter:moon-debug");
    private static MethodHandle DRAW_METHOD;
    private static Class<?> PIPELINE_CLASS;

    static boolean drawSystemMoonPhase(DrawContext ctx, int x, int y, int phase, int size) {
        try {
            if (DRAW_METHOD == null || PIPELINE_CLASS == null) {
                // Discover pipeline field on DrawContext
                Field pipelineField = null;
                for (Field f : ctx.getClass().getDeclaredFields()) {
                    if (f.getType().getName().toLowerCase().contains("pipeline")) {
                        pipelineField = f; break;
                    }
                }
                if (pipelineField == null) return false;
                pipelineField.setAccessible(true);
                Object pipeline = pipelineField.get(ctx);
                if (pipeline == null) return false;
                PIPELINE_CLASS = pipeline.getClass();

                // Find a drawTexture method with signature
                // drawTexture(RenderPipeline, Identifier, int, int, float, float, int, int, int, int)
                Method target = null;
                for (Method m : ctx.getClass().getMethods()) {
                    if (!m.getName().equals("drawTexture")) continue;
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length == 10 && p[0].isAssignableFrom(PIPELINE_CLASS)
                            && p[1] == Identifier.class
                            && p[2] == int.class && p[3] == int.class
                            && p[4] == float.class && p[5] == float.class
                            && p[6] == int.class && p[7] == int.class
                            && p[8] == int.class && p[9] == int.class) {
                        target = m; break;
                    }
                }
                if (target == null) return false;
                target.setAccessible(true);
                DRAW_METHOD = MethodHandles.lookup().unreflect(target);
            }

            // Compute UV from 4x2 grid
            int texW = 256, texH = 128;
            int frameW = texW / 4;
            int frameH = texH / 2;
            int col = phase % 4;
            int row = phase / 4;
            float u = (float) (col * frameW);
            float v = (float) (row * frameH);
            Identifier moon = Identifier.of("minecraft", "textures/environment/moon_phases.png");

            Field pipelineField = null;
            for (Field f : ctx.getClass().getDeclaredFields()) {
                if (f.getType().getName().toLowerCase().contains("pipeline")) { pipelineField = f; break; }
            }
            if (pipelineField == null) return false;
            pipelineField.setAccessible(true);
            Object pipeline = pipelineField.get(ctx);
            if (pipeline == null) return false;

            DRAW_METHOD.invoke(ctx, pipeline, moon, x, y, u, v, size, size, texW, texH);
            return true;
        } catch (Throwable t) {
            LOGGER.debug("MoonTextureDebug failed: {}", t.toString());
            return false;
        }
    }
}

