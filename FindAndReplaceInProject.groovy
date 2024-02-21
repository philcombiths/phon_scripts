/*

Currently executing from an open Session window. Once working, apply iteratively over sessions in a project.
Current issue is findNext() from FindManager object is not retreiving anything

*/

import ca.phon.app.session.editor.view.find_and_replace.FindAndReplaceEditorView
import ca.phon.app.session.editor.view.record_data.FindAndReplacePanel
import ca.phon.app.session.editor.search.FindExpr
import ca.phon.app.session.editor.search.FindManager
import ca.phon.session.Session
import ca.phon.session.Tier
import ca.phon.session.SessionPath
import ca.phon.app.session.editor.SessionEditor

import ca.phon.util.Tuple;

if(window instanceof SessionEditor) {
    final SessionEditor editor = (SessionEditor)window;
    Session session = editor.getSession();
    
    // FindManager - works
    FindManager findManager = new FindManager(session);
    // FindAndReplaceEditorView
    FindAndReplaceEditorView findAndReplaceView = new FindAndReplaceEditorView(editor);

    // findAndReplaceView.getAnyTierExpr()
    tier = "IPA Target"
    FindExpr findExpr = new FindExpr("h")
    findManager.setAnyExpr(findExpr)
    // out.println("currentLocation : $findManager.getCurrentLocation()")
    def cur = findManager.getCurrentLocation()
    def next = findManager.getNextLocation()
    out.println("cur : $cur")
    out.println("next : $next")

    findManager.setCurrentLocation(findManager.getCurrentLocation())
    def cur2 = findManager.getCurrentLocation()
    out.println("cur2 : $cur2")
    
    out.println("session.records : $findManager.session.records")
    out.println("session.name : $findManager.session.name")
    out.println("expr : $findExpr.expr")
    out.println("anyExpr : $findManager.anyExpr.expr")
    out.println("type : $findExpr.type")
    out.println("direction : $findManager.direction")
    out.println("lastEpr : $findManager.lastExpr")
    out.println("searchTiers : $findManager.searchTiers")
    out.println("tierExpr : $findManager.tierExprs")
    out.println("getStatus : $findManager.findStatus")

    String expression = "$findExpr.expr";
}

    // search for next instance of findExpr
    // print instance, location, record, session information


    // out.println("nextLocation: $findManager.getNextLocation()")
    // out.println("findNext: $findManager.findNext()")

    // findManager.setCurrentLocation(findAndReplaceView.getSessionLocation()); // Set the starting location
    // out.println(findManager.findNext())
    //  out.println(findAndReplaceView.getSessionLocation())
    
    
    // findAndReplaceView.findNext(); // Perform the search
    // out.printlin(findAndReplaceView.findNext())

    // findAndReplaceView.setExpr()


/* Partial Option 2 FindAndReplace Panel */
// FindAndReplacePanel findAndReplacePanel = FindAndReplacePanel(editor)
    // FindAndReplaceEditorView findAndReplaceView = new FindAndReplaceEditorView(editor);
    
    // FindManager findManager = SessionEditor.getFindManager(session);
    // out.println(findManager)




    //FindAndReplaceEditorView findAndReplaceView = new FindAndReplaceEditorView(editor);

    // Set up the search tiers and options
    // Tier<String> anyTier = findAndReplaceView.searchTiers.get("IPA Target");
    // anyTier.setGroup(0, "h");  

    // out.println(session.getName());
    // out.println(findAndReplaceView);


/* Partial Option 1 FindAndReplaceView */
// // Create a SessionEditor
// // def sessionEditor = new SessionEditor(...) // Provide the necessary constructor arguments
//     // SessionEditor(Project project, Session session, Transcriber transcriber)

// session = project.openSession(sessionLoc.corpus, sessionLoc.session)

// // Create a FindAndReplaceEditorView
// def findAndReplaceView = new FindAndReplaceEditorView(sessionEditor)

// // Set up the search criteria
// def findExpr = findAndReplaceView.exprForTier('tierName')
// findExpr.setExpr('searchString')
// findExpr.setCaseSensitive(true)

// // Set up the replace criteria
// def replaceExpr = findAndReplaceView.exprForTier('replaceTierName')
// replaceExpr.setExpr('replaceString')

// // Perform the search and replace
// def findManager = findAndReplaceView.getFindManager()
// findManager.setSearchTier(['tierName']) // Provide the tier names to search
// findManager.setReplaceTier(['replaceTierName']) // Provide the tier name to replace
// findManager.replaceAll()

// // Capture the output
// // implement a custom listener or observer to capture the changes made by the FindManager
