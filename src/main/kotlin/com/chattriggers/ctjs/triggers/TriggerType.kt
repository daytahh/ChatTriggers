package com.chattriggers.ctjs.triggers

import com.chattriggers.ctjs.engine.module.ModuleManager

enum class TriggerType {
    // client
    Chat,
    ActionBar,
    Tick,
    Step,
    GameUnload,
    GameLoad,
    Clicked,
    Dragged,
    GuiOpened,
    PickupItem,
    DropItem,
    MessageSent,
    Tooltip,
    PlayerInteract,
    HitBlock,
    GuiRender,
    GuiKey,
    GuiMouseClick,
    PacketSent,
    PacketReceived,
    ServerConnect,
    ServerDisconnect,
    GuiClosed,
    RenderSlot,
    GuiDrawBackground,

    // rendering
    RenderWorld,
    RenderOverlay,
    RenderPortal,
    RenderHelmet,
    RenderScoreboard,
    RenderEntity,
    PostGuiRender,
    RenderItemIntoGui,
    RenderItemOverlayIntoGui,
    PostRenderEntity,
    RenderTileEntity,
    PostRenderTileEntity,

    // world
    PlayerJoin,
    PlayerLeave,
    SoundPlay,
    WorldLoad,
    WorldUnload,
    BlockBreak,
    SpawnParticle,
    EntityDeath,

    // misc
    Forge,
    Command,
    Other;

    fun triggerAll(vararg args: Any?) {
        ModuleManager.trigger(this, args)
    }
}
