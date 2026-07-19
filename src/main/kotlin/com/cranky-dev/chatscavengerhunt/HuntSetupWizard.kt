package com.cranky-dev.chatscavengerhunt 

import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt

class HuntSetupPrompt(private val plugin: ScavengerHunt) : StringPrompt() {
    override fun getPromptText(context: ConversationContext): String = "§6[Setup] §eType the target phrase (or type 'cancel' to exit):"
    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        if (input == null || input.equals("cancel", ignoreCase = true)) return Prompt.END_OF_CONVERSATION
        context.setSessionData("phrase", input)
        return HintOnePrompt(plugin)
    }
}

class HintOnePrompt(private val plugin: ScavengerHunt) : StringPrompt() {
    override fun getPromptText(context: ConversationContext): String = "§6[Setup] §eType Hint #1 (Runs at 5 mins):"
    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        if (input == null) return Prompt.END_OF_CONVERSATION
        context.setSessionData("hint1", input)
        return HintTwoPrompt(plugin)
    }
}

class HintTwoPrompt(private val plugin: ScavengerHunt) : StringPrompt() {
    override fun getPromptText(context: ConversationContext): String = "§6[Setup] §eType Hint #2 (Runs at 10 mins):"
    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        if (input == null) return Prompt.END_OF_CONVERSATION
        context.setSessionData("hint2", input)
        return HintThreePrompt(plugin)
    }
}

class HintThreePrompt(private val plugin: ScavengerHunt) : StringPrompt() {
    override fun getPromptText(context: ConversationContext): String = "§6[Setup] §eType Hint #3 (Runs at 15 mins):"
    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        if (input == null) return Prompt.END_OF_CONVERSATION
        
        val phrase = context.getSessionData("phrase") as String
        val h1 = context.getSessionData("hint1") as String
        val h2 = context.getSessionData("hint2") as String
        
        plugin.startHuntFromWizard(phrase, h1, h2, input)
        context.forWhom.sendRawMessage("§a§l[!] Scavenger hunt initialized and broadcasted successfully!")
        return Prompt.END_OF_CONVERSATION
    }
}
