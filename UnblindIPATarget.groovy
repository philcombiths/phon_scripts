/*
 * Copyright (C) 2022 Gregory Hedlund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Unblind IPA Target for all transcribers
 *
 * When executed from the project window a dialog to select sessions will be displayed.
 * When executed from the session editor only the current session will be modified.
 * This action is not undoable, please backup your project files before running.  You may run this script
 * multiple times on the same session (e.g., after adding/removing transcribers or modifying validated IPA Targets.)
 *
 * To make this script available for the current user, place the script into the folder:
 *  <phon_application_data>/phonshell/
 * You may access the application data folder from the window menu item 'File -> Show application data folder' in Phon.
 *
 * To make this script available for users of the project, place the script into the folder:
 *  <project_folder>/__res/phonshell/
 *
 * The script will then appear under the window menu path 'Tools -> PhonShell Scripts'.
 *
 * Note: See the bottom of this file for a change which needs to be made for Phon 3.5.0+
 */
import ca.phon.app.project.ProjectWindow
import ca.phon.app.session.SessionSelector
import ca.phon.app.session.editor.EditorEvent
import ca.phon.app.session.editor.EditorEventType
import ca.phon.app.session.editor.SessionEditor
import ca.phon.ipa.AlternativeTranscript
import ca.phon.ipa.IPATranscript
import ca.phon.project.Project
import ca.phon.session.Record
import ca.phon.session.Session
import ca.phon.session.SessionPath
import ca.phon.session.Tier
import ca.phon.session.Transcriber
import ca.phon.session.Transcribers
import ca.phon.ui.CommonModuleFrame
import ca.phon.ui.decorations.DialogHeader
import ca.phon.ui.layout.ButtonBarBuilder

import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

// This is a method/function. Changed to public to permit writing to console. Originally "static void"
public void unblindIPATargetForSession(Session session) {
    Transcribers transcribers = session.getTranscribers();
	// Output transcriber usernames
	println("*****************************************************");
	println("Blind transcribers for current session:");
	for(Transcriber transcriber:transcribers) {
	    println(transcriber.getUsername());
	}
    for(Record record:session.getRecords()) {
        // ipaTarget = validated IPA Target transcriptions
		Tier<IPATranscript> ipaTarget = record.getIPATarget();
		//println(ipaTarget);
        for(IPATranscript ipa:ipaTarget) {
            // altTranscripts = list of blind transcribers/transcriptions per record
			AlternativeTranscript altTranscripts = ipa.getExtension(AlternativeTranscript.class);
			//println(altTranscripts);
            if(altTranscripts == null) {
                altTranscripts = new AlternativeTranscript();
                ipa.putExtension(AlternativeTranscript.class, altTranscripts);
            }
            if(ipa.length() > 0) {
                for(Transcriber transcriber:transcribers) {
                    // transcriber.getUsername() = Blind transcriber usernames
					altTranscripts.put(transcriber.getUsername(), IPATranscript.parseIPATranscript(ipa.toString(true)));
                }
            }
        }
    }
}

class SessionSelectorDialog extends JDialog {

    Project project;

    SessionSelector sessionSelector;

    JButton okBtn;

    JButton cancelBtn;

    boolean wasCanceled = false;

    def out;

    public SessionSelectorDialog(Project project, String title, def out) {
        super(CommonModuleFrame.getCurrentFrame());
        setTitle(title);
        this.project = project;
        this.out = out;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        final DialogHeader header = new DialogHeader("Unblind IPA Target",
            "Unblind IPA Target for all transcribers in selected sessions");
        add(header, BorderLayout.NORTH);

        sessionSelector = new SessionSelector(project);
        sessionSelector.setPreferredSize(new Dimension(250, 0));
        add(new JScrollPane(sessionSelector), BorderLayout.CENTER);

        okBtn = new JButton("Ok");
        okBtn.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        })

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                wasCanceled = true;
                setVisible(false);
            }
        })

        def buttonBar = ButtonBarBuilder.buildOkCancelBar(okBtn, cancelBtn);
        add(buttonBar, BorderLayout.SOUTH);
    }

}

/* main() */
if(window instanceof ProjectWindow) {
    SessionSelectorDialog frame = new SessionSelectorDialog(window.project, "Unblind IPA Target", out);
    frame.pack();
    frame.setModal(true);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(window);
    frame.setVisible(true);
    // .. frame is modal
    if(!frame.wasCanceled) {
        for(SessionPath sp:frame.sessionSelector.getSelectedSessions()) {

            try {
                Session session = window.project.openSession(sp.getCorpus(), sp.getSession());
                unblindIPATargetForSession(session);

                def writeLock = window.project.getSessionWriteLock(session);
                window.project.saveSession(session, writeLock);
                window.project.releaseSessionWriteLock(session, writeLock);
            } catch (IOException e) {
                out.println(e.getLocalizedMessage());
                e.printStackTrace(out);
            }
			out.println("Unblind validated IPA Target added/overwritten to session " + sp);
        }
    }
} else if(window instanceof SessionEditor) {
    final SessionEditor editor = (SessionEditor)window;
    unblindIPATargetForSession(editor.getSession());
	// Saves before proceding
    editor.saveData();

    /* Phon 3.4.X and earlier */
    // editor.getEventManager().queueEvent(new EditorEvent(EditorEventType.RECORD_REFRESH_EVT));
    /* Phon 3.5.0 and later */
    editor.getEventManager().queueEvent(new EditorEvent<>(EditorEventType.RecordRefresh, window, null));
    out.println("Unblind validated IPA Target added/overwritten for current session");
}
out.println("*****************************************************");
out.println("Script process complete. You may close this window.");