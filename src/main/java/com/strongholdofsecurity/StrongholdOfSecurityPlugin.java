/*
 * Copyright (c) 2022, Severi K <severikupari1@gmail.com>
 * Copyright (c) 2019, FlaxOnEm <flax.on.em@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.strongholdofsecurity;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
@PluginDescriptor(
        name = "Stronghold of Security Helper",
        enabledByDefault = false
)
public class StrongholdOfSecurityPlugin extends Plugin {
    private static final Color ANSWER_COLOR = new Color(0, 19, 230);

    @Inject
    private Client client;

    private boolean isNPCDialogueOpen;
    private boolean isNPCDialogOptionOpen;
    private String question;

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        switch (widgetLoaded.getGroupId()) {
            case WidgetID.DIALOG_NPC_GROUP_ID:
                isNPCDialogueOpen = true;
                break;
            case WidgetID.DIALOG_OPTION_GROUP_ID:
                isNPCDialogOptionOpen = true;
                break;
        }
    }

    @Subscribe
    public void onClientTick(ClientTick t) {
        if (isNPCDialogueOpen) {
            isNPCDialogueOpen = false;
            onNPCDialogue();
        }
        if (isNPCDialogOptionOpen) {
            isNPCDialogOptionOpen = false;
            onNPCOption();
        }
    }

    private void onNPCDialogue() {
        final Widget debugWidget = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
        if (debugWidget != null) {
			final String npcText = debugWidget.getText();
			if (SecurityAnswers.QUESTION_ANSWER_MAP.containsKey(npcText)) {
				question = npcText;
			}
        }
    }

    private void onNPCOption() {
        if (question != null) {
			final Widget optionsWidget = client.getWidget(WidgetInfo.DIALOG_OPTION);
			if (optionsWidget != null) {
				final Widget[] widgets = optionsWidget.getParent().getChildren();
				if (widgets != null) {
					final Widget answerWidget = SecurityAnswers.findMatchingWidgetForQuestion(question, widgets);
					// Reset question to be null because we found answer
					question = null;
					if (answerWidget != null) {
						// Appends index of question before answer text
						final String answerText = String.format("(%d) %s", answerWidget.getIndex(), answerWidget.getText());
						// Set answer text with wanted color
						answerWidget.setText(ColorUtil.wrapWithColorTag(answerText, ANSWER_COLOR));
					}
				}
			}
		}
    }
}
