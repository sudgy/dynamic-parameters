/* Copyright (C) 2018 Portland State University
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

import ij.gui.GenericDialog;

import org.scijava.Context;
import org.scijava.NullContextException;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;

public abstract class AbstractDParameter<T> extends AbstractRichPlugin implements DParameter<T> {
    @Override public void initialize() {}
    @Override public boolean reconstruction_needed() {return false;}
    @Override public void recreate() {throw new UnsupportedOperationException();}
    @Override public int width() {return 0;}
    @Override public String get_error() {return M_error;}
    @Override public String get_warning() {return M_warning;}
    @Override public boolean invalid() {return false;}
    @Override public void set_harvester(Harvester h) {M_harvester = h;}

    protected final void set_error(String error) {M_error = error;}
    protected final void set_warning(String warning) {M_warning = warning;}
    protected final PrefService prefs() {return context().getService(PrefService.class);}
    protected Harvester M_harvester;

    private String M_error;
    private String M_warning;
}
