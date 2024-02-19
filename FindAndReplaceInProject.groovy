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

    // findManager.setCurrentLocation(findManager.getCurrentLocation())
    
    out.println("session.records : $findManager.session.records")
    out.println("session.name : $findManager.session.name")
    out.println("expr : $findExpr.expr")
    out.println("type : $findExpr.type")
    out.println("direction : $findManager.direction")
    out.println("lastEpr : $findManager.lastExpr")
    out.println("searchTiers : $findManager.searchTiers")
    // out.println("nextLocation: $findManager.getNextLocation()")
    out.println("tierExpr : $findManager.tierExprs")
    // out.println("findNext: $findManager.findNext()")
    // out.println("nextLocation: $findManager.getNextLocation()")


    // findManager.setCurrentLocation(findAndReplaceView.getSessionLocation()); // Set the starting location
    // out.println(findManager.findNext())
    //  out.println(findAndReplaceView.getSessionLocation())
    
    
    // findAndReplaceView.findNext(); // Perform the search
    // out.printlin(findAndReplaceView.findNext())

    // findAndReplaceView.setExpr()




    String orthographyTierName = "Orthography";
    String orthographyExpression = "h";


    // FindExpr orthographyExpr = new FindExpr(orthographyExpression);
    // findAndReplaceView.setExpr();

    
    // findAndReplaceView


    // findAndReplaceView.exprForTier(orthographyTierName).setExpr(orthographyExpr);


    // findAndReplaceEditorView.getAnyTierExp()
    
    // findAndReplaceView.replaceAll()


    // String[] searchTiers = findAndReplaceView.getSearchTiers();
    // findManager.setSearchTier(getSearchTiers());


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
}
















	// Saves before proceding
    // editor.saveData();

    /* Phon 3.4.X and earlier */
    // editor.getEventManager().queueEvent(new EditorEvent(EditorEventType.RECORD_REFRESH_EVT));
    /* Phon 3.5.0 and later */
    // editor.getEventManager().queueEvent(new EditorEvent<>(EditorEventType.RecordRefresh, window, null));




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
// // You'll need to implement a custom listener or observer to capture the changes made by the FindManager
// // This is not shown in the example, as it depends on the specifics of your scripting environment