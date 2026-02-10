package com.chattriggers.ctjs.launch.plugin

import com.chattriggers.ctjs.minecraft.listeners.CancellableEvent
import com.chattriggers.ctjs.minecraft.wrappers.inventory.Slot
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.kotlin.MCSlot
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock.Companion.asm
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock.Companion.methodReturn
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager

fun injectGuiContainer() {
    injectDrawSlot()
}

fun injectDrawSlot() = inject {
    className = "net/minecraft/client/gui/inventory/GuiContainer"
    methodName = "drawSlot"
    methodDesc = "(Lnet/minecraft/inventory/Slot;)V"

    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf(
        "func_146977_a" to "drawSlot",
    )

    codeBlock {

        val local0 = shadowLocal<GuiContainer>()
        val local1 = shadowLocal<MCSlot>()

        code {
            val event = CancellableEvent()

            GlStateManager.pushMatrix()
            TriggerType.RenderSlot.triggerAll(Slot(local1), local0, event)
            GlStateManager.popMatrix()

            if (event.isCancelled()) {
                methodReturn()
            }
        }
    }
}