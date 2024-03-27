# XP World Border

A plugin that combines every player's experience level with the size of the world border!

If the sum of all player's levels on the server is 15, the world border size will increase or decrease to 15 automatically.

How long can you and your friends survive?

# Current Features

- All world borders across all dimensions and worlds increase and decrease according to the constantly updating levels of the players on the server
- If a player leaves the game while outside the border in an attempt to evade death, they will be killed upon server rejoin!
- If a player leaves the game inside the border, but the border shrinks while they are gone, they will be teleported to the closest location that is inside the border (unless they left while the border was decreasing)
- The world border increasing and decreasing makes sound! There is a command to toggle either the increasing sound, decreasing sound, or both! Using */togglesound*. You can also use this to enable/disable all other plugin sounds.
- If a player leaves the world border, a countdown will start. Once the countdown reaches 0, the player will die (or explode)
- Special items can be crafted that allow players to be outside the world border for a longer period of time!

# Configuration

Configuration is a big part of this plugin! Almost every feature is customizable, including:

- Plugin messages
- What level new players start at
- The speed of the world border increasing/decreasing
- Whether or not players are teleported inside the border after the border is shrunk while they're offline
- Whether or not players die after leaving while outside the border
- Whether the border updates when a player leaves (if not, levels are stored persistently)
- Whether the levels stored persistently are wiped after some time
- How long does a player have to be offline for their levels to be wiped and the border to be updated
- Whether or not players should be notified when an offline player's levels are wiped
- Whether or not the death message of every player should include how many levels they had
- Whether or not custom death messages should be added
- Whether or not the number of levels each player has should be displayed in the player list
- How long the countdown should last once a player leaves the world border (or if there should be a countdown at all)
- Whether or not special items that multiply the countdown time should be craftable

# Upcoming Features
None currently!

# Known Issues
None currently!
