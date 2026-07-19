package com.cranky-dev.chatscavengerhunt

import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.time.Duration

class HuntCommands(private val plugin: ScavengerHunt) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("guess", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("Only players can guess.")
                return true
            }
            handlePlayerGuess(sender, args)
            return true
        }

        if (command.name.equals("ahunt", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("Only players can open the GUI.")
                return true
            }
            if (!sender.hasPermission("scavengerhunt.admin")) {
                sender.sendMessage(plugin.color("&cYou do not have permission."))
                return true
            }
            openAdminGUI(sender)
            return true
        }
        return false
    }

    private fun handlePlayerGuess(player: Player, args: Array<out String>) {
        if (!plugin.isHuntActive) {
            player.sendMessage(plugin.color("&cThere is no active scavenger hunt right now!"))
            return
        }
        if (plugin.winners.contains(player.uniqueId)) {
            player.sendMessage(plugin.color("&aYou already solved this puzzle!"))
            return
        }
        val currentGuesses = plugin.playerGuesses.getOrDefault(player.uniqueId, 0)
        if (currentGuesses >= 3) {
            player.sendMessage(plugin.color("&cOut of guesses! Better luck next time!"))
            return
        }
        if (args.isEmpty()) {
            player.sendMessage(plugin.color("&eUsage: /guess <phrase>"))
            return
        }

        val fullGuess = args.joinToString(" ")
        val newGuessCount = currentGuesses + 1
        plugin.playerGuesses[player.uniqueId] = newGuessCount

        if (fullGuess.equals(plugin.targetPhrase, ignoreCase = true)) {
            plugin.winners.add(player.uniqueId)
            plugin.winnersCount++
            
            player.showTitle(Title.title(
                plugin.color("&a&lSOLVED!"),
                plugin.color("&eYou found the hidden phrase!"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofMillis(500))
            ))

            if (plugin.winnersCount <= 3) {
                Bukkit.broadcast(plugin.color("&2&l[SERVER] &a${player.name} discovered the phrase! &d&l🌟 [BONUS TIER ${plugin.winnersCount}/3]"))
            } else {
                Bukkit.broadcast(plugin.color("&2&l[SERVER] &a${player.name} discovered the phrase! &e[${plugin.winnersCount}/10 Winners]"))
            }

            if (plugin.winnersCount >= 10) {
                plugin.isHuntActive = false
                plugin.cancelAllTimers()
                Bukkit.broadcast(plugin.color("&4&l[SERVER] &cThe Scavenger Hunt is over! All 10 slots filled."))
            }
        } else {
            val remaining = 3 - newGuessCount
            if (remaining > 0) {
                player.sendMessage(plugin.color("&cIncorrect! You have &e$remaining &cguesses left."))
            } else {
                player.sendMessage(plugin.color("&cOut of guesses! Check your inventory for potatoes!"))
                player.inventory.addItem(ItemStack(Material.BAKED_POTATO, 16))
            }
        }
    }

    private fun openAdminGUI(player: Player) {
        val gui: Inventory = Bukkit.createInventory(null, 9, plugin.color("&8Hunt Control Panel"))

        val startItem = ItemStack(Material.GREEN_CONCRETE)
        val startMeta = startItem.itemMeta
        startMeta.displayName(plugin.color("&a▶ Start a Hunt"))
        startMeta.lore(listOf(plugin.color("&7Click to launch the chat configuration wizard.")))
        startItem.itemMeta = startMeta
        gui.setItem(0, startItem)

        val infoItem = ItemStack(Material.PAPER)
        val infoMeta = infoItem.itemMeta
        infoMeta.displayName(plugin.color("&bCurrent Status"))
        val statusLore = mutableListOf<net.kyori.adventure.text.Component>()
        if (plugin.isHuntActive) {
            statusLore.add(plugin.color("&7Status: &aActive"))
            statusLore.add(plugin.color("&7Target: &e${plugin.targetPhrase}"))
            statusLore.add(plugin.color("&7Winners: &e${plugin.winnersCount}/10"))
        } else {
            statusLore.add(plugin.color("&7Status: &cInactive"))
        }
        infoMeta.lore(statusLore)
        infoItem.itemMeta = infoMeta
        gui.setItem(4, infoItem)

        val stopItem = ItemStack(Material.RED_CONCRETE)
        val stopMeta = stopItem.itemMeta
        stopMeta.displayName(plugin.color("&cStop Current Hunt"))
        stopMeta.lore(listOf(plugin.color("&7Instantly freeze operations and wipe session stats.")))
        stopItem.itemMeta = stopMeta
        gui.setItem(8, stopItem)

        player.openInventory(gui)
    }
}
