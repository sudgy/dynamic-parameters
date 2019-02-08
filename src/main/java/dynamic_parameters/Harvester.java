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

public class Harvester extends WindowAdapter implements DialogListener {
    public boolean canceled() {return M_canceled;}
    // This only sets members variables, and nothing else.
    public Harvester(String name, DParameter... params)
    {
        M_name = name;
        M_params = params;
        for (DParameter param : M_params) {
            param.set_harvester(this);
        }
    }
    // This populates the parameters, using the class c to read from prefs
    public void populate(Class<?> c)
    {
        for (int i = 0; i < M_params.length; ++i) {
            M_params[i].read_from_prefs(c, String.valueOf(i));
        }
        populate();
        if (!M_canceled) {
            for (int i = 0; i < M_params.length; ++i) {
                M_params[i].save_to_prefs(c, String.valueOf(i));
            }
        }
    }
    // This populates the parameters without reading from any prefs
    public void populate()
    {
        create_dialog();
        GenericDialog gd = M_gd;
        M_gd.showDialog();
        // Because GenericDialog is modal, the dialog has now been closed.  This
        // could be because the user finished, or because the dialog had to be
        // recreated.  This if checks if the dialog had to be recreated
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
    // This function is called whenever the dialog needs to be remade for any
    // reason.
    private void create_dialog()
    {
        M_gd = new GenericDialog(M_name);
        for (DParameter param : M_params) {
            param.add_to_dialog(M_gd);
        }
        // This message is the error/warning
        // Note that it does NOT get filled in right away
        M_gd.addMessage("", Font.decode(null), Color.RED);
        M_error_label = (Label)M_gd.getMessage();
        M_gd.addDialogListener(this);
        M_gd.addWindowListener(this);
    }
    // The width of the window without errors is needed to resize the window
    // correctly when errors are present.  If an error is instantly present it
    // needs to be shown instantly, though.  This function gets the width before
    // between the creation of the window and setting of the error.
    @Override
    public void windowOpened(WindowEvent e)
    {
        M_gd_width = M_gd.getSize().width;
        check_for_errors();
    }
    // Overview of function:
    // Check all parameters if reconstruction is needed
    // Reconstruct if needed
    //     Because reconstructing involves showing and the dialog is modal, this
    //     function also deals with quitting the dialog as well.  It's annoying,
    //     but I can't think of a simple way around it right now.
    // Check for errors
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
    {
        if (e == null) return true;
        if (gd == M_gd) {
            boolean reconstruction_needed = false;
            for (DParameter param : M_params) {
                param.read_from_dialog(M_gd);
                if (param.reconstruction_needed()) {
                    reconstruction_needed = true;
                    param.recreate();
                }
            }
            if (reconstruction_needed) {
                M_gd.removeWindowListener(this);
                M_gd.dispose();
                create_dialog();
                M_gd.showDialog();
                if (M_gd.wasCanceled() || M_gd.wasOKed()) {
                    synchronized(this) {
                        M_finished = true;
                        notifyAll();
                    }
                }
                return false;
            }
            else return check_for_errors();
        }
        else return false;
    }
    public boolean check_for_errors()
    {
        String error = null;
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
        M_gd.getButtons()[0].setEnabled(true);
        for (DParameter param : M_params) {
            error = param.get_warning();
            if (error != null) {
                M_error_label.setText(error);
                M_error_width = M_gd.getGraphics().getFontMetrics().stringWidth(error) + 64;
                resize();
                return true;
            }
        }
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
    private DParameter[] M_params;

    private GenericDialog M_gd;
    private Label M_error_label;
    private int M_gd_width;
    private int M_error_width;

    private Lock M_finished_lock = new ReentrantLock();
    private Condition M_finished_condition = M_finished_lock.newCondition();
    private boolean M_finished = false;
    private boolean M_canceled = false;
}
