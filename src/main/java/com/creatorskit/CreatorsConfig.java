package com.creatorskit;

import net.runelite.client.config.*;

@ConfigGroup("creatorssuite")
public interface CreatorsConfig extends Config
{
	@ConfigSection(
			name = "Oculus Orb",
			description = "Settings for enabling and modifying Oculus Orb mode",
			position = 0
	)
	String oculusOrbSettings = "oculusOrbSettings";

	@ConfigItem(
			keyName = "orbToggle",
			name = "Toggle Oculus Orb Mode",
			description = "Hotkey to toggle Oculus Orb mode",
			section = oculusOrbSettings,
			position = 2
	)
	default Keybind toggleOrbHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "orbSpeed",
			name = "Orb Speed",
			description = "Set the normal speed of the Oculus Orb. Unset to disable",
			section = oculusOrbSettings,
			position = 3
	)
	default int orbSpeed()
	{
		return 36;
	}

	@ConfigSection(
			name = "Scene",
			description = "Settings for setting up your scene",
			position = 4
	)
	String sceneSettings = "sceneSettings";

	@ConfigItem(
			keyName = "quickSpawn",
			name = "Quick Spawn",
			description = "Hotkey to toggle the spawn or despawn state of the selected object",
			section = sceneSettings,
			position = 5
	)
	default Keybind quickSpawnHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "quickLocation",
			name = "Quick Location",
			description = "Hotkey to set the selected object to the mouse location",
			section = sceneSettings,
			position = 6
	)
	default Keybind quickLocationHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "quickRotateCW",
			name = "Quick Rotate CW",
			description = "Hotkey to rotate the selected object by 90 degrees clockwise",
			section = sceneSettings,
			position = 7
	)
	default Keybind quickRotateCWHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "quickRotateCCW",
			name = "Quick Rotate CCW",
			description = "Hotkey to rotate the selected object by 90 degrees counter-clockwise",
			section = sceneSettings,
			position = 8
	)
	default Keybind quickRotateCCWHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "quickDuplicate",
			name = "Quick Duplicate",
			description = "Hotkey to duplicate the selected object",
			section = sceneSettings,
			position = 9
	)
	default Keybind quickDuplicate()
	{
		return Keybind.NOT_SET;
	}

	@ConfigSection(
			name = "Overlays",
			description = "Settings for enabling/disabling overlays",
			position = 10
	)
	String overlaySettings = "overlaySettings";

	@ConfigItem(
			keyName = "toggleOverlays",
			name = "Toggle Overlays Hotkey",
			description = "Hotkey to toggle all overlays. Unset to disable",
			section = overlaySettings,
			position = 11
	)
	default Keybind toggleOverlaysHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "selectOverlay",
			name = "Select Tile Overlay",
			description = "Enables an overlay that hovers over the selected tile",
			section = overlaySettings,
			position = 12
	)
	default boolean selectOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "myObjectOverlay",
			name = "My Object Overlay",
			description = "Enables an overlay for objects introduced via this plugin",
			section = overlaySettings,
			position = 13
	)
	default boolean myObjectOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "gameObjectOverlay",
			name = "Game Object Overlay",
			description = "Enables an overlay for GameObjects",
			section = overlaySettings,
			position = 14
	)
	default boolean gameObjectOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "playerOverlay",
			name = "Player Overlay",
			description = "Enables an overlay for Players",
			section = overlaySettings,
			position = 15
	)
	default boolean playerOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "npcOverlay",
			name = "NPC Overlay",
			description = "Enables an overlay for NPCs",
			section = overlaySettings,
			position = 16
	)
	default boolean npcOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "groundObjectOverlay",
			name = "Ground Object Overlay",
			description = "Enables an overlay for GroundObjects",
			section = overlaySettings,
			position = 17
	)
	default boolean groundObjectOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "wallObjectOverlay",
			name = "Wall Object Overlay",
			description = "Enables an overlay for TileObjects",
			section = overlaySettings,
			position = 18
	)
	default boolean wallObjectOverlay()
	{
		return false;
	}

	@ConfigItem(
			keyName = "decorativeObjectOverlay",
			name = "Decorative Object Overlay",
			description = "Enables an overlay for DecorativeObjects",
			section = overlaySettings,
			position = 19
	)
	default boolean decorativeObjectOverlay()
	{
		return false;
	}
}