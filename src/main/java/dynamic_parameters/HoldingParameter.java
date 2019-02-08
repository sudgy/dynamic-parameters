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

import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

import ij.gui.GenericDialog;

public abstract class HoldingParameter<T> extends AbstractDParameter<T> {
    @Override public void add_to_dialog(GenericDialog gd)
    {
        for (DParameter<?> param : M_params) {
            param.add_to_dialog(gd);
        }
    }
    @Override public void read_from_dialog(GenericDialog gd)
    {
        for (DParameter<?> param : M_params) {
            param.read_from_dialog(gd);
        }
    }
    @Override public void save_to_prefs(Class<?> c, String name)
    {
        for (int i = 0; i < M_params.size(); ++i) {
            M_params.get(i).save_to_prefs(c, name + "_" + String.valueOf(i) + "_");
        }
    }
    @Override public void read_from_prefs(Class<?> c, String name)
    {
        for (int i = 0; i < M_params.size(); ++i) {
            M_params.get(i).read_from_prefs(c, name + "_" + String.valueOf(i) + "_");
        }
    }
    @Override public boolean reconstruction_needed()
    {
        for (DParameter<?> param : M_params) {
            if (param.reconstruction_needed()) return true;
        }
        return false;
    }
    @Override public void recreate()
    {
        for (DParameter<?> param : M_params) {
            if (param.reconstruction_needed()) param.recreate();
        }
    }
    @Override public int width()
    {
        int result = 0;
        for (DParameter<?> param : M_params) {
            result = Math.max(result, param.width());
        }
        return result;
    }
    @Override public String get_error()
    {
        String result = null;
        for (DParameter<?> param : M_params) {
            result = param.get_error();
            if (result != null) break;
        }
        return result;
    }
    @Override public String get_warning()
    {
        String result = super.get_warning();
        if (result == null) {
            for (DParameter<?> param : M_params) {
                result = param.get_warning();
                if (result != null) break;
            }
        }
        return result;
    }
    @Override public boolean invalid()
    {
        for (DParameter<?> param : M_params) {
            if (param.invalid()) return true;
        }
        return false;
    }
    @Override public void set_harvester(Harvester h)
    {
        for (DParameter<?> param : M_params) {
            param.set_harvester(h);
        }
    }

    protected final <T extends DParameter<?>> T add_parameter(Class<T> cls, Object... args)
    {
        Class<?>[] args_c = new Class<?>[args.length];
        if (args.length != 0) {
            for (int i = 0; i < args.length; ++i) {
                args_c[i] = args[i].getClass();
            }
        }
        try {
            T result;
            if (args.length == 0) result = cls.newInstance();
            else result = cls.getConstructor(args_c).newInstance(args);
            result.setContext(context());
            result.initialize();
            M_params.add(result);
            return result;
        }
        catch (NoSuchMethodException e) {throw new RuntimeException(e);}
        catch (InstantiationException e) {throw new RuntimeException(e);}
        catch (IllegalAccessException e) {throw new RuntimeException(e);}
        catch (InvocationTargetException e) {throw new RuntimeException(e);}
    }
    protected final boolean remove_parameter(DParameter<?> param)
        {return M_params.remove(param);}
    protected final DParameter<?> remove_parameter(int index)
        {return M_params.remove(index);}
    protected final void clear_parameters()
        {M_params.clear();}
    private ArrayList<DParameter<?>> M_params = new ArrayList<DParameter<?>>();
}
