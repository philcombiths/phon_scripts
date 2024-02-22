/*
 * This script is used to find and replace text in a project.  Modify the global variables below to setup
 * the find and replace settings.
 *
 * WARNING: This script will modify the project. It is recommended to make a backup of the project before running this script.
 * This action cannot be undone.  Sessions in open editors will not be updated.
 *
 * Works with Phon 3.x, not compatible with Phon 4.x
 */
/*
 * Copyright (C) 2024 Gregory Hedlund
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

import ca.phon.app.project.DesktopProject
import ca.phon.app.session.SessionSelector
import ca.phon.app.session.editor.search.FindExpr
import ca.phon.app.session.editor.search.FindManager
import ca.phon.app.session.editor.search.SearchType
import ca.phon.project.Project
import ca.phon.session.Session
import ca.phon.session.SessionFactory
import ca.phon.session.SessionPath
import ca.phon.session.position.GroupLocation
import ca.phon.session.position.RecordLocation
import ca.phon.session.position.SessionLocation
import ca.phon.session.position.SessionRange
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
import java.lang.reflect.Array

/*
 * Find and replace settings:
 *   - FIND: the text/expression to find
 *   - REPLACE: the text/ipa to replace all occurrences of FIND
 *   - TYPE: type of search - one of 'plain', 'regex', 'phonex'
 *   - MATCH_CASE: whether to match the case of the text
 *   - TIERS: tiers to search, a comma separated list or '*' for all tiers
 *   - SHOW_SESSION_SELECTOR: show a session selector dialog, otherwise perform action on all sessions
 */
                 FIND = "hello";
              REPLACE = "world";
                 TYPE = "plain";
           MATCH_CASE = false;
                TIERS = "*";
SHOW_SESSION_SELECTOR = true;
/* End find and replace variables */

/**
 * Create a new FindManager for the given session using find and replace settings above.
 *
 * @param session
 */
def createFindManager(Session session) {
    def findExpr = new FindExpr();
    switch (TYPE) {
        case "plain":
            findExpr.setType(SearchType.PLAIN);
            break;
        case "regex":
            findExpr.setType(SearchType.REGEX);
            break;
        case "phonex":
            findExpr.setType(SearchType.PHONEX);
            break;
    }
    findExpr.setExpr(FIND);
    findExpr.setCaseSensitive(MATCH_CASE);

    // setup tier list
    def allTiers = session.tierView.collect { it.getTierName()};
    def tierList = "*" === TIERS ? allTiers : TIERS.split(",").stream().filter {allTiers.contains(it)}.toList();

    // setup find manager
    def findManager = new FindManager(session);

    // setup location for a session, start with the first possible location
    findManager.setCurrentLocation(new SessionLocation(0,
            new RecordLocation(allTiers.get(0), new GroupLocation(0, 0))));

    findManager.setAnyExpr(findExpr);
    findManager.setSearchTier(tierList);
    return findManager;
}

/**
 * Perform find and replace in the given session
 *
 * @param session
 * @param console
 */
def doFindAndReplace(Session session, PrintWriter console) {
    def findManager = createFindManager(session);
    while(findManager.getStatus() != FindManager.FindStatus.HIT_END) {
        def sessionRange = findManager.findNext();
        if(findManager.getStatus() == FindManager.FindStatus.HIT_RESULT && sessionRange != null) {
            def record = session.getRecord(sessionRange.getRecordIndex());
            def tier = record.getTier(sessionRange.getRecordRange().getTier());

            def oldValLength = tier.getGroup(sessionRange.getRecordRange().getGroupRange().getGroupIndex()).toString().length();
            def newVal = findManager.getMatchedExpr().replace(REPLACE);
            def newValLength = newVal.toString().length();
            console.println("Replacing match in " + session.getCorpus() + "/" + session.getName() + " at "
                    + sessionRange + ": " + tier.getGroup(sessionRange.getRecordRange().getGroupRange().getGroupIndex()) + " -> " + newVal);
            tier.setGroup(sessionRange.getRecordRange().getGroupRange().getGroupIndex(), newVal);

            // update find manager location if necessary, this is usually done by the session editor
            if(newValLength < oldValLength) {
                // if the new value is shorter than the old value, we need to adjust the find manager location
                // to the end of the new value
                findManager.setCurrentLocation(new SessionLocation(sessionRange.getRecordIndex(),
                        new RecordLocation(sessionRange.getRecordRange().getTier(),
                                new GroupLocation(sessionRange.getRecordRange().getGroupRange().getGroupIndex(),
                                        sessionRange.getRecordRange().getGroupRange().getRange().getEnd() - (oldValLength - newValLength))
                        )
                ));
            } else if(newValLength > oldValLength) {
                // if the new value is longer than the old value, we need to adjust the find manager location
                // to the end of the new value
                findManager.setCurrentLocation(new SessionLocation(sessionRange.getRecordIndex(),
                        new RecordLocation(sessionRange.getRecordRange().getTier(),
                                new GroupLocation(sessionRange.getRecordRange().getGroupRange().getGroupIndex(),
                                        sessionRange.getRecordRange().getGroupRange().getRange().getEnd() + (newValLength - oldValLength))
                        )
                ));
            }
        }
    }
}

// get the project from the current window
Project project = window.project;

if(project == null) {
    println("No project found");
    return;
}

def sessionPathList = new ArrayList<>();
if(SHOW_SESSION_SELECTOR) {
    SessionSelectorDialog frame = new SessionSelectorDialog(project, "Copy IPA to Blind Transcription", out);
    frame.pack();
    frame.setModal(true);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(window);
    frame.setVisible(true);
    // .. frame is modal
    if(!frame.wasCanceled) {
        sessionPathList.addAll(frame.sessionSelector.getSelectedSessions());
    } else {
        return;
    }
} else {
    project.getCorpora().forEach { corpus ->
        project.getCorpusSessions(corpus).forEach { sessionName ->
            sessionPathList.add(new SessionPath(corpus, sessionName));
        }
    }
}

// log find and replace settings
out.println("Find and replace settings:");
out.println("\tFIND: " + FIND);
out.println("\tREPLACE: " + REPLACE);
out.println("\tTYPE: " + TYPE);
out.println("\tMATCH_CASE: " + MATCH_CASE);
out.println("\tTIERS: " + TIERS);
out.println("\tSHOW_SESSION_SELECTOR: " + SHOW_SESSION_SELECTOR);

// perform find and replace on all selected sessions
for(SessionPath sp:sessionPathList) {
    out.println("Find and replace for session " + sp);
    try {
        Session session = project.openSession(sp.getCorpus(), sp.getSession());
        doFindAndReplace(session, out);

        out.println("Saving session " + sp + "\n");
        // save session
        def writeLock = project.getSessionWriteLock(session);
        project.saveSession(session, writeLock);
        project.releaseSessionWriteLock(session, writeLock);
    } catch(Exception e) {
        out.println("Error: " + e);
    }
}

/**
 * Session selector dialog for groovy scripts
 */
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

        final DialogHeader header = new DialogHeader("Copy IPA to Blind Transcription",
                "Copy data in IPA Tiers for all selected sessions");
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
