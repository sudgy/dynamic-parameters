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
import java.util.Map;

import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

/** This is a Scijava <a href="https://javadoc.scijava.org/SciJava/org/scijava/module/process/PreprocessorPlugin.html">Preprocessor Plugin</a>
 * that populates dynamic input parameters for a module.
 * <p>
 * It populates any Scijava <a href="https://javadoc.scijava.org/SciJava/org/scijava/plugin/Parameter.html">Parameter</a>
 * that implements the {@link DParameter} interface.  It does this by creating a
 * {@link Harvester} with all of the DParameters that it finds.  Users and
 * programmers shouldn't ever need to use this class directly.
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.LOW)
public class DynamicPreprocessor extends AbstractPreprocessorPlugin {
    /** Populate the inputs. */
    @Override
    public void process(final Module module)
    {
        final String title = module.getInfo().getTitle();
        final Iterable<ModuleItem<?>> inputs = module.getInfo().inputs();
        ArrayList<DParameter> params = new ArrayList<DParameter>();
        for (final ModuleItem<?> item : inputs) {
            Object input = item.getValue(module);
            if (input instanceof DParameter) {
                DParameter param = (DParameter)input;
                if (param.invalid()) {
                    cancel(param.getError());
                    return;
                }
                P_context.inject(param);
                param.initialize();
                params.add(param);
                module.resolveInput(item.getName());
            }
        }
        if (params.size() > 0) {
            Harvester h = new Harvester(title, params.toArray(new DParameter[0]));
            h.populate(module.getDelegateObject().getClass());
            if (h.canceled()) {
                cancel(null);
            }
        }
    }
    @Parameter private Context P_context;
}
