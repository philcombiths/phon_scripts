/*
*
* Find and Replace in Project
*
* Author: Philip Combiths
* Created: 2024-02-10
* Modified: 2024-02-10
*/
import javax.swing.JOptionPane

import ca.phon.ipa.IPATranscript
import ca.phon.project.Project
import ca.phon.session.Session
import ca.phon.session.Record
import ca.phon.app.session.editor.search.FindManager
// import ca.phon.session.editor.search.FindManager
// import ca.phon.session.editor.search.*



import ca.phon.app.log.BufferPanel
import ca.phon.app.project.ProjectWindow
import ca.phon.project.Project
import ca.phon.session.*
import ca.phon.ui.nativedialogs.*
import ca.phon.ui.toast.ToastFactory

import ca.phon.app.session.SessionSelector
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



// Method
public void findReplace(Session session, String tierName) {
    def findManager = new FindManager(session)
    def searchString = "find_this_string"
    def replaceString = "replace_with_this_string"
    
    // Perform the find and replace operation
    def result = findManager.findAndReplace(searchString, replaceString)

    // Process the result
    println "Find and replace result: $result"

    // // Track modified records
    // // int recordsModifiedInSession = 0
    // session.records.each { Record record ->
    //     Tier<?> tier = record.getTier(tierName)
    //     if(tier != null) {
    //         tier.forEach { Object value ->
    //             if(value instanceof IPATranscript) {
    //                 IPATranscript ipa = (IPATranscript) value
    //                 String original = ipa.text
    //                 String modified = original.replaceAll(findPhonex, replacePhonex)
    //                 if(!original.equals(modified)) {
    //                     ipa.text = modified
    //                     // recordsModifiedInSession++
    //                 }
    //             }
    //         }
    //     }
    // }



}








// Create SessionSelectorDialog
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

        final DialogHeader header = new DialogHeader("Find and Replace in Project",
            "Find and replace in all selected sessions.");
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

/* main */
// When script is run from open project window...
if(window instanceof ProjectWindow) {
    SessionSelectorDialog frame = new SessionSelectorDialog(window.project, "Find and Replace in Project", out);
    frame.pack();
    frame.setModal(true);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(window);
    frame.setVisible(true);
    // .. frame is modal
    // Session Selector window is closed...
    if(!frame.wasCanceled) {
        // Input parameters
        def String findPhonex = JOptionPane.showInputDialog("Enter Phonex string to find:")
        def String replacePhonex = JOptionPane.showInputDialog("Enter Phonex string to replace with:")
        def String tierName = JOptionPane.showInputDialog("Enter tier to search (e.g., IPA Target):")

        // Validate input
        if(findPhonex == null || replacePhonex == null || tierName == null) {
            ToastFactory.makeToast("Operation cancelled or invalid input.").start(projectWindow)
            return
        }

        // int totalSessionsModified = 0
        // int totalRecordsModified = 0


        // For each selected session in project...
        for(SessionPath sp:frame.sessionSelector.getSelectedSessions()) {
            // Open session
            try {
                Session session = window.project.openSession(sp.getCorpus(), sp.getSession());
                // Perform action
                findReplace(session, tierName);
                // Save session
                def writeLock = window.project.getSessionWriteLock(session);
                window.project.saveSession(session, writeLock);
                window.project.releaseSessionWriteLock(session, writeLock);
                // println "Modified $recordsModifiedInSession records in session: ${sessionPath}";
            } catch (IOException e) {
                out.println(e.getLocalizedMessage());
                e.printStackTrace(out);
            }
			out.println("Unblind validated IPA Target added/overwritten to session " + sp);
        }
    }
}
        // if(recordsModifiedInSession > 0) {
        //     totalSessionsModified++
        //     totalRecordsModified += recordsModifiedInSession
        //     project.saveSession(sessionPath, session)
        //     println "Modified $recordsModifiedInSession records in session: ${sessionPath}"
        // }

// println "Total sessions modified: $totalSessionsModified"
// println "Total records modified: $totalRecordsModified"




/* main() */
// Access the current project


// ProjectWindow projectWindow = CommonModuleFrame.getCurrentFrame().getExtension(ProjectWindow.class)
//def project = window.project

// // Prompt user for input
// String findPhonex = JOptionPane.showInputDialog("Enter Phonex string to find:")
// String replacePhonex = JOptionPane.showInputDialog("Enter Phonex string to replace with:")
// String tierName = JOptionPane.showInputDialog("Enter tier to search (e.g., IPA Target):")

// // Validate input
// if(findPhonex == null || replacePhonex == null || tierName == null) {
//     ToastFactory.makeToast("Operation cancelled or invalid input.").start(projectWindow)
//     return
// }

// int totalSessionsModified = 0
// int totalRecordsModified = 0

// // Iterate over each session
// project.sessions.each { SessionPath sessionPath ->
//     Session session = project.getSession(sessionPath)
//     int recordsModifiedInSession = 0

//     session.records.each { Record record ->
//         Tier<?> tier = record.getTier(tierName)
//         if(tier != null) {
//             tier.forEach { Object value ->
//                 if(value instanceof IPATranscript) {
//                     IPATranscript ipa = (IPATranscript) value
//                     String original = ipa.text
//                     String modified = original.replaceAll(findPhonex, replacePhonex)
//                     if(!original.equals(modified)) {
//                         ipa.text = modified
//                         recordsModifiedInSession++
//                     }
//                 }
//             }
//         }
//     }

//     if(recordsModifiedInSession > 0) {
//         totalSessionsModified++
//         totalRecordsModified += recordsModifiedInSession
//         project.saveSession(sessionPath, session)
//         println "Modified $recordsModifiedInSession records in session: ${sessionPath}"
//     }
// }

// println "Total sessions modified: $totalSessionsModified"
// println "Total records modified: $totalRecordsModified"