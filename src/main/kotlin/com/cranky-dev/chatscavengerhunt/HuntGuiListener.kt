package com.cranky_dev.chatscavengerhunt

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class HuntGuiListener(private val plugin: ScavengerHunt) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title() != plugin.color("&8Hunt Control Panel")) return
        
        event.isCancelled = true // Lock the GUI items down
        
        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return

        when (clickedItem.type) {
            Material.GREEN_CONCRETE -> {
                player.closeInventory()
                plugin.conversationFactory.buildConversation(player).begin()
            }
            Material.RED_CONCRETE -> {
                player.closeInventory()
                if (!plugin.isHuntActive) {
                    player.sendMessage(plugin.color("&cThere is no running hunt to stop."))
                    return
                }
                plugin.stopHuntManually()
                Bukkit.broadcast(plugin.color("&4&l[SERVER] &cThe Scavenger Hunt has been stopped by an administrator."))
            }
            else -> {}
        }
    }
}
