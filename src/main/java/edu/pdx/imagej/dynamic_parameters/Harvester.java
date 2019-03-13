/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.dynamic_parameters;

import ij.IJ;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import ij.gui.GenericDialog;
import ij.gui.DialogListener;

/** The Harvester takes in a bunch of {@link DParameter}s and populates them
 * through a dialog.  It is used by the {@link DynamicPreprocessor} and should
 * not be created manually.
 */
public class Harvester extends WindowAdapter implements DialogListener {
    /** Checks if the user cancelled the dialog.
     *
     * @return <code>true</code> if the user cancelled the dialog */
    public boolean canceled() {return M_canceled;}
    /** Create the Harvester.
     *
     * It only sets inputs, and nothing else.
     *
     * @param name Window title to be used for the dialog.  It is usually the
     *             title of the command.
     * @param params {@link DParameter}s to populate.
     */
    public Harvester(String name, DParameter... params)
    {
        M_name = name;
        M_params = params;
        for (DParameter param : M_params) {
            param.set_harvester(this);
        }
    }
    /** Populate the parameters with preferences
     *
     * @param c The class to read from prefs with.  It is usually the class of
     *          the command itself.
     */
    public void populate(Class<?> c)
    {
        for (DParameter<?> param : M_params) {
            param.read_from_prefs(c, param.label());
            param.refresh_visibility();
        }
        populate();
        if (!M_canceled) {
            for (DParameter<?> param : M_params) {
                param.save_to_prefs(c, param.label());
            }
        }
    }
    /** Populate the parameters without preferences */
    // This populates the parameters without reading from any prefs
    public void populate()
    {
        create_dialog();
        GenericDialog gd = M_gd;
        M_gd.showDialog();
        // Because GenericDialog is modal, the dialog has now been closed.  This
        // could be because the user finished, or because the dialog had to be
        // recreated.  This if checks if the dialog had to be recreated.
        if (!gd.wasCanceled() && !gd.wasOKed()) {
            // This function shouldn't return until the user is finished.  So we
            // wait for M_finished to be true before finishing.
            synchronized(this) {
                try {while (!M_finished) wait();}
                catch (InterruptedException e) {M_canceled = true;}
            }
        }
        if (M_gd.wasCanceled()) M_canceled = true;
    }
    private void create_dialog()
    {
        M_gd = new GenericDialog(M_name);
        for (DParameter param : M_params) {
            if (param.visible()) {
                param.add_to_dialog(M_gd);
            }
        }
        // This message is the error/warning
        // Note that it does NOT get filled in right away
        M_gd.addMessage("", Font.decode(null), Color.RED);
        M_error_label = (Label)M_gd.getMessage();
        M_gd.addDialogListener(this);
        M_gd.addWindowListener(this);
    }
    /** Calculate anything needed when the window is opened. */
    @Override
    public void windowOpened(WindowEvent e)
    {
        // The width of the window without errors is needed to resize the window
        // correctly when errors are present.  If an error is instantly present
        // it needs to be shown instantly, though.  This function gets the width
        // between the creation of the window and setting of the error.
        M_gd_width = M_gd.getSize().width;
        check_for_errors();
    }
    /** React to user input.  This recreates the dialog if it is needed.
     *
     * @param gd The GenericDialog that created this event (not necessarilly
     *           M_gd).
     * @param e The event (<code>null</code> if its from pressing OK).
     * @return <code>true</code> if the dialog is in a valid state.
     */
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
    {
        // If the user pressed okay, just don't do anything
        if (e == null) return true;
        // If gd != M_gd, that means that this dialog is an old dialog closing.
        // We don't want to have anything to do with it in that case.
        if (gd == M_gd) {
            boolean reconstruction_needed = false;
            for (DParameter param : M_params) {
                param.read_from_dialog(M_gd);
                if (param.visibility_changed()) {
                    reconstruction_needed = true;
                    param.refresh_visibility();
                }
            }
            if (reconstruction_needed) {
                M_gd.removeWindowListener(this);
                M_gd.dispose();
                create_dialog();
                M_gd.showDialog();
                // Because GenericDialog is modal, the dialog has now been
                // closed.  If it was canceled or oked, our job is done and we
                // need to notify populate().
                if (M_gd.wasCanceled() || M_gd.wasOKed()) {
                    synchronized(this) {
                        M_finished = true;
                        notifyAll();
                    }
                }
                // Because we reconstructed, we are returning for an old dialog.
                // This return value doesn't really matter, but it doesn't hurt.
                return false;
            }
            // if (!reconstruction_needed)
            else return check_for_errors();
        }
        // if (gd != M_gd)
        else return false;
    }
    /** Check if there is any error or warning in the parameters.
     * <p>
     * Some change other than that found through {@link dialogItemChanged} might
     * cause an error.  So, if any {@link DParameter} needs to cause an error
     * without changing the dialog, it can call this function directly.
     *
     * @return <code>true</code> if there are no errors.  This value is used for
     *         {@link dialogItemChanged}.
     */
    public boolean check_for_errors()
    {
        String error = null;
        // Check error
        for (DParameter param : M_params) {
            error = param.get_error();
            if (error != null) {
                M_gd.getButtons()[0].setEnabled(false);
                M_error_label.setText(error);
                M_error_width = M_gd.getGraphics().getFontMetrics().stringWidth(error) + 64;
                resize();
                return false;
            }
        }
        // There is no error, we can push OK (this must be here because warnings
        // need to return before the end of the function)
        M_gd.getButtons()[0].setEnabled(true);
        // Check warning
        for (DParameter param : M_params) {
            error = param.get_warning();
            if (error != null) {
                M_error_label.setText(error);
                M_error_width = M_gd.getGraphics().getFontMetrics().stringWidth(error) + 64;
                resize();
                return true;
            }
        }
        // There are no errors or warnings
        M_error_label.setText(null);
        M_error_width = 0;
        resize();
        return true;
    }
    private void resize()
    {
        int width = M_gd_width > M_error_width ? M_gd_width : M_error_width;
        for (DParameter param : M_params) {
            int param_width = param.width();
            width = width > param_width ? width : param_width;
        }
        M_gd.setSize(width, M_gd.getSize().height);
    }
    private String M_name;
    private DParameter<?>[] M_params;

    private GenericDialog M_gd;
    private Label M_error_label;
    private int M_gd_width;
    private int M_error_width;

    private Lock M_finished_lock = new ReentrantLock();
    private Condition M_finished_condition = M_finished_lock.newCondition();
    private boolean M_finished = false;
    private boolean M_canceled = false;
}
