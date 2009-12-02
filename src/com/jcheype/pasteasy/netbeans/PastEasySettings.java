/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jcheype.pasteasy.netbeans;

import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class PastEasySettings extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        Project project = activatedNodes[0].getLookup().lookup(Project.class);
        NotifyDescriptor.InputLine question;
        question = new NotifyDescriptor.InputLine("PastEasyUrl",
            "URL:",
            NotifyDescriptor.OK_CANCEL_OPTION,
            NotifyDescriptor.QUESTION_MESSAGE);
        if (DialogDisplayer.getDefault().notify(question) == NotifyDescriptor.OK_OPTION) {
            Preferences.userRoot().put(PastEasyAction.PASTEASY_PREF, question.getInputText());
        }
    }

    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    public String getName() {
        return NbBundle.getMessage(PastEasySettings.class, "CTL_PastEasySettings");
    }

    protected Class[] cookieClasses() {
        return new Class[]{Project.class};
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

