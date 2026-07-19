package com.cranky-dev.chatscavengerhunt

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationFactory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class ScavengerHunt : JavaPlugin() {

    var isHuntActive = false
    var targetPhrase = ""
    var winnersCount = 0
    val hints = mutableListOf<String>()
    
    val playerGuesses = HashMap<UUID, Int>()
    val winners = HashSet<UUID>()
    private val activeTasks = mutableListOf<BukkitTask>()

    lateinit var conversationFactory: ConversationFactory
        private set

    // Clean, modern utility to turn classic & codes into rich text colors
    fun color(text: String): Component {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text)
    }

    override fun onEnable() {
        this.conversationFactory = ConversationFactory(this)
            .withModality(true)
            .withFirstPrompt(HuntSetupPrompt(this))
            .withEscapeSequence("cancel")
            .thatExcludesNonPlayersWithMessage("Only players can use the setup wizard.")

        val commandHandler = HuntCommands(this)
        getCommand("guess")?.setExecutor(commandHandler)
        getCommand("ahunt")?.setExecutor(commandHandler)

        server.pluginManager.registerEvents(HuntGuiListener(this), this)
        logger.info("ScavengerHunt successfully initialized!")
    }

    override fun onDisable() {
        cancelAllTimers()
    }

    fun startHuntFromWizard(phrase: String, h1: String, h2: String, h3: String) {
        cancelAllTimers()
        playerGuesses.clear()
        winners.clear()
        hints.clear()

        targetPhrase = phrase
        hints.addAll(listOf(h1, h2, h3))
        winnersCount = 0
        isHuntActive = true

        val startTitle = Title.title(color("&6&lSCAVENGER HUNT"), color("&eThe Event is Live at Spawn!"))
        for (p in Bukkit.getOnlinePlayers()) { p.showTitle(startTitle) }

        Bukkit.broadcast(color("&6&l[SERVER] &eA new Scavenger Hunt has started! Find the hidden phrase and use &b/guess <phrase>"))
        Bukkit.broadcast(color("&4&l[SERVER] &cWARNING: You only get exactly 3 GUESSES total. Use them wisely!"))

        scheduleHint(1, hints[0], 5 * 60 * 20L)
        scheduleHint(2, hints[1], 10 * 60 * 20L)
        scheduleHint(3, hints[2], 15 * 60 * 20L)
    }

    fun stopHuntManually() {
        isHuntActive = false
        cancelAllTimers()
    }

    private fun scheduleHint(hintNum: Int, msg: String, delay: Long) {
        val task = Bukkit.getScheduler().runTaskLater(this, Runnable {
            if (isHuntActive) {
                val titleText = if (hintNum == 3) "&4&lFINAL HINT" else "&6&lNEW HINT DROPPED"
                val hintTitle = Title.title(color(titleText), color("&eCheck chat for details!"))
                for (p in Bukkit.getOnlinePlayers()) { p.showTitle(hintTitle) }
                
                Bukkit.broadcast(color("&6&l[SERVER HUNT HINT #$hintNum] &r$msg"))
            }
        }, delay)
        activeTasks.add(task)
    }

    fun cancelAllTimers() {
        activeTasks.forEach { it.cancel() }
        activeTasks.clear()
    }
}

