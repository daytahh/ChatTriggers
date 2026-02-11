package com.chattriggers.ctjs.launch.plugin

import com.chattriggers.ctjs.minecraft.listeners.CancellableEvent
import com.chattriggers.ctjs.minecraft.objects.message.TextComponent
import com.chattriggers.ctjs.minecraft.wrappers.inventory.Item
import com.chattriggers.ctjs.triggers.TriggerType
import com.chattriggers.ctjs.utils.kotlin.MCITextComponent
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock.Companion.iReturn
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock.Companion.methodReturn
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraft.client.gui.GuiScreen
import net.minecraft.item.ItemStack
import org.lwjgl.input.Keyboard

fun injectGuiScreen() {
    injectHandleKeyboardInput()
    injectMouseClick()
    injectMouseDrag()
    injectRenderTooltip()
    injectPreBackground()
}

fun injectHandleKeyboardInput() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleKeyboardInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "keyTyped",
                "(CI)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146282_l" to "handleKeyboardInput",
        "func_73869_a" to "keyTyped"
    )

    codeBlock {
        val local0 = shadowLocal<GuiScreen>()

        code {
            val event = CancellableEvent()
            TriggerType.GuiKey.triggerAll(
                Keyboard.getEventCharacter(),
                Keyboard.getEventKey(),
                local0,
                event
            )

            if (event.isCancelled())
                methodReturn()
        }
    }
}

fun injectMouseClick() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleMouseInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "mouseClicked",
                "(III)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146274_d" to "handleMouseInput",
        "func_73864_a" to "mouseClicked"
    )

    codeBlock {
        val local0 = shadowLocal<GuiScreen>()
        val local1 = shadowLocal<Int>()
        val local2 = shadowLocal<Int>()
        val local3 = shadowLocal<Int>()

        code {
            val event = CancellableEvent()
            TriggerType.GuiMouseClick.triggerAll(local1, local2, local3, local0, event)
            if (event.isCancelled())
                methodReturn()
        }
    }
}

fun injectMouseDrag() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleMouseInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "mouseClickMove",
                "(IIIJ)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146274_d" to "handleMouseInput",
        "func_146273_a" to "mouseClickMove"
    )

    codeBlock {
        val local0 = shadowLocal<GuiScreen>()
        val local1 = shadowLocal<Int>()
        val local2 = shadowLocal<Int>()
        val local3 = shadowLocal<Int>()

        code {
            val event = CancellableEvent()
            TriggerType.GuiMouseDrag.triggerAll(local1, local2, local3, local0, event)
            if (event.isCancelled())
                methodReturn()
        }
    }
}

fun injectRenderTooltip() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "renderToolTip"
    methodDesc = "(L$ITEM_STACK;II)V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                ITEM_STACK,
                "getTooltip",
                "(L$ENTITY_PLAYER;Z)Ljava/util/List;"
            )
        ),
        before = false,
        shift = 1
    )

    methodMaps = mapOf(
        "func_146285_a" to "renderToolTip",
        "func_82840_a" to "getTooltip"
    )

    codeBlock {
        val local1 = shadowLocal<ItemStack>()
        val local4 = shadowLocal<List<String>>()

        code {
            val event = CancellableEvent()
            TriggerType.Tooltip.triggerAll(local4, Item(local1), event)
            if (event.isCancelled())
                methodReturn()
        }
    }
}

fun injectPreBackground() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "drawDefaultBackground"
    methodDesc = "()V"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_146276_q_" to "drawDefaultBackground")

    codeBlock {
        val local0 = shadowLocal<GuiScreen>()
        code {
            val event = CancellableEvent()
            TriggerType.GuiDrawBackground.triggerAll(local0, event)
            if (event.isCancelled())
                methodReturn()
        }
    }
}
