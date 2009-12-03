/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcheype.pasteasy.netbeans;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.prefs.Preferences;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class PastEasyAction extends CookieAction {

    public static final String PASTEASY_PREF = "com.jcheype.pasteasy.url";

    private String pasteCode(String language, String data, int lineNumber, String urlString) {
        StringBuilder sb = new StringBuilder();
        try {
            // Construct data
            String buffer = URLEncoder.encode("language", "UTF-8") + "=" + URLEncoder.encode(language, "UTF-8");
            buffer += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");
            buffer += "&" + URLEncoder.encode("lineNumber", "UTF-8") + "=" + URLEncoder.encode("" + lineNumber, "UTF-8");

            // Send data
            URL url = new URL(urlString + "/add.do");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(buffer);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
        }
        return urlString + "/show.do?id=" + sb.toString();
    }

    private void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, new ClipboardOwner() {

            public void lostOwnership(Clipboard clipboard, Transferable transferable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    public int getLineNumber(JEditorPane component, int pos) {
        int posLine;
        int y = 0;

        try {
            Rectangle caretCoords = component.modelToView(pos);
            y = (int) caretCoords.getY();
        } catch (BadLocationException ex) {
        }

        int lineHeight = component.getFontMetrics(component.getFont()).getHeight();
        posLine = (y / lineHeight) + 1;
        return posLine;
    }

    protected void performAction(Node[] activatedNodes) {
        try {
            EditorCookie editorCookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
            StyledDocument doc = editorCookie.getDocument();
            JEditorPane[] panes = editorCookie.getOpenedPanes();
            String title  = (String) doc.getProperty(StyledDocument.TitleProperty);
            String language = "java";
            if(title != null){
                int pos = title.lastIndexOf('.');
                if(pos>0){
                    language = title.substring(pos+1);
                }
            }
            String data;
            int lineNumber=1;
            if (panes.length > 0 && panes[0].getSelectedText() != null && panes[0].getSelectedText().trim().length() > 0) {
                 data = panes[0].getSelectedText();
                 lineNumber = getLineNumber(panes[0], panes[0].getSelectionStart());
            }
            else{
                data = doc.getText(0, doc.getLength());
            }
            String url = Preferences.userRoot().get(PASTEASY_PREF, "http://localhost:8080/");
            String msg = pasteCode(language, data, lineNumber, url);
            setClipboardContents(msg);
            int msgType = NotifyDescriptor.INFORMATION_MESSAGE;
            NotifyDescriptor d = new NotifyDescriptor.Message(msg, msgType);
            DialogDisplayer.getDefault().notify(d);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(PastEasyAction.class, "CTL_PastEasyAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

