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
import ca.phon.session.editor.search.*

import ca.phon.app.log.BufferPanel
import ca.phon.app.project.ProjectWindow
import ca.phon.project.Project
import ca.phon.session.*
import ca.phon.ui.nativedialogs.*
import ca.phon.ui.toast.ToastFactory


/* main() */
// Access the current project


// ProjectWindow projectWindow = CommonModuleFrame.getCurrentFrame().getExtension(ProjectWindow.class)
Project project = window.project

// Prompt user for input
String findPhonex = JOptionPane.showInputDialog("Enter Phonex string to find:")
String replacePhonex = JOptionPane.showInputDialog("Enter Phonex string to replace with:")
String tierName = JOptionPane.showInputDialog("Enter tier to search (e.g., IPA Target):")

// Validate input
if(findPhonex == null || replacePhonex == null || tierName == null) {
    ToastFactory.makeToast("Operation cancelled or invalid input.").start(projectWindow)
    return
}

int totalSessionsModified = 0
int totalRecordsModified = 0

// Iterate over each session
project.sessions.each { SessionPath sessionPath ->
    Session session = project.getSession(sessionPath)
    int recordsModifiedInSession = 0

    session.records.each { Record record ->
        Tier<?> tier = record.getTier(tierName)
        if(tier != null) {
            tier.forEach { Object value ->
                if(value instanceof IPATranscript) {
                    IPATranscript ipa = (IPATranscript) value
                    String original = ipa.text
                    String modified = original.replaceAll(findPhonex, replacePhonex)
                    if(!original.equals(modified)) {
                        ipa.text = modified
                        recordsModifiedInSession++
                    }
                }
            }
        }
    }

    if(recordsModifiedInSession > 0) {
        totalSessionsModified++
        totalRecordsModified += recordsModifiedInSession
        project.saveSession(sessionPath, session)
        println "Modified $recordsModifiedInSession records in session: ${sessionPath}"
    }
}

println "Total sessions modified: $totalSessionsModified"
println "Total records modified: $totalRecordsModified"